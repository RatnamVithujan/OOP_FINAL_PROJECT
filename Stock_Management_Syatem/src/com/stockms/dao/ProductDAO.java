package com.stockms.dao;

import com.stockms.model.Product;
import java.util.List;
import java.sql.SQLException;

public interface ProductDAO {
    List<Product> getAllProducts() throws SQLException;
    void updateQuantity(int productId, int newQuantity) throws SQLException;
}