package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EmployeePhoneRecordScreen extends JFrame {

    private JTable phoneTable;


    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public EmployeePhoneRecordScreen() {
        super("Employee Phone Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("EMPLOYEE PHONE DIRECTORY");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        phoneTable = new JTable();
        styleTable(phoneTable);
        loadPhoneData();

        JScrollPane scrollPane = new JScrollPane(phoneTable);
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

        JButton btnUpdate = new JButton("Update Phone");
        styleButton(btnUpdate, WARNING_COLOR);
        btnUpdate.addActionListener(e -> updatePhone());

        JButton btnDelete = new JButton("Delete Record");
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> deletePhone());

        JButton btnCreate = new JButton("Add Phone Number");
        styleButton(btnCreate, SUCCESS_COLOR);
        btnCreate.addActionListener(e -> createPhone());

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

    private void loadPhoneData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT ep.PhoneID, e.FirstName, e.LastName, ep.PhoneNumber " +
                "FROM employeephones ep " +
                "JOIN employees e ON ep.EmployeeID = e.EmployeeID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            String[] headers = {"Record ID", "First Name", "Last Name", "Phone Number"};
            for (String h : headers) { model.addColumn(h); }

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("PhoneID"));
                row.add(rs.getString("FirstName"));
                row.add(rs.getString("LastName"));
                row.add(rs.getString("PhoneNumber"));
                model.addRow(row);
            }
            phoneTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void createPhone() {
        JComboBox<String> cmbEmployee = new JComboBox<>();
        JTextField txtPhone = new JTextField();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT EmployeeID, FirstName, LastName FROM employees")) {
            while (rs.next()) {
                cmbEmployee.addItem(rs.getInt("EmployeeID") + " - " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
        } catch (SQLException ignored) {}

        Object[] message = {
                "Select Employee:", cmbEmployee,
                "Phone Number:", txtPhone
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Phone Record", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String selectedEmp = cmbEmployee.getSelectedItem().toString();
            int empId = Integer.parseInt(selectedEmp.split(" - ")[0]);

            String sql = "INSERT INTO employeephones (EmployeeID, PhoneNumber) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, empId);
                pstmt.setString(2, txtPhone.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Phone number added successfully!");
                loadPhoneData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void updatePhone() {
        int selectedRow = phoneTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int phoneId = (int) phoneTable.getValueAt(selectedRow, 0);
        String currentPhone = (String) phoneTable.getValueAt(selectedRow, 3);
        String empName = phoneTable.getValueAt(selectedRow, 1) + " " + phoneTable.getValueAt(selectedRow, 2);

        JTextField txtPhone = new JTextField(currentPhone);

        Object[] message = {
                "Employee: " + empName,
                "New Phone Number:", txtPhone
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Phone Number", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE employeephones SET PhoneNumber = ? WHERE PhoneID = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtPhone.getText());
                pstmt.setInt(2, phoneId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Phone number updated successfully!");
                loadPhoneData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void deletePhone() {
        int selectedRow = phoneTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int phoneId = (int) phoneTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this phone record?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM employeephones WHERE PhoneID = ?")) {
                pstmt.setInt(1, phoneId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record deleted successfully.");
                loadPhoneData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: Deletion failed.");
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}