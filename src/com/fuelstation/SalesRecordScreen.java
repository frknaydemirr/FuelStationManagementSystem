package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.sql.*;
import java.util.Vector;

public class SalesRecordScreen extends JFrame {

    private JTable salesTable;
    private static LanguageManager lm = LanguageManager.getInstance();

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public SalesRecordScreen() {
        super(lm.getString("records.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JLabel lblHeader = new JLabel(lm.getString("records.title").toUpperCase());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLO
        salesTable = new JTable();
        styleTable(salesTable);
        loadSalesData();

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // BUTON PANELİ
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        JButton btnBack = new JButton("Back to Dashboard");
        styleButton(btnBack, SECONDARY_COLOR);
        btnBack.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            this.dispose();
        });

        JButton btnCreateSale = new JButton("New Sale");
        styleButton(btnCreateSale, SUCCESS_COLOR);
        btnCreateSale.addActionListener(e -> createNewSale());

        JButton btnUpdateSale = new JButton("Update Sale");
        styleButton(btnUpdateSale, WARNING_COLOR);
        btnUpdateSale.addActionListener(e -> updateSelectedSale());

        JButton btnDeleteSale = new JButton(lm.getString("records.deleteButton"));
        styleButton(btnDeleteSale, DANGER_COLOR);
        btnDeleteSale.addActionListener(e -> deleteSelectedSale());

        controlPanel.add(btnBack);
        controlPanel.add(btnCreateSale);
        controlPanel.add(btnUpdateSale);
        controlPanel.add(btnDeleteSale);

        mainContainer.add(controlPanel, BorderLayout.SOUTH);
        add(mainContainer);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
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
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadSalesData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT TransactionID, TransactionDate, TotalAmount, PaymentMethod FROM SalesTransactions ORDER BY TransactionDate DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) model.addColumn(metaData.getColumnLabel(i));

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) row.add(rs.getObject(i));
                model.addRow(row);
            }
            salesTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    // --- HATAYI ÇÖZEN GÜNCELLENMİŞ CREATE MANTIĞI ---
    private void createNewSale() {
        JComboBox<String> cmbEmp = new JComboBox<>();
        JComboBox<String> cmbFuel = new JComboBox<>();
        JComboBox<String> cmbPump = new JComboBox<>();
        JTextField txtLiters = new JTextField("0.00");
        JTextField txtAmount = new JTextField("0.00");
        String[] methods = {"Cash", "Credit Card", "Corporate Account", "Coupon"};
        JComboBox<String> cmbMethod = new JComboBox<>(methods);

        // Veritabanından zorunlu ID'leri çek
        fillCombo("employees", "EmployeeID", "FirstName", cmbEmp);
        fillCombo("fueltypes", "FuelTypeID", "FuelName", cmbFuel);
        fillCombo("fuelpumps", "PumpID", "PumpID", cmbPump);

        Object[] message = {
                "Employee:", cmbEmp,
                "Fuel Type:", cmbFuel,
                "Pump:", cmbPump,
                "Liters Sold:", txtLiters,
                "Total Amount:", txtAmount,
                "Payment Method:", cmbMethod
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register New Sale", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            // Tüm zorunlu alanları (EmployeeID, FuelTypeID vb.) ekliyoruz
            String sql = "INSERT INTO SalesTransactions (TransactionDate, EmployeeID, FuelTypeID, PumpID, LitersSold, TotalAmount, PaymentMethod) VALUES (NOW(), ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(cmbEmp.getSelectedItem().toString().split(" - ")[0]));
                pstmt.setInt(2, Integer.parseInt(cmbFuel.getSelectedItem().toString().split(" - ")[0]));
                pstmt.setInt(3, Integer.parseInt(cmbPump.getSelectedItem().toString().split(" - ")[0]));
                pstmt.setDouble(4, Double.parseDouble(txtLiters.getText()));
                pstmt.setDouble(5, Double.parseDouble(txtAmount.getText()));
                pstmt.setString(6, cmbMethod.getSelectedItem().toString());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Sale added!");
                loadSalesData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Creation Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void fillCombo(String table, String idCol, String nameCol, JComboBox<String> combo) {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + idCol + ", " + nameCol + " FROM " + table)) {
            while (rs.next()) combo.addItem(rs.getInt(idCol) + " - " + rs.getString(nameCol));
        } catch (SQLException ignored) {}
    }

    private void updateSelectedSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a sale!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String transactionID = salesTable.getValueAt(selectedRow, 0).toString();
        JTextField amountField = new JTextField(salesTable.getValueAt(selectedRow, 2).toString());
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Corporate Account", "Coupon"});
        methodCombo.setSelectedItem(salesTable.getValueAt(selectedRow, 3).toString());

        Object[] message = { "Transaction ID: " + transactionID, "Total Amount:", amountField, "Payment Method:", methodCombo };
        if (JOptionPane.showConfirmDialog(this, message, "Update Sale", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE SalesTransactions SET TotalAmount = ?, PaymentMethod = ? WHERE TransactionID = ?")) {
                pstmt.setDouble(1, Double.parseDouble(amountField.getText()));
                pstmt.setString(2, methodCombo.getSelectedItem().toString());
                pstmt.setLong(3, Long.parseLong(transactionID));
                pstmt.executeUpdate();
                loadSalesData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void deleteSelectedSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) return;
        String saleID = salesTable.getValueAt(selectedRow, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Delete Transaction ID: " + saleID + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM SalesTransactions WHERE TransactionID = ?")) {
                pstmt.setLong(1, Long.parseLong(saleID));
                pstmt.executeUpdate();
                loadSalesData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}