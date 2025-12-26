package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

//FuelPumps
public class FuelPumpRecordScreen extends JFrame {

    private JTable pumpTable;

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public FuelPumpRecordScreen() {
        super("Fuel Pump Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JLabel lblHeader = new JLabel("FUEL PUMP MANAGEMENT");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLE
        pumpTable = new JTable();
        styleTable(pumpTable);
        loadPumpData();

        JScrollPane scrollPane = new JScrollPane(pumpTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        JButton btnHome = new JButton("Back to Dashboard");
        styleButton(btnHome, SECONDARY_COLOR);
        btnHome.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            this.dispose();
        });

        JButton btnUpdate = new JButton("Update Pump");
        styleButton(btnUpdate, WARNING_COLOR);
        btnUpdate.addActionListener(e -> updatePump());

        JButton btnDelete = new JButton("Delete Pump");
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> deletePump());

        JButton btnCreate = new JButton("Add New Pump");
        styleButton(btnCreate, SUCCESS_COLOR);
        btnCreate.addActionListener(e -> createPump());

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

    private void loadPumpData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        // DB Şemasına göre: PumpID, Status, LastMaintenanceDate
        String sql = "SELECT PumpID, Status, LastMaintenanceDate FROM fuelpumps";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            String[] headers = {"Pump ID", "Status", "Last Maintenance"};
            for (String h : headers) { model.addColumn(h); }

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("PumpID"));
                row.add(rs.getString("Status"));
                row.add(rs.getDate("LastMaintenanceDate"));
                model.addRow(row);
            }
            pumpTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void createPump() {
        String[] statusOptions = {"Active", "Maintenance", "Inactive"};
        JComboBox<String> cmbStatus = new JComboBox<>(statusOptions);

        Object[] message = {
                "Pump Status:", cmbStatus
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Fuel Pump", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            // Yeni pompa eklerken bugünün tarihini bakım tarihi olarak atıyoruz
            String sql = "INSERT INTO fuelpumps (Status, LastMaintenanceDate) VALUES (?, CURRENT_DATE)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cmbStatus.getSelectedItem().toString());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pump added successfully!");
                loadPumpData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void updatePump() {
        int selectedRow = pumpTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pump to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int pumpID = (int) pumpTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) pumpTable.getValueAt(selectedRow, 1);

        String[] statusOptions = {"Active", "Maintenance", "Inactive"};
        JComboBox<String> cmbStatus = new JComboBox<>(statusOptions);
        cmbStatus.setSelectedItem(currentStatus);

        Object[] message = {
                "Pump ID: " + pumpID,
                "Update Status:", cmbStatus,
                "Note: Update will refresh Last Maintenance Date to today."
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Pump", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE fuelpumps SET Status = ?, LastMaintenanceDate = CURRENT_DATE WHERE PumpID = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cmbStatus.getSelectedItem().toString());
                pstmt.setInt(2, pumpID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pump updated successfully!");
                loadPumpData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void deletePump() {
        int selectedRow = pumpTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a pump to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int pumpID = (int) pumpTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete Pump " + pumpID + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fuelpumps WHERE PumpID = ?")) {
                pstmt.setInt(1, pumpID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pump deleted successfully.");
                loadPumpData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: You cannot delete pumps linked to sales records.");
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}