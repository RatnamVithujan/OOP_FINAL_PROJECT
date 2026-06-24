package com.stockms.dao;

import com.stockms.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginLogDAOImpl implements LoginLogDAO {

    @Override
    public void recordLogin(int employeeId) throws SQLException {
        String sql = "INSERT INTO login_log (employee_id) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.executeUpdate();
        }
    }
}//bbbbbbbbbbbbbbbbbbbbbbbbbbbbb