package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.model.Transaction;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private ToggleButton homeButton;
    @FXML
    private Button addTransactionButton;
    @FXML
    private MenuItem deleteProfileMenuItem;
    @FXML
    private MenuItem renameProfileMenuItem;

    private CategoryDAO categoryDAO;
    private TransactionDAO transactionDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryDAO = new CategoryDAO(); // Khởi tạo CategoryDAO để truy vấn category từ categoryId
        transactionDAO = new TransactionDAO(); // Khởi tạo TransactionDAO để lấy giao dịch từ cơ sở dữ liệu

        loadDashboardData(); // Load dữ liệu khi trang Dashboard được mở

        // Event handling for adding transactions
        addTransactionButton.setOnAction(event -> handleAddTransaction());

        // Event handling for renaming profile
        renameProfileMenuItem.setOnAction(event -> handleRenameProfile());

        // Event handling for deleting profile
        deleteProfileMenuItem.setOnAction(event -> handleDeleteProfile());
    }

    // Cập nhật biểu đồ với danh sách giao dịch
    public void updateBarChart(List<Transaction> transactions) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chi tiêu tháng này");

        if (transactions != null && !transactions.isEmpty()) {
            for (Transaction t : transactions) {
                // Lấy thông tin category từ categoryId trong Transaction
                Category category = categoryDAO.getCategoryById(t.getCategoryId());
                if (category != null) {
                    // Lấy tên category từ đối tượng Category
                    String categoryName = category.getName();
                    series.getData().add(new XYChart.Data<>(categoryName, t.getAmount()));
                }
            }
        } else {
            // Nếu không có giao dịch, bạn có thể hiển thị dữ liệu mẫu
            series.getData().add(new XYChart.Data<>("Ăn uống", 100000));
            series.getData().add(new XYChart.Data<>("Di chuyển", 50000));
            series.getData().add(new XYChart.Data<>("Mua sắm", 70000));
        }

        barChart.getData().clear(); // Xóa dữ liệu cũ
        barChart.getData().add(series); // Thêm dữ liệu mới
    }

    // Tải dữ liệu và hiển thị các giao dịch gần đây
    private void loadDashboardData() {
        // Giả sử bạn lấy giao dịch của profile ID là 1 (thay thế với logic thực tế của bạn)
        List<Transaction> transactions = transactionDAO.getTransactionsByProfile(1);
        updateBarChart(transactions);
    }

    // Xử lý sự kiện "Thêm giao dịch"
    private void handleAddTransaction() {
        System.out.println("Thêm giao dịch mới.");
        // Logic mở cửa sổ hoặc thêm giao dịch mới sẽ được triển khai ở đây
    }

    // Xử lý sự kiện "Đổi tên hồ sơ"
    private void handleRenameProfile() {
        System.out.println("Đổi tên hồ sơ.");
        // Logic đổi tên hồ sơ sẽ được triển khai ở đây
    }

    // Xử lý sự kiện "Xóa hồ sơ"
    private void handleDeleteProfile() {
        System.out.println("Xóa hồ sơ.");
        // Logic xóa hồ sơ sẽ được triển khai ở đây
    }
}
