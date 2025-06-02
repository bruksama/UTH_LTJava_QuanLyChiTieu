package main.java.vn.edu.ut.expensemanager.service;

import main.java.vn.edu.ut.expensemanager.dao.ReportDAO;
import main.java.vn.edu.ut.expensemanager.dao.ConnectorDAO;
import main.java.vn.edu.ut.expensemanager.model.Report;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO;

    public ReportService() {
        Connection conn;
        try {
            conn = ConnectorDAO.getInstance().getConnection();
            this.reportDAO = new ReportDAO(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Không thể kết nối database cho ReportService", e);
        }
    }

    // ✅ Tạo báo cáo mới
    public boolean taoBaoCaoMoi(Report report) {
        if (report == null) return false;
        return reportDAO.addReport(report); // Đã sửa từ insertReport thành addReport
    }

    // ✅ Lấy danh sách báo cáo theo ID người dùng
    public List<Report> layDanhSachBaoCao(int profileId) {
        return reportDAO.getReportsByProfile(profileId); // Đã sửa từ getReportsByProfileId thành getReportsByProfile
    }

    // ⚠️ (Tuỳ chọn): Xoá báo cáo nếu có nhu cầu
    // public boolean xoaBaoCao(int reportId) {
    //     return reportDAO.deleteReport(reportId);
    // }

    // ⚠️ (Tuỳ chọn): Xuất báo cáo ra file văn bản nếu cần
    // public boolean exportReportToFile(Report report, String filePath) {
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    //         writer.write(report.exportText());
    //         return true;
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
}
