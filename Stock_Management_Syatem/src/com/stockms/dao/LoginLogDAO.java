package com.stockms.dao;

import java.sql.SQLException;

public interface LoginLogDAO {
    // Saves a login session event linked to the specific employee who logged in
    void recordLogin(int employeeId) throws SQLException;
}