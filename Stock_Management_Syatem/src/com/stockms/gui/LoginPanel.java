package com.stockms.gui;

import com.stockms.model.Employee;
import com.stockms.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private final AuthService authService = new AuthService();
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        
        // Modern layout with generous full-screen distribution grid sizing
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Breathing room between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- STYLISH UPPERCASE DESIGN FONT FAMILY SCHEMES ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 28);      // Large premium landing layout title
        Font labelFont = new Font("SansSerif", Font.BOLD, 15);      // Highly readable field labels
        Font inputFont = new Font("SansSerif", Font.PLAIN, 15);     // Comfortable input character font matrix
        Font buttonFont = new Font("SansSerif", Font.BOLD, 15);     // Standout command action button text

        // 1. SYSTEM MAIN TITLE
        JLabel lblTitle = new JLabel("STOCK MANAGEMENT SYSTEM", JLabel.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(30, 41, 59)); // Deep professional slate color
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 35, 15); // Extra structural gap beneath header title block
        add(lblTitle, gbc);

        // Reset grid structure configuration settings
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 10, 15);

        // 2. USERNAME INPUT INTERFACE ROW
        JLabel lblUsername = new JLabel("USERNAME:"); // CONVERTED TO BOLD UPPERCASE
        lblUsername.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(inputFont);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(10, 12, 10, 12) // Thick structural tracking textbox pad dimensioning
        ));
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // 3. PASSWORD INPUT INTERFACE ROW
        JLabel lblPassword = new JLabel("PASSWORD:"); // CONVERTED TO BOLD UPPERCASE
        lblPassword.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(inputFont);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // 4. SIGN IN SUBMIT CONTROL BUTTON
        JButton btnLogin = new JButton("SIGN IN TO ACCOUNT"); // CONVERTED TO BOLD UPPERCASE
        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(0, 120, 215)); // Deep corporate blueprint accent blue
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(btnLogin.getPreferredSize().width, 48)); // Extra thick touch targeting profile
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 15, 15, 15); // Clear vertical buffer padding allocation block
        add(btnLogin, gbc);

        // --- AUTHENTICATION ACTION ENGINE ---
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            try {
                Employee employee = authService.authenticate(username, password);
                
                if (employee != null) {
                    mainFrame.setCurrentEmployeeId(employee.getEmployeeId());
                    String role = employee.getRole();
                    
                    // --- FIXED INTER-PANEL ROUTING ROUTINE LOOPHOLE ---
                    // Locks active database query validation role ("ADMIN" / "STAFF") into core session profile context state tracking matrix.
                    mainFrame.setCurrentUserRole(role); 
                    
                    // Clear inputs upon successful validation for security
                    txtUsername.setText("");
                    txtPassword.setText("");
                    
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        mainFrame.showCard("ADMIN_DASHBOARD");
                    } else if ("STAFF".equalsIgnoreCase(role)) {
                        mainFrame.showCard("STAFF_DASHBOARD");
                    } else {
                        JOptionPane.showMessageDialog(this, "Unrecognized User Access Level Profile Group.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Authentication Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}