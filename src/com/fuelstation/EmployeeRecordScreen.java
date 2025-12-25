package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EmployeeRecordScreen extends JFrame {

    private JTable employeeTable;

    // Modern Color Palette
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public EmployeeRecordScreen() {
        super("Employee Management Records");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JLabel lblHeader = new JLabel("EMPLOYEE MANAGEMENT SYSTEM");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLE
        employeeTable = new JTable();
        styleTable(employeeTable);
        loadEmployeeData();

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        // --- GÜNCELLENDİ: Dashboard'a Dönüş Butonu ---
        JButton btnBack = new JButton("Back to Dashboard");
        styleButton(btnBack, SECONDARY_COLOR);
        btnBack.addActionListener(e -> {
            new DashboardScreen().setVisible(true); // Dashboard'a yönlendirir
            this.dispose(); // Mevcut ekranı kapatır
        });

        // Update Button
        JButton btnUpdateEmployee = new JButton("Update Selected");
        styleButton(btnUpdateEmployee, WARNING_COLOR);
        btnUpdateEmployee.addActionListener(e -> updateEmployee());

        // Delete Button
        JButton btnDeleteEmployee = new JButton("Delete Selected");
        styleButton(btnDeleteEmployee, DANGER_COLOR);
        btnDeleteEmployee.addActionListener(e -> deleteSelectedEmployee());

        // Create Button
        JButton btnCreateEmployee = new JButton("Add New Employee");
        styleButton(btnCreateEmployee, SUCCESS_COLOR);
        btnCreateEmployee.addActionListener(e -> createEmployee());

        controlPanel.add(btnBack);
        controlPanel.add(btnUpdateEmployee);
        controlPanel.add(btnDeleteEmployee);
        controlPanel.add(btnCreateEmployee);

        mainContainer.add(controlPanel, BorderLayout.SOUTH);
        add(mainContainer);

        setSize(1350, 650);
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

    private void loadEmployeeData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT EmployeeID, FirstName, LastName, Position, TC_KimlikNo, HireDate, Salary, Shift, Email FROM employees";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            String[] headers = {"ID", "First Name", "Last Name", "Position", "ID Number", "Hire Date", "Salary", "Shift", "E-Mail"};
            for (String h : headers) {
                model.addColumn(h);
            }

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("EmployeeID"));
                row.add(rs.getString("FirstName"));
                row.add(rs.getString("LastName"));
                row.add(rs.getString("Position"));
                row.add(rs.getString("TC_KimlikNo"));
                row.add(rs.getDate("HireDate"));
                row.add(rs.getBigDecimal("Salary"));
                row.add(rs.getString("Shift"));
                row.add(rs.getString("Email"));
                model.addRow(row);
            }
            employeeTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Loading Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void createEmployee() {
        JTextField txtFirst = new JTextField();
        JTextField txtLast = new JTextField();
        JTextField txtPos = new JTextField();
        JTextField txtIDNo = new JTextField();
        JTextField txtSal = new JTextField("0.00");
        String[] shifts = {"Day", "Night"};
        JComboBox<String> cmbShift = new JComboBox<>(shifts);
        JTextField txtMail = new JTextField();

        Object[] message = {
                "First Name:", txtFirst,
                "Last Name:", txtLast,
                "Position:", txtPos,
                "ID Number (TC):", txtIDNo,
                "Salary:", txtSal,
                "Shift:", cmbShift,
                "Email:", txtMail
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register New Employee", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO employees (FirstName, LastName, Position, TC_KimlikNo, HireDate, Salary, Shift, Email) VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtFirst.getText());
                pstmt.setString(2, txtLast.getText());
                pstmt.setString(3, txtPos.getText());
                pstmt.setString(4, txtIDNo.getText());
                pstmt.setDouble(5, Double.parseDouble(txtSal.getText()));
                pstmt.setString(6, cmbShift.getSelectedItem().toString());
                pstmt.setString(7, txtMail.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "New employee registered successfully!");
                loadEmployeeData();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Registration Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = employeeTable.getValueAt(selectedRow, 0).toString();
        JTextField txtFirst = new JTextField(employeeTable.getValueAt(selectedRow, 1).toString());
        JTextField txtLast = new JTextField(employeeTable.getValueAt(selectedRow, 2).toString());
        JTextField txtPos = new JTextField(employeeTable.getValueAt(selectedRow, 3).toString());
        JTextField txtSal = new JTextField(employeeTable.getValueAt(selectedRow, 6).toString());
        JTextField txtMail = new JTextField(employeeTable.getValueAt(selectedRow, 8).toString());

        Object[] message = {
                "First Name:", txtFirst,
                "Last Name:", txtLast,
                "Position:", txtPos,
                "Salary:", txtSal,
                "Email:", txtMail
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Employee ID: " + id, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE employees SET FirstName=?, LastName=?, Position=?, Salary=?, Email=? WHERE EmployeeID=?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtFirst.getText());
                pstmt.setString(2, txtLast.getText());
                pstmt.setString(3, txtPos.getText());
                pstmt.setDouble(4, Double.parseDouble(txtSal.getText()));
                pstmt.setString(5, txtMail.getText());
                pstmt.setInt(6, Integer.parseInt(id));

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                loadEmployeeData();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object empID = employeeTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to PERMANENTLY delete Employee ID: " + empID + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM employees WHERE EmployeeID = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(empID.toString()));
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Employee removed from database.");
                    loadEmployeeData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Cannot delete employee: They may be linked to existing sales records.");
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}