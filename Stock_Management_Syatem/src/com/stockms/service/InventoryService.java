package com.stockms.service;

import com.stockms.dao.ProductDAO;
import com.stockms.dao.ProductDAOImpl;
import com.stockms.model.Product;
import com.stockms.util.DBConnection;

import java.sql.*;
import java.util.List;

public class InventoryService {
    private final ProductDAO productDAO = new ProductDAOImpl();

    public List<Product> loadInventory() throws SQLException {
        return productDAO.getAllProducts();
    }

    public void updateStockLevel(int productId, int currentQty, int changeQty, boolean isStockUp, int employeeId) throws SQLException {
        // 1. Calculate target levels
        int newQty = isStockUp ? (currentQty + changeQty) : (currentQty - changeQty);
        if (newQty < 0) {
            throw new IllegalArgumentException("Negative stock levels are completely blocked.");
        }
        
        // 2. Update the main stock table
        productDAO.updateQuantity(productId, newQty);

        // 3. Log transaction history with the real employeeId
        String sql = "INSERT INTO stock_transaction (stock_id, employee_id, type, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            double itemPrice = 0.0;
            String priceSql = "SELECT unit_price FROM stock WHERE stock_id = ?";
            try (PreparedStatement pricePs = conn.prepareStatement(priceSql)) {
                pricePs.setInt(1, productId);
                try (ResultSet rs = pricePs.executeQuery()) {
                    if (rs.next()) {
                        itemPrice = rs.getDouble("unit_price");
                    }
                }
            }

            String transactionType = isStockUp ? "PURCHASE" : "SALE";
            
            ps.setInt(1, productId);
            ps.setInt(2, employeeId); // Dynamic logged-in employee ID fixed here
            ps.setString(3, transactionType);
            ps.setInt(4, changeQty);
            ps.setDouble(5, itemPrice);
            
            ps.executeUpdate(); 
            System.out.println("Transaction successfully logged.");
        } catch (SQLException ex) {
            System.err.println("Transaction tracking logging failed: " + ex.getMessage());
        }
    }
}