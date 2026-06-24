package com.stockms.gui;

import com.stockms.model.Product;
import com.stockms.service.InventoryService;
import com.stockms.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final InventoryService inventoryService = new InventoryService();
    private JTable tblStock;
    private DefaultTableModel tableModel;

    // Typography constants for consistency
    private final Font tableBodyFont = new Font("SansSerif", Font.PLAIN, 15);
    private final Font popupLabelFont = new Font("SansSerif", Font.BOLD, 15);

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font navFont = new Font("SansSerif", Font.BOLD, 14);
        Font tableHeaderFont = new Font("SansSerif", Font.BOLD, 16);
        Font actionFont = new Font("SansSerif", Font.BOLD, 15);

        // 1. TOP NAVIGATION BAR
        JPanel pnlTopNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTopNav.setBorder(BorderFactory.createEtchedBorder());

        JButton btnEmployees = new JButton("EMPLOYEE DETAILS");
        JButton btnLoginLogs = new JButton("LOGIN LOGS");
        JButton btnTransactions = new JButton("TRANSACTIONS");
        JButton btnDealers = new JButton("DEALER DETAILS");
        JButton btnSuppliers = new JButton("SUPPLIER DETAILS");

        JButton[] navButtons = {btnEmployees, btnLoginLogs, btnTransactions, btnDealers, btnSuppliers};
        for (JButton btn : navButtons) {
            btn.setFont(navFont);
            btn.setPreferredSize(new Dimension(200, 45));
        }

        pnlTopNav.add(btnEmployees);
        pnlTopNav.add(btnLoginLogs);
        pnlTopNav.add(btnTransactions);
        pnlTopNav.add(btnDealers);
        pnlTopNav.add(btnSuppliers);
        add(pnlTopNav, BorderLayout.NORTH);

        // 2. MIDDLE AREA
        String[] columns = {"STOCK ID", "ITEM NAME", "CATEGORY", "QUANTITY AVAILABLE", "UNIT PRICE"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblStock = new JTable(tableModel);
        
        tblStock.setFont(tableBodyFont);
        tblStock.setRowHeight(35);
        tblStock.getTableHeader().setFont(tableHeaderFont);
        tblStock.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scrollPane = new JScrollPane(tblStock);
        TitledBorder border = BorderFactory.createTitledBorder("CURRENT STOCK INVENTORY STATUS (ADMIN MODE)");
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 16));
        scrollPane.setBorder(border);
        add(scrollPane, BorderLayout.CENTER);

        refreshTableData();

        // 3. BOTTOM CONTROL BAR
        JPanel pnlBottomBar = new JPanel(new BorderLayout());
        pnlBottomBar.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JButton btnLogout = new JButton("LOGOUT");
        btnLogout.setForeground(new Color(180, 0, 0)); 
        btnLogout.setFont(actionFont);
        btnLogout.setPreferredSize(new Dimension(140, 48));
        pnlBottomBar.add(btnLogout, BorderLayout.WEST);

        JPanel pnlStockActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnAddProduct = new JButton("ADD NEW PRODUCT");
        JButton btnStockUp = new JButton("STOCK UP (RESTOCK)");
        JButton btnStockDown = new JButton("STOCK DOWN (DISPATCH)");
        
        btnAddProduct.setBackground(new Color(0, 120, 215));
        btnAddProduct.setForeground(Color.BLACK); 
        btnAddProduct.setFont(actionFont);
        btnAddProduct.setPreferredSize(new Dimension(210, 48));
        
        btnStockUp.setBackground(new Color(40, 180, 40));
        btnStockUp.setForeground(Color.BLACK); 
        btnStockUp.setFont(actionFont);
        btnStockUp.setPreferredSize(new Dimension(220, 48));

        btnStockDown.setBackground(new Color(255, 140, 0));
        btnStockDown.setForeground(Color.BLACK); 
        btnStockDown.setFont(actionFont);
        btnStockDown.setPreferredSize(new Dimension(220, 48));

        pnlStockActions.add(btnAddProduct);
        pnlStockActions.add(btnStockUp);
        pnlStockActions.add(btnStockDown);
        pnlBottomBar.add(pnlStockActions, BorderLayout.EAST);
        add(pnlBottomBar, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---

        btnAddProduct.addActionListener(e -> {
            JPanel pnlInput = new JPanel(new GridLayout(4, 2, 8, 15));
            JTextField txtName = new JTextField();
            JTextField txtCategory = new JTextField();
            JTextField txtQty = new JTextField("0");
            JTextField txtPrice = new JTextField("0.00");

            txtName.setFont(tableBodyFont);
            txtCategory.setFont(tableBodyFont);
            txtQty.setFont(tableBodyFont);
            txtPrice.setFont(tableBodyFont);

            JLabel lblName = new JLabel("ITEM NAME:"); lblName.setFont(popupLabelFont);
            JLabel lblCat = new JLabel("CATEGORY:"); lblCat.setFont(popupLabelFont);
            JLabel lblQty = new JLabel("INITIAL QUANTITY:"); lblQty.setFont(popupLabelFont);
            JLabel lblPrice = new JLabel("UNIT PRICE (Rs.):"); lblPrice.setFont(popupLabelFont);

            pnlInput.add(lblName); pnlInput.add(txtName);
            pnlInput.add(lblCat); pnlInput.add(txtCategory);
            pnlInput.add(lblQty); pnlInput.add(txtQty);
            pnlInput.add(lblPrice); pnlInput.add(txtPrice);

            int result = JOptionPane.showConfirmDialog(this, pnlInput, "REGISTER NEW INVENTORY ITEM", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // UPDATED: Query now targets 'stock' table and 'item_name' column
                    String sql = "INSERT INTO stock (item_name, category, quantity, unit_price) VALUES (?, ?, ?, ?)";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtName.getText().trim());
                        ps.setString(2, txtCategory.getText().trim());
                        ps.setInt(3, Integer.parseInt(txtQty.getText().trim()));
                        ps.setDouble(4, Double.parseDouble(txtPrice.getText().trim()));
                        ps.executeUpdate();
                    }
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "New item saved into inventory successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
                }
            }
        });

        btnStockUp.addActionListener(e -> {
            int selectedRow = tblStock.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JPanel pnlInput = new JPanel(new GridLayout(2, 2, 8, 15));
            JComboBox<String> cmbSupplier = new JComboBox<>();
            loadDropdownFromDB(cmbSupplier, "SELECT name FROM supplier"); 
            JTextField txtQty = new JTextField();

            cmbSupplier.setFont(tableBodyFont);
            txtQty.setFont(tableBodyFont);

            JLabel lblSelSupplier = new JLabel("SELECT SUPPLIER:"); lblSelSupplier.setFont(popupLabelFont);
            JLabel lblEnterQty = new JLabel("ENTER QUANTITY TO ADD:"); lblEnterQty.setFont(popupLabelFont);

            pnlInput.add(lblSelSupplier); pnlInput.add(cmbSupplier);
            pnlInput.add(lblEnterQty); pnlInput.add(txtQty);

            int result = JOptionPane.showConfirmDialog(this, pnlInput, "STOCK UP (RESTOCK ENTRY)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    int currentQty = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
                    int qtyToAdd = Integer.parseInt(txtQty.getText().trim());
                    inventoryService.updateStockLevel(productId, currentQty, qtyToAdd, true, mainFrame.getCurrentEmployeeId());
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Database entry permanently updated!");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        btnStockDown.addActionListener(e -> {
            int selectedRow = tblStock.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JPanel pnlInput = new JPanel(new GridLayout(2, 2, 8, 15));
            JComboBox<String> cmbDealer = new JComboBox<>();
            loadDropdownFromDB(cmbDealer, "SELECT name FROM dealer"); 
            JTextField txtQty = new JTextField();

            cmbDealer.setFont(tableBodyFont);
            txtQty.setFont(tableBodyFont);

            JLabel lblSelDealer = new JLabel("SELECT DEALER:"); lblSelDealer.setFont(popupLabelFont);
            JLabel lblRemQty = new JLabel("ENTER QUANTITY TO REMOVE:"); lblRemQty.setFont(popupLabelFont);

            pnlInput.add(lblSelDealer); pnlInput.add(cmbDealer);
            pnlInput.add(lblRemQty); pnlInput.add(txtQty);

            int result = JOptionPane.showConfirmDialog(this, pnlInput, "STOCK DOWN (DISPATCH ENTRY)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    int currentQty = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
                    int qtyToRemove = Integer.parseInt(txtQty.getText().trim());
                    inventoryService.updateStockLevel(productId, currentQty, qtyToRemove, false, mainFrame.getCurrentEmployeeId());
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Database entry permanently updated!");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        btnLogout.addActionListener(e -> { if(JOptionPane.showConfirmDialog(this, "Logout?") == JOptionPane.YES_OPTION) mainFrame.showCard("LoginView"); });
        btnEmployees.addActionListener(e -> mainFrame.showCard("EMPLOYEE_MANAGEMENT"));
        btnLoginLogs.addActionListener(e -> mainFrame.showCard("LOGIN_LOGS"));
        btnTransactions.addActionListener(e -> mainFrame.showCard("TRANSACTIONS"));
        btnDealers.addActionListener(e -> mainFrame.showCard("DEALER_MANAGEMENT"));
        btnSuppliers.addActionListener(e -> mainFrame.showCard("SUPPLIER_MANAGEMENT"));
    }

    private void loadDropdownFromDB(JComboBox<String> comboBox, String sql) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            comboBox.removeAllItems();
            while (rs.next()) {
                comboBox.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println("Database load error: " + ex.getMessage());
        }
    }

    public final void refreshTableData() {
        try {
            tableModel.setRowCount(0); 
            List<Product> products = inventoryService.loadInventory();
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getQuantity(),
                    "Rs. " + String.format("%.2f", p.getUnitPrice())
                });
            }
        } catch (Exception ex) {
            System.err.println("Failed to read items: " + ex.getMessage());
        }
    }
}