package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class UserRecordScreen extends JFrame {

    private JTable userTable;

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public UserRecordScreen() {
        super("User Management System");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // HEADER
        JLabel lblHeader = new JLabel("USER ACCOUNT MANAGEMENT");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLE
        userTable = new JTable();
        styleTable(userTable);
        loadUserData();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        controlPanel.setOpaque(false);

        // DÜZELTİLDİ: Artık DashboardScreen'e yönlendiriyor
        JButton btnHome = new JButton("Back to Dashboard");
        styleButton(btnHome, SECONDARY_COLOR);
        btnHome.addActionListener(e -> {
            new DashboardScreen().setVisible(true);
            this.dispose();
        });

        JButton btnUpdate = new JButton("Update User");
        styleButton(btnUpdate, WARNING_COLOR);
        btnUpdate.addActionListener(e -> updateUser());

        JButton btnDelete = new JButton("Delete User");
        styleButton(btnDelete, DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteUser());

        JButton btnCreate = new JButton("Create User");
        styleButton(btnCreate, SUCCESS_COLOR);
        btnCreate.addActionListener(e -> createUser());

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

    private void loadUserData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT u.UserID, u.Username, u.IsActive, e.FirstName, e.LastName " +
                "FROM users u " +
                "JOIN employees e ON u.EmployeeID = e.EmployeeID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            String[] headers = {"User ID", "Username", "Employee Name", "Status"};
            for (String h : headers) { model.addColumn(h); }

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("UserID"));
                row.add(rs.getString("Username"));
                row.add(rs.getString("FirstName") + " " + rs.getString("LastName"));
                row.add(rs.getInt("IsActive") == 1 ? "Active" : "Inactive");
                model.addRow(row);
            }
            userTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void createUser() {
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtEmpID = new JTextField();

        Object[] message = {
                "Username:", txtUser,
                "Password:", txtPass,
                "Employee ID:", txtEmpID
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New User Account", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO users (Username, Password, EmployeeID, IsActive) VALUES (?, ?, ?, 1)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtUser.getText());
                pstmt.setString(2, new String(txtPass.getPassword()));
                pstmt.setInt(3, Integer.parseInt(txtEmpID.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User created successfully!");
                loadUserData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userID = (int) userTable.getValueAt(selectedRow, 0);
        String currentUsername = (String) userTable.getValueAt(selectedRow, 1);
        String currentStatus = (String) userTable.getValueAt(selectedRow, 3);

        JTextField txtUser = new JTextField(currentUsername);
        JPasswordField txtPass = new JPasswordField();
        String[] statusOptions = {"Active", "Inactive"};
        JComboBox<String> cmbStatus = new JComboBox<>(statusOptions);
        cmbStatus.setSelectedItem(currentStatus);

        Object[] message = {
                "Update Username:", txtUser,
                "New Password (Leave blank to keep current):", txtPass,
                "Account Status:", cmbStatus
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update User: " + currentUsername, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Connection conn = DBConnection.getConnection();
            String newPassword = new String(txtPass.getPassword());
            int activeVal = cmbStatus.getSelectedItem().equals("Active") ? 1 : 0;

            String sql;
            if (newPassword.isEmpty()) {
                sql = "UPDATE users SET Username = ?, IsActive = ? WHERE UserID = ?";
            } else {
                sql = "UPDATE users SET Username = ?, IsActive = ?, Password = ? WHERE UserID = ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, txtUser.getText());
                pstmt.setInt(2, activeVal);
                if (newPassword.isEmpty()) {
                    pstmt.setInt(3, userID);
                } else {
                    pstmt.setString(3, newPassword);
                    pstmt.setInt(4, userID);
                }
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUserData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userID = (int) userTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this user account?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE UserID = ?")) {
                pstmt.setInt(1, userID);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                loadUserData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: You cannot delete a user linked to existing activity.");
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}