package com.fuelstation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

class SalesRecordScreen extends JFrame {

    private JTable salesTable;
    private static LanguageManager lm = LanguageManager.getInstance();

    // Modern Renk Paleti
    private final Color PRIMARY_COLOR = new Color(41, 128, 185); // Mavi
    private final Color DANGER_COLOR = new Color(231, 76, 60);   // Kırmızı (Silme)
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);  // Yeşil (Yeni Kayıt)
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);

    public SalesRecordScreen() {
        super(lm.getString("records.title"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Ana Konteynır (Boşluklar için)
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // BAŞLIK
        JLabel lblHeader = new JLabel(lm.getString("records.title").toUpperCase());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_COLOR);
        lblHeader.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainContainer.add(lblHeader, BorderLayout.NORTH);

        // TABLO TASARIMI
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

        JButton btnDeleteSale = new JButton(lm.getString("records.deleteButton"));
        styleButton(btnDeleteSale, DANGER_COLOR);
        btnDeleteSale.addActionListener(e -> deleteSelectedSale());

        JButton btnNewSale = new JButton(lm.getString("records.newSaleButton"));
        styleButton(btnNewSale, SUCCESS_COLOR);
        btnNewSale.addActionListener(e -> {
            new FuelSaleTransactionScreen().setVisible(true);
            this.dispose(); // Genelde yeni satış ekranı açılınca liste kapanır veya güncellenir
        });

        controlPanel.add(btnDeleteSale);
        controlPanel.add(btnNewSale);

        mainContainer.add(controlPanel, BorderLayout.SOUTH);

        add(mainContainer);
        setSize(900, 600); // Biraz daha geniş alan verileri rahat gösterir
        setLocationRelativeTo(null);
    }

    // Tabloyu modern bir görünüme kavuşturur
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30); // Satır yüksekliği artırıldı
        table.setSelectionBackground(new Color(232, 244, 253)); // Seçim rengi
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false); // Sadece yatay çizgiler kalsın (Modern stil)

        // Header (Başlık) Tasarımı
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 40));
    }

    // Butonları modernize eden yardımcı metot
    private void styleButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
    }

    // --- Veritabanı Mantığı (Yapıyı Bozmadan) ---

    private void loadSalesData() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Tablo elle değiştirilemesin
        };

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT TransactionID, TransactionDate, TotalAmount, PaymentMethod FROM SalesTransactions ORDER BY TransactionDate DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            Vector<String> columnNames = new Vector<>();
            // Başlıkları daha okunaklı yapmak için elle de girilebilir ama dinamik yapı korundu
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }
            model.setColumnIdentifiers(columnNames);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object val = rs.getObject(i);
                    // Tarih formatını daha temiz göstermek isterseniz burada işlem yapabilirsiniz
                    row.add(val);
                }
                model.addRow(row);
            }
            salesTable.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, lm.getString("records.loadingError") + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private void deleteSelectedSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, lm.getString("records.noSelection"), "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object saleIDObj = salesTable.getValueAt(selectedRow, 0);
        String saleID = saleIDObj.toString();

        String confirmMessage = lm.getString("records.confirmDelete").replace("$deleteID$", saleID);

        int confirm = JOptionPane.showConfirmDialog(this, confirmMessage, "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return;

            String sql = "DELETE FROM SalesTransactions WHERE TransactionID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, Long.parseLong(saleID));
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, lm.getString("records.deleteSuccess"), "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSalesData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, lm.getString("records.dbError") + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
    }
}