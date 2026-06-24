package com.stockms.dao;

import com.stockms.model.Dealer;
import com.stockms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealerDAO {
    
    public List<Dealer> getAllDealers() throws SQLException {
        List<Dealer> dealers = new ArrayList<>();
        String sql = "SELECT * FROM dealer ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                dealers.add(new Dealer(
                    rs.getInt("dealer_id"),
                    rs.getString("name"),
                    rs.getString("contact_no"),
                    rs.getString("address")
                ));
            }
        }
        return dealers;
    }

    public void addDealer(Dealer dealer) throws SQLException {
        String sql = "INSERT INTO dealer (name, contact_no, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dealer.getName());
            ps.setString(2, dealer.getContactNo());
            ps.setString(3, dealer.getAddress());
            ps.executeUpdate();
        }
    }
}