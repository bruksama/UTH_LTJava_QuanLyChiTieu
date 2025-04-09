package main.java.com.expensemanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//report
public class Report {
    private double totalIncome;
    private double totalExpense;
    private double balance;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Transaction> filteredTransactions;

    public Report(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.filteredTransactions = new ArrayList<>();
        this.totalIncome = 0;
        this.totalExpense = 0;

        for (Transaction t : transactions) {
            if (isInRange(t.getDate())) {
                filteredTransactions.add(t);
                if (t.getType().equalsIgnoreCase("income")) {
                    totalIncome += t.getAmount();
                } else if (t.getType().equalsIgnoreCase("expense")) {
                    totalExpense += t.getAmount();
                }
            }
        }

        this.balance = totalIncome - totalExpense;
    }

    private boolean isInRange(LocalDate date) {
        if (startDate == null || endDate == null) return true;
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isEqual(endDate) || date.isBefore(endDate));
    }

    public String exportReportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BÁO CÁO THỐNG KÊ =====\n");
        if (startDate != null && endDate != null) {
            sb.append("Từ ngày ").append(startDate)
                    .append(" đến ").append(endDate).append("\n");
        }
        sb.append("Tổng thu nhập: ").append(totalIncome).append(" VND\n");
        sb.append("Tổng chi tiêu: ").append(totalExpense).append(" VND\n");
        sb.append("Số dư: ").append(balance).append(" VND\n");
        sb.append("Chi tiết giao dịch:\n");

        for (Transaction t : filteredTransactions) {
            sb.append("- ").append(t.getDate()).append(" | ")
                    .append(t.getType()).append(" | ")
                    .append(t.getCategory()).append(" | ")
                    .append(t.getAmount()).append(" VND\n");
        }

        return sb.toString();
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getFilteredTransactions() {
        return filteredTransactions;
    }
}
