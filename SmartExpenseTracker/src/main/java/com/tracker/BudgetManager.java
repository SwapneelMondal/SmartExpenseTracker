package com.tracker;

import java.sql.*;
import java.util.*;

public class BudgetManager {
    private final Connection conn;

    public BudgetManager() {
        this.conn = DBHelper.connect();
        createBudgetTable();
    }

    private void createBudgetTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS budgets (
                category TEXT PRIMARY KEY,
                budget_limit REAL
            )
        """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBudget(String category, double limit) {
        String sql = "INSERT OR REPLACE INTO budgets(category, budget_limit) VALUES(?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setDouble(2, limit);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getAllBudgets() {
        Map<String, Double> budgets = new HashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM budgets")) {
            while (rs.next()) {
                budgets.put(rs.getString("category"), rs.getDouble("budget_limit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }

    public double getBudget(String category) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT budget_limit FROM budgets WHERE category=?")) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("budget_limit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}