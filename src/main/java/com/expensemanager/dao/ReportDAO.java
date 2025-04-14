package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Report;
import main.java.com.expensemanager.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection conn;


    // ✅ Thêm báo cáo vào bảng 'reports'
    public boolean insertReport(Report report) {
        if (report == null) {
            System.err.println("Báo cáo không hợp lệ.");
            return false;
        }

        String sql = "INSERT INTO reports (profileId, name, description, totalTransaction, totalAmount, startDate, endDate, generatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set các giá trị vào PreparedStatement
            stmt.setInt(1, report.getProfileId());
            stmt.setString(2, report.getName());
            stmt.setString(3, report.getDescription());
            stmt.setInt(4, report.getTotalTransaction());

            // Tính tổng số tiền từ các giao dịch
            double totalAmount = 0;
            for (Transaction t : report.getFilteredTransactions()) {
                totalAmount += t.getAmount();
            }
            stmt.setDouble(5, totalAmount);

            stmt.setString(6, report.getStartDate());
            stmt.setString(7, report.getEndDate());

            // Thực thi câu lệnh và kiểm tra kết quả
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Báo cáo đã được thêm thành công.");
                return true;
            } else {
                System.out.println("Không thể thêm báo cáo.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm báo cáo: " + e.getMessage());
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

            // Duyệt qua các dòng kết quả và tạo đối tượng Report
            while (rs.next()) {
                Report report = new Report(
                        rs.getInt("profileId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        new ArrayList<>(),  // Giả sử ban đầu không lấy giao dịch chi tiết
                        rs.getString("startDate"),
                        rs.getString("endDate")
                );
                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn báo cáo: " + e.getMessage());
            e.printStackTrace();
        }

        return reports;
    }
}
