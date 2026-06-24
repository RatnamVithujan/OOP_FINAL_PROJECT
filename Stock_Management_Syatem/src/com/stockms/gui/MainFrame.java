package com.stockms.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    
    private int currentEmployeeId;
    // NEW: Variable to dynamically track if the logged-in user is an ADMIN or STAFF
    private String currentUserRole = "STAFF"; 

    public MainFrame() {
        setTitle("Stock Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add the real active application views
        cardPanel.add(new LoginPanel(this), "LoginView");
        cardPanel.add(new AdminDashboardPanel(this), "ADMIN_DASHBOARD");
        cardPanel.add(new StaffDashboardPanel(this), "STAFF_DASHBOARD");
        cardPanel.add(new EmployeeManagementPanel(this), "EMPLOYEE_MANAGEMENT");
        cardPanel.add(new LoginLogPanel(this), "LOGIN_LOGS");
        cardPanel.add(new TransactionPanel(this), "TRANSACTIONS");
        cardPanel.add(new DealerManagementPanel(this), "DEALER_MANAGEMENT");
        cardPanel.add(new SupplierManagementPanel(this), "SUPPLIER_MANAGEMENT");

        add(cardPanel);
        cardLayout.show(cardPanel, "LoginView");

        // FORCES the application window to open completely maximized right on startup
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void showCard(String cardName) {
        cardLayout.show(cardPanel, cardName);
    }

    // --- NEW DYNAMIC ROUTING & SESSION MANAGEMENT METHODS ---

    /**
     * Sets the logged-in employee ID dynamically when authentication succeeds.
     */
    public void setCurrentEmployeeId(int employeeId) {
        this.currentEmployeeId = employeeId;
        System.out.println("User session locked dynamically to Employee ID: " + employeeId);
    }

    public int getCurrentEmployeeId() {
        return this.currentEmployeeId;
    }

    /**
     * NEW: Sets the active role ("ADMIN" or "STAFF") during login authentication.
     */
    public void setCurrentUserRole(String role) {
        if (role != null) {
            this.currentUserRole = role.toUpperCase().trim();
            System.out.println("Active user interface role initialized to: " + this.currentUserRole);
        }
    }

    /**
     * NEW: Smart dynamic routing system. 
     * Call this inside ANY sub-panel's BACK button action listener!
     */
    public void goBackToDashboard() {
        if ("ADMIN".equals(this.currentUserRole)) {
            showCard("ADMIN_DASHBOARD");
        } else {
            showCard("STAFF_DASHBOARD");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}