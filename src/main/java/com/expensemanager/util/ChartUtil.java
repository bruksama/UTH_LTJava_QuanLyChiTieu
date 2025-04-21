package main.java.com.expensemanager.util;

import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import main.java.com.expensemanager.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ChartUtil {

    // Tạo biểu đồ cột thu/chi (Income vs Expense)
    public static VBox createIncomeExpenseChart(List<Transaction> allTransactions) {
        double incomeTotal = 0;
        double expenseTotal = 0;

        Transaction[] transactions = new Transaction[0];
        for (Transaction transaction : transactions) {
            if ("income".equals(transaction.getType())) {
                incomeTotal += transaction.getAmount();
            } else if ("expense".equals(transaction.getType())) {
                expenseTotal += transaction.getAmount();
            }
        }

        // Tạo trục X, Y
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Loại giao dịch");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("So sánh Thu và Chi");

        // Dữ liệu
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Thu", incomeTotal));
        series.getData().add(new XYChart.Data<>("Chi", expenseTotal));

        barChart.getData().add(series);
        return new VBox(barChart);
    }

    // Tạo biểu đồ tròn cho các danh mục chi tiêu
    public static Node createExpenseCategoryChart(List<Transaction> transactions, List<String> categories) {
        Map<String, Double> categoryTotalMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if ("expense".equals(transaction.getType())) {
                String category = transaction.getDescription();
                categoryTotalMap.put(category, categoryTotalMap.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Tỷ lệ chi tiêu theo danh mục");

        for (String category : categories) {
            Double total = categoryTotalMap.get(category);
            if (total != null && total > 0) {
                pieChart.getData().add(new PieChart.Data(category, total));
            }
        }

        return new VBox(pieChart);
    }


    public static PieChart createExpenseCategoryChart(List<Transaction> allTransactions) {
        return null;
    }
}
