package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.Node;

import main.java.com.expensemanager.model.Transaction;
import main.java.com.expensemanager.service.ReportService;
import main.java.com.expensemanager.util.ChartUtil;

import java.util.List;

public class ReportController {

    @FXML
    private VBox chartContainer;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button btnViewChart;

    private ReportService reportService;

    public void initialize() {
        reportService = new ReportService();

        // Gán sự kiện nút "Xem biểu đồ"
        btnViewChart.setOnAction(e -> loadCharts());
    }

    private void loadCharts() {
        // Kiểm tra ngày đã chọn
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            System.out.println("Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.");
            return;
        }

        String from = fromDatePicker.getValue().toString();
        String to = toDatePicker.getValue().toString();

        List<Transaction> transactions = reportService.getTransactionsInRange(from, to);

        // Thống kê tổng thu - chi
        int totalIncome = 0;
        int totalExpense = 0;
        for (Transaction t : transactions) {
            if ("income".equalsIgnoreCase(t.getType())) {
                totalIncome += t.getAmount();
            } else if ("expense".equalsIgnoreCase(t.getType())) {
                totalExpense += t.getAmount();
            }
        }

        // Hiển thị thống kê
        Label summary = new Label("Tổng thu: " + totalIncome + " | Tổng chi: " + totalExpense);
        summary.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Tạo biểu đồ
        Node chart = ChartUtil.createIncomeExpenseChart(transactions);

        // Hiển thị lên giao diện
        chartContainer.getChildren().setAll(summary, chart);
    }
}
