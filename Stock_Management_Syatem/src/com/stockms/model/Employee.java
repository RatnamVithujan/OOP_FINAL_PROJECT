package com.stockms.model;

public class Employee {
    private int employeeId;
    private String fullName;
    private String role; // "ADMIN" or "STAFF"
    private String username;
    private String passwordHash;
    private String contactNo;
    private String status; // "ACTIVE" or "INACTIVE"

    // Default Constructor
    public Employee() {}

    // Parameterized Constructor
    public Employee(int employeeId, String fullName, String role, String username, String passwordHash, String contactNo, String status) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.contactNo = contactNo;
        this.status = status;
    }

    // Getters and Setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}