package com.stockms.dao;

import com.stockms.model.Employee;
import java.sql.SQLException;

public interface EmployeeDAO {
    Employee getByUsername(String username) throws SQLException;
}