package main.java.com.expensemanager.dao;

import main.java.com.expensemanager.model.Report;
import main.java.com.expensemanager.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ReportDAO {
    private final Connection conn;

    public ReportDAO(Connection conn) {
        this.conn = conn;
    }

    // Hàm chèn báo cáo mới vào bảng reports
    public boolean insertReport(Report report) {
        String sql = "INSERT INTO reports (profileId, name, description, totalTransaction, totalAmount, startDate, endDate, generatedDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, report.getProfileId());
            stmt.setString(2, report.getName());
            stmt.setString(3, report.getDescription());
            stmt.setInt(4, report.getTotalTransaction());

            // Tính tổng số tiền từ danh sách giao dịch đã lọc
            double tongTien = 0;
            List<Transaction> dsGiaoDich = report.getFilteredTransactions();
            if (dsGiaoDich != null) {
                for (Transaction t : dsGiaoDich) {
                    tongTien += t.getAmount();
                }
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
}
