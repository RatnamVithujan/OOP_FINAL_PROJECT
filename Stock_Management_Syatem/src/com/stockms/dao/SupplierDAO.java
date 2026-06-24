package com.stockms.dao;

import com.stockms.model.Supplier;
import com.stockms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_no"),
                    rs.getString("address")
                ));
            }
        }
        return suppliers;
    }

    public void addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO supplier (name, contact_no, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactNo());
            ps.setString(3, supplier.getAddress());
            ps.executeUpdate();
        }
    }
}