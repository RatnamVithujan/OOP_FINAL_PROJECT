package com.stockms.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.stockms.util.DBConnection;

public class LoginLogPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JTable tblLogs;
    private final DefaultTableModel tableModel;

    public LoginLogPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TYPOGRAPHY CONFIGURATIONS ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 18);
        Font tableHeaderFont = new Font("SansSerif", Font.BOLD, 16);
        Font tableBodyFont = new Font("SansSerif", Font.PLAIN, 15);
        Font actionFont = new Font("SansSerif", Font.BOLD, 14);

        // 1. TOP TITLE
        JLabel lblTitle = new JLabel("SYSTEM LOGIN AUDIT LOGS", JLabel.LEFT);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CENTER TABLE
        String[] columns = {"LOG ID", "EMPLOYEE ID", "USERNAME", "FULL NAME", "ROLE", "LOGIN TIMESTAMP"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLogs = new JTable(tableModel);
        
        tblLogs.setFont(tableBodyFont);
        tblLogs.setRowHeight(35);
        tblLogs.getTableHeader().setFont(tableHeaderFont);
        tblLogs.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scrollPane = new JScrollPane(tblLogs);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        refreshLogsTable();

        // 3. BOTTOM CONTROLS
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        // BACK BUTTON - UPDATED ROUTING
        JButton btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(actionFont);
        btnBack.setPreferredSize(new Dimension(210, 48));
        btnBack.addActionListener(e -> mainFrame.goBackToDashboard());
        pnlBottom.add(btnBack, BorderLayout.WEST);

        // ACTION BUTTONS
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRefresh = new JButton("REFRESH LOGS");
        JButton btnClearLogs = new JButton("CLEAR HISTORY");

        // --- THEME OVERRIDES ---
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        btnClearLogs.setBackground(new Color(200, 50, 50)); // Alert Red
        btnClearLogs.setForeground(Color.BLACK); // High contrast black text

        JButton[] actionButtons = {btnRefresh, btnClearLogs};
        for (JButton b : actionButtons) {
            b.setFont(actionFont);
            b.setPreferredSize(new Dimension(180, 48));
            b.setFocusPainted(false);
            b.setContentAreaFilled(true);
            b.setOpaque(true);
        }

        pnlActions.add(btnRefresh);
        pnlActions.add(btnClearLogs);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- LISTENERS ---
        btnRefresh.addActionListener(e -> refreshLogsTable());
        btnClearLogs.addActionListener(e -> clearAuditHistory());
    }

    public final void refreshLogsTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT l.log_id, l.employee_id, e.username, e.full_name, e.role, l.login_time " +
                     "FROM login_log l " +
                     "JOIN employee e ON l.employee_id = e.employee_id " +
                     "ORDER BY l.login_time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("log_id"), rs.getInt("employee_id"),
                    rs.getString("username"), rs.getString("full_name"),
                    rs.getString("role"), rs.getTimestamp("login_time")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Logs database fetch failure: " + ex.getMessage());
        }
    }

    private void clearAuditHistory() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you absolutely sure you want to permanently delete all login track logs?", 
                "CONFIRM CLEAR", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM login_log");
                refreshLogsTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Clear Failed: " + ex.getMessage());
            }
        }
    }
}