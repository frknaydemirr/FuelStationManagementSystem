package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.math.BigDecimal;

//SalesDetail
public class StoreSalesRecordScreen extends JFrame {

    private JTable detailsTable;
    private DefaultTableModel model;

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public StoreSalesRecordScreen() {
        // Pencere başlığı güncellendi
        super("Sales Detail Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER - İstediğin gibi "SALES DETAIL" olarak güncellendi
        JLabel lblHeader = new JLabel("SALES DETAIL");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLE
        detailsTable = new JTable();
        styleTable(detailsTable);
        loadDetailsData();

        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        JButton btnHome = new JButton("Dashboard");
        styleButton(btnHome, SECONDARY_COLOR);
        btnHome.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            this.dispose();
        });

        JButton btnUpdate = new JButton("Update Detail");
        styleButton(btnUpdate, WARNING_COLOR);
        btnUpdate.addActionListener(e -> updateDetail());

        JButton btnDelete = new JButton("Delete Detail");
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteDetail());

        JButton btnCreate = new JButton("Add Sale Detail");
        styleButton(btnCreate, SUCCESS_COLOR);
        btnCreate.addActionListener(e -> createDetail());

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

    private void loadDetailsData() {
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        String[] headers = {"Detail ID", "Store ID", "Product ID", "Quantity", "Price At Sale"};
        for (String h : headers) { model.addColumn(h); }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DetailID, StoreID, ProductID, Quantity, PriceAtSale FROM saledetails ORDER BY DetailID DESC")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getLong("DetailID"));
                row.add(rs.getLong("StoreID"));
                row.add(rs.getInt("ProductID"));
                row.add(rs.getInt("Quantity"));
                row.add(rs.getBigDecimal("PriceAtSale"));
                model.addRow(row);
            }
            detailsTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Load Error: " + e.getMessage());
        }
    }

    private void createDetail() {
        JTextField txtStoreId = new JTextField();
        JTextField txtProductId = new JTextField();
        JTextField txtQuantity = new JTextField();
        JTextField txtPrice = new JTextField();

        Object[] message = {
                "Store ID (Sale ID reference):", txtStoreId,
                "Product ID:", txtProductId,
                "Quantity:", txtQuantity,
                "Price At Sale:", txtPrice
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Detail", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO saledetails (StoreID, ProductID, Quantity, PriceAtSale) VALUES (?, ?, ?, ?)")) {

                pstmt.setLong(1, Long.parseLong(txtStoreId.getText()));
                pstmt.setInt(2, Integer.parseInt(txtProductId.getText()));
                pstmt.setInt(3, Integer.parseInt(txtQuantity.getText()));
                pstmt.setDouble(4, Double.parseDouble(txtPrice.getText()));

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Detail added successfully!");
                loadDetailsData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void updateDetail() {
        int selectedRow = detailsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a detail to update.");
            return;
        }

        long detailId = (long) detailsTable.getValueAt(selectedRow, 0);
        JTextField txtQuantity = new JTextField(detailsTable.getValueAt(selectedRow, 3).toString());
        JTextField txtPrice = new JTextField(detailsTable.getValueAt(selectedRow, 4).toString());

        Object[] message = {
                "Update Quantity:", txtQuantity,
                "Update Price:", txtPrice
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Detail ID: " + detailId, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE saledetails SET Quantity = ?, PriceAtSale = ? WHERE DetailID = ?")) {

                pstmt.setInt(1, Integer.parseInt(txtQuantity.getText()));
                pstmt.setDouble(2, Double.parseDouble(txtPrice.getText()));
                pstmt.setLong(3, detailId);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Detail updated successfully!");
                loadDetailsData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            }
        }
    }

    private void deleteDetail() {
        int selectedRow = detailsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a detail to delete.");
            return;
        }

        long detailId = (long) detailsTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Detail #" + detailId + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM saledetails WHERE DetailID = ?")) {
                pstmt.setLong(1, detailId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record deleted.");
                loadDetailsData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}