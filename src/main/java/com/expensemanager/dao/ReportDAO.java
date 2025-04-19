package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Report;
import main.java.com.expensemanager.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private final ConnectorDAO connector;

    public ReportDAO(Connection conn) {
        this.connector = ConnectorDAO.getInstance();
    }

    // Thêm báo cáo
    public boolean addReport(Report report) {
        String sql = "INSERT INTO reports (profileId, name, description, totalTransaction, totalAmount, startDate, endDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, report.getProfileId());
            stmt.setString(2, report.getName());
            stmt.setString(3, report.getDescription());
            stmt.setInt(4, report.getTotalTransaction());  // Đảm bảo rằng totalTransaction đã được tính toán trong Report
            stmt.setDouble(5, report.getTotalAmount());   // Đảm bảo rằng totalAmount đã được tính toán trong Report
            stmt.setString(6, report.getStartDate());
            stmt.setString(7, report.getEndDate());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm báo cáo: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách báo cáo theo profileId
    public List<Report> getReportsByProfile(int profileId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE profileId = ? ORDER BY generatedDate DESC";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Khởi tạo báo cáo
                Report report = new Report(
                        rs.getInt("profileId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        new ArrayList<>(),  // Ban đầu không có giao dịch chi tiết
                        rs.getString("startDate"),
                        rs.getString("endDate")
                );

                // Thêm danh sách giao dịch vào báo cáo
                List<Transaction> transactions = getTransactionsForReport(report);
                report.setFilteredTransactions(transactions);

                reports.add(report);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách báo cáo: " + e.getMessage());
        }

        return reports;
    }

    // Lấy danh sách giao dịch cho một báo cáo
    private List<Transaction> getTransactionsForReport(Report report) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE profileId = ? AND date BETWEEN ? AND ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, report.getProfileId());
            stmt.setString(2, report.getStartDate());
            stmt.setString(3, report.getEndDate());
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
            System.err.println("Lỗi khi lấy giao dịch cho báo cáo: " + e.getMessage());
        }

        return transactions;
    }

    // Xóa báo cáo theo ID
    public boolean deleteReport(int id) {
        String sql = "DELETE FROM reports WHERE id = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa báo cáo: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật báo cáo (nếu cần thiết)
    public boolean updateReport(Report report) {
        String sql = "UPDATE reports SET name = ?, description = ?, totalTransaction = ?, totalAmount = ?, startDate = ?, endDate = ? WHERE id = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getName());
            stmt.setString(2, report.getDescription());
            stmt.setInt(3, report.getTotalTransaction());
            stmt.setDouble(4, report.getTotalAmount());
            stmt.setString(5, report.getStartDate());
            stmt.setString(6, report.getEndDate());
            stmt.setInt(7, report.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật báo cáo: " + e.getMessage());
            return false;
        }
    }

    public ConnectorDAO getConnector() {
        return connector;
    }
}
