package com.fuelstation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginScreen extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private static LanguageManager lm = LanguageManager.getInstance();


    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Koyu Mavi
    private final Color HOVER_COLOR = new Color(52, 152, 219);   // Açık Mavi
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(45, 52, 54);
    private final Color INPUT_BORDER = new Color(223, 230, 233);

    public LoginScreen() {

        super("Fuel Station Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Ana Konteynır (Dış boşluklar)
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        mainContainer.setBorder(new EmptyBorder(40, 50, 40, 50));

        // 1. ÜST KISIM: Başlık Bölümü
        JLabel lblHeader = new JLabel("FUEL STATION");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubHeader = new JLabel("Management System");
        lblSubHeader.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 16));
        lblSubHeader.setForeground(TEXT_DARK);
        lblSubHeader.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.add(lblHeader);
        headerPanel.add(lblSubHeader);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // 2. ORTA KISIM: Giriş Alanları (BoxLayout ile Dikey Hizalama)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Username Bölümü
        JLabel lblUser = createInputLabel("Username");
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(lblUser);

        txtUsername = createStyledTextField();
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT); // Ortala
        formPanel.add(txtUsername);

        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // İki kutu arası boşluk

        // Password Bölümü
        JLabel lblPass = createInputLabel("Password");
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(lblPass);

        txtPassword = createStyledPasswordField();
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Ortala
        formPanel.add(txtPassword);

        mainContainer.add(formPanel, BorderLayout.CENTER);

        // 3. ALT KISIM: Buton Bölümü
        btnLogin = new JButton("LOGIN");
        styleLoginButton(btnLogin);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT); // Ortala
        btnLogin.addActionListener(e -> attemptLogin());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        footerPanel.add(btnLogin);

        mainContainer.add(footerPanel, BorderLayout.SOUTH);

        add(mainContainer);
        pack();
        setLocationRelativeTo(null); // Ekranın ortasında açılır
    }

    // Etiketler için standart stil
    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    // Modern TextField oluşturucu
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        applyFieldStyle(field);
        return field;
    }

    // Modern PasswordField oluşturucu
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        applyFieldStyle(field);
        return field;
    }

    // Input alanlarını butonla aynı genişliğe (300px) getiren stil
    private void applyFieldStyle(JTextField field) {
        // Genişlik ayarı: 300px genişlik, 40px yükseklik
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(300, 40));
        field.setMinimumSize(new Dimension(300, 40));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);

        // İçeriden boşluk ve ince gri kenarlık
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(INPUT_BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)));

        // Odaklanma (Focus) Efekti
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 1),
                        new EmptyBorder(5, 10, 5, 10)));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(INPUT_BORDER, 1),
                        new EmptyBorder(5, 10, 5, 10)));
            }
        });
    }

    // Login butonunu 300px genişliğinde ve modern renkte tasarlar
    private void styleLoginButton(JButton btn) {
        btn.setPreferredSize(new Dimension(300, 45)); // Inputlarla aynı genişlik
        btn.setMaximumSize(new Dimension(300, 45));

        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Üzerine gelince renk değiştirme (Hover)
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_COLOR); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY_COLOR); }
        });
    }

    // Giriş Denemesi Mantığı
// Giriş Denemesi Mantığı
    private void attemptLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!",
                    "Fuel Station", JOptionPane.INFORMATION_MESSAGE);

            // Başarılı girişte direkt Dashboard sayfasına yönlendirilir
            new DashboardScreen().setVisible(true);
            dispose(); // Login penceresini kapatır
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Veritabanı Sorgusu
    private boolean authenticateUser(String username, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Kayıt bulunduysa true döner
            }
        } catch (SQLException e) {
            System.err.println("Login Error: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}