package main.java.com.expensemanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private int id;
    private int profileId;
    private String name;
    private String description;
    private int totalTransaction;
    private double totalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate generatedDate;
    private List<Transaction> filteredTransactions;

    public Report(int profileId, String name, String description, List<Transaction> allTransactions,
                  LocalDate startDate, LocalDate endDate) {

        this.profileId = profileId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedDate = LocalDate.now();
        this.filteredTransactions = new ArrayList<>();

        // Tính toán các giá trị thống kê
        double tongTien = 0;
        int soGiaoDich = 0;

        for (Transaction t : allTransactions) {
            if (isInRange(t.getDate())) {
                filteredTransactions.add(t);
                tongTien += t.getAmount();
                soGiaoDich++;
            }
        }

        this.totalAmount = tongTien;
        this.totalTransaction = soGiaoDich;
    }

    private boolean isInRange(LocalDate date) {
        return (startDate == null || !date.isBefore(startDate)) &&
                (endDate == null || !date.isAfter(endDate));
    }

    public String exportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BÁO CÁO CHI TIẾT =====\n");
        sb.append("Tên báo cáo: ").append(name).append("\n");
        sb.append("Tài khoản ID: ").append(profileId).append("\n");
        sb.append("Thời gian: ").append(startDate).append(" đến ").append(endDate).append("\n");
        sb.append("Tổng số giao dịch: ").append(totalTransaction).append("\n");
        sb.append("Tổng số tiền: ").append(totalAmount).append(" VND\n");
        sb.append("Mô tả: ").append(description).append("\n\n");

        sb.append("Chi tiết giao dịch:\n");
        for (Transaction t : filteredTransactions) {
            sb.append("- ").append(t.getDate()).append(" | ")
                    .append(t.getType()).append(" | ")
                    .append("Mã thể loại: ").append(t.getCategoryId()).append(" | ")
                    .append(t.getAmount()).append(" VND\n");


        }
        return sb.toString();
    }

    // Getter & Setter

    public int getTotalTransaction() {
        return totalTransaction;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }
}
