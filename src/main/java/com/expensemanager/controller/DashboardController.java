package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

import java.awt.*;

public class DashboardController implements Initializable {

    @FXML
    private ToggleButton navigateTransactionBtn;
    @FXML
    private ToggleButton navigateCategoryBtn;
    @FXML
    private ToggleButton navigateReportBtn;
    @FXML
    private Button addTransaction;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        navigateCategoryBtn.setOnAction(event -> navigateCategory ());
        navigateTransactionBtn.setOnAction(event -> navigateTransaction ());
        navigateReportBtn.setOnAction(event -> navigateReport ());
        addTransaction.setOnAction(event -> navigateToTransaction());
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Transaction.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) navigateTransactionBtn.getScene().getWindow();

            // Thiết lập scene mới
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình chính",
                    "Đã xảy ra lỗi khi chuyển đến màn hình chính: " + e.getMessage());
        }
    }

    private void navigateReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Report.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) navigateReportBtn.getScene().getWindow();

            // Thiết lập scene mới
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình chính",
                    "Đã xảy ra lỗi khi chuyển đến màn hình chính: " + e.getMessage());
        }
    }

    private void navigateCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Category.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) navigateCategoryBtn.getScene().getWindow();

            // Thiết lập scene mới
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình chính",
                    "Đã xảy ra lỗi khi chuyển đến màn hình chính: " + e.getMessage());
        }
    }
    @FXML
    private void navigateToTransaction() {
        try {
            // Tải màn hình Transaction.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Transaction.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addTransaction.getScene().getWindow(); // Lấy Stage hiện tại
            Scene scene = new Scene(root); // Tạo Scene mới
            stage.setScene(scene); // Thay thế Scene cũ bằng Scene mới
            stage.show(); // Hiển thị màn hình mới

            // Lấy controller của Transaction.fxml
            TransactionController transactionController = loader.getController();
            transactionController.focusOnTextField(); // Đặt focus vào TextField trong Transaction

        } catch (IOException e) {
            // Hiển thị thông báo lỗi nếu có vấn đề khi chuyển màn hình
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể chuyển màn hình", "Đã xảy ra lỗi khi chuyển đến giao diện Giao dịch: " + e.getMessage());
        }
    }
}