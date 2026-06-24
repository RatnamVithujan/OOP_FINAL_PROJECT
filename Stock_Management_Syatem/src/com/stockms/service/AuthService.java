package com.stockms.service;

import com.stockms.dao.EmployeeDAO;
import com.stockms.dao.EmployeeDAOImpl;
import com.stockms.dao.LoginLogDAO;
import com.stockms.dao.LoginLogDAOImpl;
import com.stockms.exception.InvalidLoginException;
import com.stockms.model.Employee;
import com.stockms.util.PasswordUtil;
import java.sql.SQLException;

public class AuthService {
    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final LoginLogDAO loginLogDAO = new LoginLogDAOImpl();

    public Employee authenticate(String username, String plainPassword) throws InvalidLoginException, SQLException {
        // 1. Basic validation inputs check
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            throw new InvalidLoginException("Username and Password fields cannot be empty.");
        }

        // 2. Fetch employee row from database
        Employee employee = employeeDAO.getByUsername(username.trim());
        
        // 3. Check if username exists
        if (employee == null) {
            throw new InvalidLoginException("Invalid username or password.");
        }

        // 4. Check if account status is active
        if (!"ACTIVE".equalsIgnoreCase(employee.getStatus())) {
            throw new InvalidLoginException("Account is inactive. Please contact your System Administrator.");
        }

        // 5. Verify input password against database entry (Supports both hashed and plain-text database entries)
        boolean isPasswordCorrect = false;
        try {
            // Try standard hashing comparison first
            isPasswordCorrect = PasswordUtil.verify(plainPassword, employee.getPasswordHash());
        } catch (Exception e) {
            isPasswordCorrect = false;
        }

        // UNIVERSAL PLAIN-TEXT FALLBACK: If hashing fails, match direct raw strings for ANY user (vithu, admin, etc.)
        if (!isPasswordCorrect) {
            isPasswordCorrect = plainPassword.equals(employee.getPasswordHash());
        }

        if (!isPasswordCorrect) {
            throw new InvalidLoginException("Invalid username or password.");
        }

        // 6. Success! Write a history row to the login audit log tracker table
        loginLogDAO.recordLogin(employee.getEmployeeId());

        // Return the authenticated object containing the user's role (ADMIN/STAFF)
        return employee;
    }
}