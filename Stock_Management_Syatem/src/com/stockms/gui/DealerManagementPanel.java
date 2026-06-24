package com.stockms.gui;

import com.stockms.dao.DealerDAO;
import com.stockms.model.Dealer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DealerManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DealerDAO dealerDAO = new DealerDAO();
    private final JTable tblDealers;
    private final DefaultTableModel tableModel;

    public DealerManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Generous full-screen outer padding

        // --- GLOBAL HIGH-CONTRAST UPSCALE TYPOGRAPHY CONFIGURATIONS ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 18);
        Font tableHeaderFont = new Font("SansSerif", Font.BOLD, 16);
        Font tableBodyFont = new Font("SansSerif", Font.PLAIN, 15);
        Font popupLabelFont = new Font("SansSerif", Font.BOLD, 15);
        Font actionFont = new Font("SansSerif", Font.BOLD, 15);

        // 1. TOP TITLE STRIP
        JLabel lblTitle = new JLabel("REGISTERED DEALER DIRECTORY", JLabel.LEFT); // CONVERTED TO BOLD UPPERCASE
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CENTER DATA GRID TABLE
        String[] columns = {"DEALER ID", "COMPANY NAME", "CONTACT NUMBER", "OFFICE ADDRESS"}; // BOLD UPPERCASE HEADERS
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDealers = new JTable(tableModel);
        
        tblDealers.setFont(tableBodyFont);
        tblDealers.setRowHeight(35);
        tblDealers.getTableHeader().setFont(tableHeaderFont);
        tblDealers.getTableHeader().setPreferredSize(new Dimension(0, 42));

        JScrollPane scrollPane = new JScrollPane(tblDealers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();

        // 3. BOTTOM CONTROL NAVIGATION BAR
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JButton btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(actionFont);
        btnBack.setPreferredSize(new Dimension(210, 48));
        btnBack.addActionListener(e -> mainFrame.goBackToDashboard());
        pnlBottom.add(btnBack, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnRefresh = new JButton("REFRESH LIST");
        JButton btnAddDealer = new JButton("ADD NEW DEALER");

        btnRefresh.setBackground(new Color(0, 120, 215));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(actionFont);
        btnRefresh.setPreferredSize(new Dimension(180, 48));

        btnAddDealer.setBackground(new Color(40, 180, 40));
        btnAddDealer.setForeground(Color.BLACK);
        btnAddDealer.setFont(actionFont);
        btnAddDealer.setPreferredSize(new Dimension(200, 48));

        pnlActions.add(btnRefresh);
        pnlActions.add(btnAddDealer);
        pnlBottom.add(pnlActions, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // --- INTERACTION ACTION LISTENERS ---
        btnRefresh.addActionListener(e -> refreshTable());
        
        btnAddDealer.addActionListener(e -> {
            JPanel pnlInput = new JPanel(new GridLayout(3, 2, 8, 15));
            JTextField txtName = new JTextField();
            JTextField txtContact = new JTextField();
            JTextField txtAddress = new JTextField();

            txtName.setFont(tableBodyFont);
            txtContact.setFont(tableBodyFont);
            txtAddress.setFont(tableBodyFont);

            JLabel lblName = new JLabel("COMPANY NAME:"); lblName.setFont(popupLabelFont);
            JLabel lblContact = new JLabel("CONTACT NO:"); lblContact.setFont(popupLabelFont);
            JLabel lblAddress = new JLabel("ADDRESS:"); lblAddress.setFont(popupLabelFont);

            pnlInput.add(lblName); pnlInput.add(txtName);
            pnlInput.add(lblContact); pnlInput.add(txtContact);
            pnlInput.add(lblAddress); pnlInput.add(txtAddress);

            int result = JOptionPane.showConfirmDialog(this, pnlInput, "REGISTER NEW DEALER CONTACT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String name = txtName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Company Name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    dealerDAO.addDealer(new Dealer(0, name, txtContact.getText().trim(), txtAddress.getText().trim()));
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Dealer profile saved successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Save Failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public final void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Dealer> dealers = dealerDAO.getAllDealers();
            for (Dealer d : dealers) {
                tableModel.addRow(new Object[]{
                    d.getDealerId(), d.getName(), d.getContactNo(), d.getAddress()
                });
            }
        } catch (Exception ex) {
            System.err.println("Dealer load execution error: " + ex.getMessage());
        }
    }
}