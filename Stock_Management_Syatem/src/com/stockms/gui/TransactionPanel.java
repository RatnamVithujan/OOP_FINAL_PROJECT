package com.stockms.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.stockms.util.DBConnection;

public class TransactionPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JTable tblTransactions;
    private final DefaultTableModel tableModel;

    public TransactionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TYPOGRAPHY CONFIGURATIONS ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 18);
        Font tableHeaderFont = new Font("SansSerif", Font.BOLD, 16);
        Font tableBodyFont = new Font("SansSerif", Font.PLAIN, 15);
        Font actionFont = new Font("SansSerif", Font.BOLD, 14);

        // 1. TOP TITLE
        JLabel lblTitle = new JLabel("INVENTORY TRANSACTION HISTORY LEDGER", JLabel.LEFT);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CENTER TABLE
        String[] columns = {"TRANSACTION ID", "ITEM NAME", "TRANSACTION TYPE", "QUANTITY", "UNIT PRICE (LKR)", "TIMESTAMP"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTransactions = new JTable(tableModel);
        
        tblTransactions.setFont(tableBodyFont);
        tblTransactions.setRowHeight(35);
        tblTransactions.getTableHeader().setFont(tableHeaderFont);
        tblTransactions.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scrollPane = new JScrollPane(tblTransactions);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        refreshTransactionTable();

        // 3. BOTTOM CONTROLS
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        JButton btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(actionFont);
        btnBack.setPreferredSize(new Dimension(210, 48));
        btnBack.addActionListener(e -> mainFrame.goBackToDashboard());
        pnlBottom.add(btnBack, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnRefresh = new JButton("REFRESH TRANSACTIONS");
        
        btnRefresh.setBackground(new Color(0, 120, 215));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(actionFont);
        btnRefresh.setPreferredSize(new Dimension(230, 48));
        btnRefresh.setFocusPainted(false);

        pnlActions.add(btnRefresh);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- LISTENER ---
        btnRefresh.addActionListener(e -> refreshTransactionTable());
    }

    public final void refreshTransactionTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT t.transaction_id, s.item_name, t.type, t.quantity, t.unit_price, t.transaction_date " +
                     "FROM stock_transaction t " +
                     "JOIN stock s ON t.stock_id = s.stock_id " +
                     "ORDER BY t.transaction_date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("item_name"),       
                    rs.getString("type").toUpperCase(),
                    rs.getInt("quantity"),
                    "Rs. " + String.format("%.2f", rs.getDouble("unit_price")),
                    rs.getTimestamp("transaction_date")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Transaction log database fetch failure: " + ex.getMessage());
        }
    }
}