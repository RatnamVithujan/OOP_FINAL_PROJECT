package com.stockms.dao;

import com.stockms.model.Employee;
import com.stockms.util.DBConnection;
import java.sql.*;

public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public Employee getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM employee WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmployeeId(rs.getInt("employee_id"));
                    emp.setFullName(rs.getString("full_name"));
                    emp.setRole(rs.getString("role"));
                    emp.setUsername(rs.getString("username"));
                    emp.setPasswordHash(rs.getString("password_hash"));
                    emp.setContactNo(rs.getString("contact_no"));
                    emp.setStatus(rs.getString("status"));
                    return emp; 
                }
            }
        }
        return null; // Returns null cleanly if username isn't found
    }
}