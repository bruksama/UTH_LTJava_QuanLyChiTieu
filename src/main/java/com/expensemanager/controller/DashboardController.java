package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import main.java.com.expensemanager.util.SessionManager;
import main.java.com.expensemanager.service.ProfileService;
import main.java.com.expensemanager.model.Profile;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label profileNameLabel; // Label để hiển thị tên của profile

    private ProfileService profileService;  // Dịch vụ quản lý profile

    // Constructor
    public DashboardController() {
        this.profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO());
    }

    // Hàm initialize sẽ được gọi khi giao diện Dashboard được load
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lấy profileId từ SessionManager
        int profileId = SessionManager.getInstance().getCurrentProfileId();

        // Lấy thông tin profile từ profileId
        Profile profile = profileService.getProfile(profileId);

        // Hiển thị tên profile nếu profile tồn tại
        if (profile != null) {
            profileNameLabel.setText("Welcome, " + profile.getName());  // Hiển thị tên profile
        } else {
            profileNameLabel.setText("Profile not found.");
        }

        // Thêm logic cho các biểu đồ hoặc thông tin khác nếu cần
        // Ví dụ, bạn có thể thêm code để vẽ biểu đồ, thống kê giao dịch, v.v.
        setupBarChart();
    }

    // Hàm cài đặt biểu đồ (BarChart)
    private void setupBarChart() {
        // Giả sử bạn có dữ liệu để vẽ biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Transactions");

        // Thêm dữ liệu mẫu vào biểu đồ (ở đây bạn có thể thay thế với dữ liệu thực)
        series.getData().add(new XYChart.Data<>("January", 100));
        series.getData().add(new XYChart.Data<>("February", 120));
        series.getData().add(new XYChart.Data<>("March", 140));

        // Thêm dữ liệu vào BarChart
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.getData().add(series);
    }
}