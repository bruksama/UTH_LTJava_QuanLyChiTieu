package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Report;
import main.java.com.expensemanager.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection conn;

    // Constructor nhận kết nối database
    public ReportDAO(Connection conn) {
        this.conn = conn;
        createReportsTableIfNotExists(); // Thêm để đảm bảo bảng luôn tồn tại
    }

    // ✅ Tạo bảng 'reports' nếu chưa có
    private void createReportsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS reports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "profileId INTEGER," +
                "name TEXT," +
                "description TEXT," +
                "totalTransaction INTEGER," +
                "totalAmount REAL," +
                "startDate TEXT," +
                "endDate TEXT," +
                "generatedDate TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Thêm báo cáo vào bảng 'reports'
    public boolean insertReport(Report report) {
        String sql = "INSERT INTO reports (profileId, name, description, totalTransaction, totalAmount, startDate, endDate, generatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, report.getProfileId());
            stmt.setString(2, report.getName());
            stmt.setString(3, report.getDescription());
            stmt.setInt(4, report.getTotalTransaction());

            double tongTien = 0;
            for (Transaction t : report.getFilteredTransactions()) {
                tongTien += t.getAmount();
            }
            stmt.setDouble(5, tongTien);

            stmt.setString(6, report.getStartDate());
            stmt.setString(7, report.getEndDate());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Lấy danh sách báo cáo theo profileId
    public List<Report> getReportsByProfileId(int profileId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE profileId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Chỉ tạo đối tượng Report chứa thông tin tổng quan, không cần danh sách giao dịch
                Report report = new Report(
                        rs.getInt("profileId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        new ArrayList<>(),
                        rs.getString("startDate"),
                        rs.getString("endDate")
                );
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
}
