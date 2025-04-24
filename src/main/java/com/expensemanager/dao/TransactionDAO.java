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

    public List<Transaction> getLatestFiveTransactionsByProfile(int profileId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE profileId = ? ORDER BY date DESC LIMIT 5";

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
    public double getTotalIncomeByDate(String date, int profileId) {
        double totalIncome = 0.0;
        String sql = "SELECT SUM(amount) AS total_income FROM transactions WHERE date(date ) = ? AND type = 'Thu' AND profileId = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            stmt.setInt(2, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalIncome = rs.getDouble("total_income");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng thu: " + e.getMessage());
        }
        return totalIncome;
    }
    public double getTotalExpenseByDate(String date, int profileId) {
        double totalExpense = 0.0;
        String sql = "SELECT SUM(amount) AS total_expense FROM transactions WHERE date(date) = ? AND type = 'Chi' AND profileId = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            stmt.setInt(2, profileId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalExpense = rs.getDouble("total_expense");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng chi: " + e.getMessage());
        }

        return totalExpense;
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
    public boolean deleteAllTransactionByProfileId(int profileId) {
        String sql = "DELETE FROM transactions WHERE profileId = ?";
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Đã xoá " + affectedRows + " giao dịch của hồ sơ ID: " + profileId);
                return true;
            } else {
                System.out.println("Không có giao dịch nào được xoá.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xoá giao dịch theo profileId: " + e.getMessage());
            return false;
        }
    }
}
