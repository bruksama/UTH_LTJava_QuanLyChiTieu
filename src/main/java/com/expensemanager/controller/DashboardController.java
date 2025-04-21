package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController implements Initializable {

    @FXML
    private ToggleButton navigateTransactionBtn;
    @FXML
    private ToggleButton navigateCategoryBtn;
    @FXML
    private ToggleButton navigateReportBtn;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuButton menuButton1;
    @FXML
    private Button buttonProfile;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        navigateCategoryBtn.setOnAction(event -> navigateCategory ());
        navigateTransactionBtn.setOnAction(event -> navigateTransaction ());
        navigateReportBtn.setOnAction(event -> navigateReport ());
        buttonProfile.setOnAction(event ->navigateProfile () );

    }

    // Thiết lập các hành động cho menuButton
//    private void setMenuButtonActions() {
//        // Lựa chọn "Xóa Profile"
//        MenuItem deleteItem = new MenuItem("Xóa Profile");
//        deleteItem.setOnAction(event -> handleDeleteProfile());
//
//        // Lựa chọn "Đổi tên Profile"
//        MenuItem editItem = new MenuItem("Đổi tên Profile");
//        editItem.setOnAction(event -> handleEditProfile());
//
//        // Thêm các items vào menuButton
//        menuButton.getItems().clear();
//        menuButton.getItems().addAll(deleteItem, editItem);
//    }
//
//    // Xử lý sự kiện xóa profile
//    private void handleDeleteProfile() {
//        // Logic để xóa profile
//        showAlert(Alert.AlertType.INFORMATION, "Xóa Profile", "Profile đã được xóa", "Profile đã được xóa thành công!");
//    }
//
//    // Xử lý sự kiện chỉnh sửa profile
//    private void handleEditProfile() {
//        // Logic để chỉnh sửa profile
//        showAlert(Alert.AlertType.INFORMATION, "Đổi tên Profile", "Profile đã được đổi tên", "Profile đã được đổi tên thành công!");
//    }
//
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

    private void navigateProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Profile.fxml"));
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

}