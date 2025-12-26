package com.fuelstation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardScreen extends JFrame {

    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  
    private final Color ACCENT_COLOR = new Color(142, 68, 173);   
    private final Color WARNING_COLOR = new Color(243, 156, 18); 
    private final Color INFO_COLOR = new Color(52, 152, 219);    
    private final Color PUMP_COLOR = new Color(230, 126, 34);    
    private final Color PHONE_COLOR = new Color(22, 160, 133);   
    private final Color STORE_COLOR = new Color(192, 57, 43);  
    private final Color DANGER_COLOR = new Color(231, 76, 60);  
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);

    public DashboardScreen() {
        super("Fuel Station Management - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("WELCOME TO CONTROL PANEL");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(PRIMARY_COLOR);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSub = new JLabel("Select an administrative task to continue");
        lblSub.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 16));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(lblWelcome);
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 30, 25));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        gridPanel.add(createMenuButton("NEW FUEL SALE", "View and record transactions", SUCCESS_COLOR, e -> {
            new SalesRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("EMPLOYEE MGMT", "Manage staff records", PRIMARY_COLOR, e -> {
            new EmployeeRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("USER ACCOUNTS", "Manage system login access", ACCENT_COLOR, e -> {
            new UserRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("SALES RECORDS", "View transaction history", WARNING_COLOR, e -> {
            new SalesRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("FUEL TYPES", "Manage prices and stocks", INFO_COLOR, e -> {
            new FuelTypeRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("FUEL PUMPS", "Monitor pump maintenance", PUMP_COLOR, e -> {
            new FuelPumpRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("PHONE DIRECTORY", "Manage staff contact numbers", PHONE_COLOR, e -> {
            new EmployeePhoneRecordScreen().setVisible(true);
            this.dispose();
        }));

        gridPanel.add(createMenuButton("STORE SALES", "Manage market and shop sales", STORE_COLOR, e -> {
            new StoreSalesRecordScreen().setVisible(true);
            this.dispose();
        }));

        mainPanel.add(gridPanel, BorderLayout.CENTER);


        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        JButton btnLogout = new JButton("LOGOUT FROM SYSTEM");
        btnLogout.setPreferredSize(new Dimension(300, 45));
        btnLogout.setBackground(DANGER_COLOR);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(DANGER_COLOR.brighter()); }
            public void mouseExited(MouseEvent e) { btnLogout.setBackground(DANGER_COLOR); }
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginScreen().setVisible(true);
                this.dispose();
            }
        });

        footerPanel.add(btnLogout);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSize(850, 950);
        setLocationRelativeTo(null);
    }

    private JPanel createMenuButton(String title, String subtitle, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(250, 250, 250)); }
            public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
            public void mousePressed(MouseEvent e) { action.actionPerformed(null); }
        });

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(10, 0));
        card.add(colorBar, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);

        textPanel.add(lblTitle);
        textPanel.add(lblSub);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
}