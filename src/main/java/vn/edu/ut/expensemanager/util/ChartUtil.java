package main.java.vn.edu.ut.expensemanager.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import main.java.vn.edu.ut.expensemanager.model.Transaction;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ChartUtil {

    // Hàm tạo biểu đồ cột cho các giao dịch theo loại (income/expense)
    public static JPanel createIncomeExpenseChart(List<Transaction> transactions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double incomeTotal = 0;
        double expenseTotal = 0;

        // Tính tổng thu nhập và chi tiêu
        for (Transaction transaction : transactions) {
            if ("income".equals(transaction.getType())) {
                incomeTotal += transaction.getAmount();
            } else if ("expense".equals(transaction.getType())) {
                expenseTotal += transaction.getAmount();
            }
        }

        // Thêm dữ liệu vào dataset
        dataset.addValue(incomeTotal, "Income", "Income");
        dataset.addValue(expenseTotal, "Expense", "Expense");

        // Tạo và trả về biểu đồ cột
        return createBarChart(dataset, "Income vs Expense", "Category", "Amount");
    }

    // Hàm tạo biểu đồ tròn cho các danh mục chi tiêu
    public static JPanel createExpenseCategoryChart(List<Transaction> transactions, List<String> categories) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Sử dụng Map để lưu trữ tổng chi tiêu cho mỗi danh mục
        Map<String, Double> categoryTotalMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if ("expense".equals(transaction.getType())) {
                String category = transaction.getDescription();
                categoryTotalMap.put(category, categoryTotalMap.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }

        // Thêm dữ liệu vào dataset
        for (String category : categories) {
            Double categoryTotal = categoryTotalMap.get(category);
            if (categoryTotal != null) {
                dataset.setValue(category, categoryTotal);
            }
        }

        // Tạo và trả về biểu đồ tròn
        return createPieChart(dataset, "Expense Category Distribution");
    }

    // Tạo biểu đồ cột (Bar Chart) với dataset đã có
    private static JPanel createBarChart(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,         // Tiêu đề
                xAxisLabel,    // Tiêu đề trục X
                yAxisLabel,    // Tiêu đề trục Y
                dataset,       // Dữ liệu
                PlotOrientation.VERTICAL, // Hướng
                false,         // Hiển thị legend
                true,          // Hiển thị tooltips
                false          // Hiển thị URL
        );

        return new ChartPanel(chart);
    }

    // Tạo biểu đồ tròn (Pie Chart) với dataset đã có
    private static JPanel createPieChart(DefaultPieDataset dataset, String title) {
        JFreeChart chart = ChartFactory.createPieChart(
                title,         // Tiêu đề
                dataset,       // Dữ liệu
                true,          // Hiển thị legend
                true,          // Hiển thị tooltips
                false          // Hiển thị URL
        );

        return new ChartPanel(chart);
    }
}
