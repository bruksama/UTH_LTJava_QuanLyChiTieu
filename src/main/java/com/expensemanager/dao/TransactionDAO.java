package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final ConnectorDAO connector;

    public TransactionDAO() {
        this.connector = ConnectorDAO.getInstance();
    }

    // Thêm giao dịch
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (profileId, type, categoryId, description, amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getProfileId());
            stmt.setString(2, transaction.getType());
            stmt.setInt(3, transaction.getCategoryId());
            stmt.setString(4, transaction.getDescription());
            stmt.setDouble(5, transaction.getAmount());

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Giao dịch đã được thêm vào cơ sở dữ liệu.");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm giao dịch: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách giao dịch theo profileId
    public List<Transaction> getTransactionsByProfile(int profileId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE profileId = ? ORDER BY date DESC";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("profileId"),
                        rs.getString("type"),
                        rs.getInt("categoryId"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getDouble("amount")
                );
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách giao dịch: " + e.getMessage());
        }

        return transactions;
    }

    // Xóa giao dịch theo ID
    public boolean deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Giao dịch đã được xoá khỏi hệ thống.");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xoá giao dịch: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật giao dịch (nếu muốn)
    public boolean updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET type = ?, categoryId = ?, description = ?, amount = ? WHERE id = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getType());
            stmt.setInt(2, transaction.getCategoryId());
            stmt.setString(3, transaction.getDescription());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setInt(5, transaction.getId());

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Giao dịch đã được cập nhật thành công.");
            }
            return success;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật giao dịch: " + e.getMessage());
            return false;
        }
    }
}
