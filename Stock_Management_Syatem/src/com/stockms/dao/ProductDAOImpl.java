package com.stockms.dao;

import com.stockms.model.Product;
import com.stockms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        // Updated to use your exact table 'stock'
        String sql = "SELECT stock_id, item_name, category, quantity, unit_price FROM stock"; 
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product p = new Product();
                // Mapped precisely to your column fields
                p.setProductId(rs.getInt("stock_id"));
                p.setProductName(rs.getString("item_name"));
                p.setCategory(rs.getString("category"));
                p.setQuantity(rs.getInt("quantity"));
                p.setUnitPrice(rs.getDouble("unit_price"));
                p.setLastUpdated(null); // Setting null since your schema doesn't use a timestamp
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public void updateQuantity(int productId, int newQuantity) throws SQLException {
        // Updated query targeting the correct table and unique PRI stock_id key
        String sql = "UPDATE stock SET quantity = ? WHERE stock_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }
}