package com.tracker;

import java.sql.*;
import java.util.*;

public class ExpenseDAO {
    private final Connection conn;

    public ExpenseDAO() {
        this.conn = DBHelper.connect();
    }

    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT,
                category TEXT,
                amount REAL
            )
        """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM expenses")) {
            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertExpense(Expense e) {
        String sql = "INSERT INTO expenses(description, category, amount) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getDescription());
            ps.setString(2, e.getCategory());
            ps.setDouble(3, e.getAmount());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteExpense(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, Double> getCategoryTotals() {
        Map<String, Double> map = new HashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")) {
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
