package com.fuelstation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FuelSaleTransactionScreen extends JFrame {

    // UI Components
    private JComboBox<String> cmbEmployee;
    private JComboBox<String> cmbFuelType;
    private JComboBox<String> cmbPump;
    private JTextField txtLitersSold;
    private JTextField txtTotalAmount;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtDiscount;
    private JButton btnCompleteSale;
    private JButton btnViewRecords;
    private static LanguageManager lm = LanguageManager.getInstance();

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Mavi
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  // Yeşil
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(45, 52, 54);
    private final Color INPUT_BORDER = new Color(210, 218, 226);

    public FuelSaleTransactionScreen() {
        super(lm.getString("sale.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Ana Konteynır
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // BAŞLIK
        JLabel lblTitle = new JLabel(lm.getString("sale.title").toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // FORM PANELİ (GridBagLayout ile daha hassas kontrol)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5); // Hücreler arası boşluk

        // Bileşenleri Ekleme
        addFormField(formPanel, lm.getString("sale.employee"), cmbEmployee = createStyledComboBox(), gbc, 0);
        loadDataFromDB("Employees", "EmployeeID", "FirstName, LastName", cmbEmployee);

        addFormField(formPanel, lm.getString("sale.fuelType"), cmbFuelType = createStyledComboBox(), gbc, 1);
        loadDataFromDB("FuelTypes", "FuelTypeID", "FuelName", cmbFuelType);

        addFormField(formPanel, lm.getString("sale.pump"), cmbPump = createStyledComboBox(), gbc, 2);
        loadDataFromDB("FuelPumps", "PumpID", "PumpID", cmbPump);

        addFormField(formPanel, lm.getString("sale.litersSold"), txtLitersSold = createStyledTextField("0.00"), gbc, 3);
        addFormField(formPanel, lm.getString("sale.totalAmount"), txtTotalAmount = createStyledTextField("0.00"), gbc, 4);

        addFormField(formPanel, lm.getString("sale.paymentMethod"), cmbPaymentMethod = createStyledComboBox(), gbc, 5);
        cmbPaymentMethod.setModel(new DefaultComboBoxModel<>(new String[]{"Cash", "Credit Card", "Corporate Account", "Coupon"}));

        addFormField(formPanel, lm.getString("sale.discount"), txtDiscount = createStyledTextField("0.00"), gbc, 6);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // BUTON PANELİ
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnCompleteSale = new JButton(lm.getString("sale.completeSale"));
        styleButton(btnCompleteSale, new Color(52, 152, 219), Color.WHITE);
        btnCompleteSale.addActionListener(this::executeFuelSaleStoredProcedure);

        btnViewRecords = new JButton(lm.getString("sale.viewRecords"));
        styleButton(btnViewRecords, SUCCESS_COLOR, Color.WHITE);
        btnViewRecords.addActionListener(e -> new SalesRecordScreen().setVisible(true));

        buttonPanel.add(btnCompleteSale);
        buttonPanel.add(btnViewRecords);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    // --- Görsel Stil Yardımcı Metotları ---

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(INPUT_BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setPreferredSize(new Dimension(250, 35));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(210, 45));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    // --- Mevcut Logic (Değişmedi) ---

    private void loadDataFromDB(String tableName, String idColumn, String displayColumns, JComboBox<String> cmb) {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT " + idColumn + ", " + displayColumns + " FROM " + tableName;
        if (conn == null) return;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String displayValue;
                if (tableName.equals("Employees")) {
                    displayValue = rs.getString("FirstName") + " " + rs.getString("LastName");
                } else if (tableName.equals("FuelTypes")) {
                    displayValue = rs.getString("FuelName");
                } else {
                    displayValue = rs.getString(idColumn);
                }
                cmb.addItem(rs.getInt(idColumn) + " - " + displayValue);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), lm.getString("app.title"), JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void executeFuelSaleStoredProcedure(ActionEvent e) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try {
            int employeeID = Integer.parseInt(((String) cmbEmployee.getSelectedItem()).split(" - ")[0]);
            int fuelTypeID = Integer.parseInt(((String) cmbFuelType.getSelectedItem()).split(" - ")[0]);
            int pumpID = Integer.parseInt(((String) cmbPump.getSelectedItem()).split(" - ")[0]);
            double litersSold = Double.parseDouble(txtLitersSold.getText());
            double totalAmount = Double.parseDouble(txtTotalAmount.getText());
            String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
            double discount = Double.parseDouble(txtDiscount.getText());

            String call = "{CALL SP_RecordFuelSale(?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement cs = conn.prepareCall(call)) {
                cs.setInt(1, employeeID);
                cs.setNull(2, java.sql.Types.INTEGER);
                cs.setInt(3, fuelTypeID);
                cs.setInt(4, pumpID);
                cs.setDouble(5, litersSold);
                cs.setDouble(6, totalAmount);
                cs.setString(7, paymentMethod);
                cs.setDouble(8, discount);
                cs.execute();
                JOptionPane.showMessageDialog(this, lm.getString("sale.success"), lm.getString("app.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), lm.getString("app.title"), JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}