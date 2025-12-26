package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class FuelTypeRecordScreen extends JFrame {

    private JTable fuelTable;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public FuelTypeRecordScreen() {
        super("Fuel Type Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("FUEL TYPE MANAGEMENT");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        fuelTable = new JTable();
        styleTable(fuelTable);
        loadFuelData();

        JScrollPane scrollPane = new JScrollPane(fuelTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        JButton btnHome = new JButton("Back to Dashboard");
        styleButton(btnHome, SECONDARY_COLOR);
        btnHome.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            this.dispose();
        });

        JButton btnUpdate = new JButton("Update Fuel");
        styleButton(btnUpdate, WARNING_COLOR);
        btnUpdate.addActionListener(e -> updateFuel());

        JButton btnDelete = new JButton("Delete Fuel");
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteFuel());

        JButton btnCreate = new JButton("Add New Fuel");
        styleButton(btnCreate, SUCCESS_COLOR);
        btnCreate.addActionListener(e -> createFuel());

        controlPanel.add(btnHome);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnDelete);
        controlPanel.add(btnCreate);

        mainContainer.add(controlPanel, BorderLayout.SOUTH);
        add(mainContainer);

        setSize(1100, 600);
        setLocationRelativeTo(null);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 40));
    }

    private void styleButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadFuelData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

    
        String sql = "SELECT FuelTypeID, FuelName, CurrentPricePerLiter, CurrentStockLiters, LastUpdated FROM fueltypes";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            String[] headers = {"ID", "Fuel Name", "Price/Liter", "Stock (Liters)", "Last Updated"};
            for (String h : headers) { model.addColumn(h); }

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("FuelTypeID"));
                row.add(rs.getString("FuelName"));
                row.add(rs.getBigDecimal("CurrentPricePerLiter"));
                row.add(rs.getBigDecimal("CurrentStockLiters"));
                row.add(rs.getTimestamp("LastUpdated"));
                model.addRow(row);
            }
            fuelTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void createFuel() {
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField();

        Object[] message = {
                "Fuel Name:", txtName,
                "Current Price per Liter:", txtPrice,
                "Initial Stock (Liters):", txtStock
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Fuel Type", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO fueltypes (FuelName, CurrentPricePerLiter, CurrentStockLiters, LastUpdated) VALUES (?, ?, ?, NOW())";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtName.getText());
                pstmt.setDouble(2, Double.parseDouble(txtPrice.getText()));
                pstmt.setDouble(3, Double.parseDouble(txtStock.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fuel type added successfully!");
                loadFuelData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void updateFuel() {
        int selectedRow = fuelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fuel type to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fuelID = (int) fuelTable.getValueAt(selectedRow, 0);
        String currentName = (String) fuelTable.getValueAt(selectedRow, 1);
        String currentPrice = fuelTable.getValueAt(selectedRow, 2).toString();
        String currentStock = fuelTable.getValueAt(selectedRow, 3).toString();

        JTextField txtName = new JTextField(currentName);
        JTextField txtPrice = new JTextField(currentPrice);
        JTextField txtStock = new JTextField(currentStock);

        Object[] message = {
                "Fuel Name:", txtName,
                "Price per Liter:", txtPrice,
                "Stock (Liters):", txtStock
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Fuel: " + currentName, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE fueltypes SET FuelName = ?, CurrentPricePerLiter = ?, CurrentStockLiters = ?, LastUpdated = NOW() WHERE FuelTypeID = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtName.getText());
                pstmt.setDouble(2, Double.parseDouble(txtPrice.getText()));
                pstmt.setDouble(3, Double.parseDouble(txtStock.getText()));
                pstmt.setInt(4, fuelID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fuel type updated successfully!");
                loadFuelData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void deleteFuel() {
        int selectedRow = fuelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a fuel type to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fuelID = (int) fuelTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this fuel type? \nWarning: This may fail if sales records are linked.", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fueltypes WHERE FuelTypeID = ?")) {
                pstmt.setInt(1, fuelID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fuel type deleted successfully.");
                loadFuelData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: You cannot delete fuel types linked to sales transactions.");
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}