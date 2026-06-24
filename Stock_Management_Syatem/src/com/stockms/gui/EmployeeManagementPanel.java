package com.stockms.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.stockms.util.DBConnection;

public class EmployeeManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private final JTable tblEmployees;
    private final DefaultTableModel tableModel;

    public EmployeeManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TYPOGRAPHY CONFIGURATIONS ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 18);
        Font tableHeaderFont = new Font("SansSerif", Font.BOLD, 16);
        Font tableBodyFont = new Font("SansSerif", Font.PLAIN, 15);
        Font actionFont = new Font("SansSerif", Font.BOLD, 14);

        // 1. TOP TITLE
        JLabel lblTitle = new JLabel("EMPLOYEE ADMINISTRATION PORTAL", JLabel.LEFT);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CENTER TABLE
        String[] columns = {"EMPLOYEE ID", "USERNAME", "FULL NAME", "ROLE", "STATUS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblEmployees = new JTable(tableModel);
        
        tblEmployees.setFont(tableBodyFont);
        tblEmployees.setRowHeight(35);
        tblEmployees.getTableHeader().setFont(tableHeaderFont);
        tblEmployees.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scrollPane = new JScrollPane(tblEmployees);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        refreshEmployeeTable();

        // 3. BOTTOM CONTROLS
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        JButton btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(actionFont);
        btnBack.setPreferredSize(new Dimension(210, 48));
        btnBack.addActionListener(e -> mainFrame.goBackToDashboard());
        pnlBottom.add(btnBack, BorderLayout.WEST);

        // ACTION BUTTONS
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRefresh = new JButton("REFRESH");
        JButton btnAdd = new JButton("ADD USER");
        JButton btnToggleRole = new JButton("TOGGLE ROLE");
        JButton btnActivate = new JButton("ACTIVATE");
        JButton btnDeactivate = new JButton("DEACTIVATE");

        // --- HIGH CONTRAST THEME (Black Text on Colored Backgrounds) ---
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        btnAdd.setBackground(new Color(40, 160, 80));
        btnAdd.setForeground(Color.BLACK);

        btnToggleRole.setBackground(new Color(230, 140, 10));
        btnToggleRole.setForeground(Color.BLACK);

        btnActivate.setBackground(new Color(70, 130, 180));
        btnActivate.setForeground(Color.BLACK);
        
        btnDeactivate.setBackground(new Color(200, 50, 50));
        btnDeactivate.setForeground(Color.BLACK);
        
        JButton[] buttons = {btnRefresh, btnAdd, btnToggleRole, btnActivate, btnDeactivate};
        for (JButton b : buttons) {
            b.setFont(actionFont);
            b.setPreferredSize(new Dimension(140, 48));
            b.setFocusPainted(false);
            b.setContentAreaFilled(true); 
            b.setOpaque(true);
        }

        pnlActions.add(btnRefresh);
        pnlActions.add(btnAdd);
        pnlActions.add(btnToggleRole);
        pnlActions.add(btnActivate);
        pnlActions.add(btnDeactivate);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- LISTENERS ---
        btnRefresh.addActionListener(e -> refreshEmployeeTable());
        btnActivate.addActionListener(e -> updateStatus("ACTIVE"));
        btnDeactivate.addActionListener(e -> updateStatus("INACTIVE"));
        btnToggleRole.addActionListener(e -> toggleEmployeeRole());
        
        btnAdd.addActionListener(e -> {
            JPanel pnlInput = new JPanel(new GridLayout(4, 2, 8, 15));
            JTextField txtUser = new JTextField();
            JTextField txtPass = new JTextField();
            JTextField txtName = new JTextField();
            JComboBox<String> cmbRole = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
            pnlInput.add(new JLabel("USERNAME:")); pnlInput.add(txtUser);
            pnlInput.add(new JLabel("PASSWORD:")); pnlInput.add(txtPass);
            pnlInput.add(new JLabel("FULL NAME:")); pnlInput.add(txtName);
            pnlInput.add(new JLabel("ROLE:")); pnlInput.add(cmbRole);

            int result = JOptionPane.showConfirmDialog(this, pnlInput, "CREATE NEW USER ACCOUNT", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                      PreparedStatement ps = conn.prepareStatement("INSERT INTO employee (username, password_hash, role, status, full_name) VALUES (?, ?, ?, 'ACTIVE', ?)")) {
                    ps.setString(1, txtUser.getText().trim());
                    ps.setString(2, txtPass.getText().trim());
                    ps.setString(3, (String) cmbRole.getSelectedItem());
                    ps.setString(4, txtName.getText().trim());
                    ps.executeUpdate();
                    refreshEmployeeTable();
                } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Save Failed: " + ex.getMessage()); }
            }
        });
    }

    public final void refreshEmployeeTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT employee_id, username, full_name, role, status FROM employee")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("employee_id"), rs.getString("username"), rs.getString("full_name"), rs.getString("role"), rs.getString("status")});
            }
        } catch (SQLException ex) { System.err.println("Database error: " + ex.getMessage()); }
    }

    private void toggleEmployeeRole() {
        int row = tblEmployees.getSelectedRow();
        if (row == -1) return;
        String current = tableModel.getValueAt(row, 3).toString();
        String target = "ADMIN".equalsIgnoreCase(current) ? "STAFF" : "ADMIN";
        try (Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement("UPDATE employee SET role = ? WHERE employee_id = ?")) {
            ps.setString(1, target);
            ps.setInt(2, Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
            ps.executeUpdate();
            refreshEmployeeTable();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage()); }
    }

    private void updateStatus(String s) {
        int row = tblEmployees.getSelectedRow();
        if (row == -1) return;
        try (Connection conn = DBConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement("UPDATE employee SET status = ? WHERE employee_id = ?")) {
            ps.setString(1, s);
            ps.setInt(2, Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
            ps.executeUpdate();
            refreshEmployeeTable();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage()); }
    }
}