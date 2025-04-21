package main.java.com.expensemanager.controller;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;


public class TransactionController {
    @FXML
    private ListView<String> transactionListView;

    @FXML
    private ToggleButton toggleTransactionButton;

    @FXML
    private ToggleButtonGroup navigationGroup;

    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Phương thức chuyển cảnh chung
    private void navigateTo(String fxmlFile, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));  // Đọc file FXML
            Parent root = loader.load();  // Tải giao diện mới

            Stage stage = (Stage) navigationGroup.getScene().getWindow();  // Lấy Stage hiện tại
            Scene scene = new Scene(root);  // Tạo Scene mới
            stage.setScene(scene);  // Thiết lập Scene mới
            stage.show();  // Hiển thị Stage

        } catch (IOException e) {
            System.err.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình", errorMessage + ": " + e.getMessage());
        }
    }

    // Phương thức điều hướng tới Dashboard
    @FXML
    private void navigateDashboard() {
        navigateTo("../view/Dashboard.fxml", "Lỗi khi chuyển đến Dashboard");
    }
    // Phương thức điều hướng tới Category
    @FXML
    private void navigateCategory() {
        navigateTo("../view/Category.fxml", "Lỗi khi chuyển đến Category");
    }

    @FXML
    private void navigateReport() {
        navigateTo("../view/Report.fxml", "Lỗi khi chuyển đến Report");
    }

    @FXML
    private void handleToggleSelection() {
        ToggleButton selectedButton = (ToggleButton) navigationGroup.getToggles();
        if (selectedButton != null) {
            String text = selectedButton.getText();  // Lấy tên của nút

            // Điều hướng dựa trên tên của nút được chọn
            if ("Trang chủ".equals(text)) {
                navigateDashboard();  // Chuyển đến Dashboard
            } else if ("Danh mục".equals(text)) {
                navigateCategory();  // Chuyển đến Category
            } else if ("Báo cáo".equals(text)) {
                navigateReport();  // Chuyển đến Report
            }
        }
    }

    @FXML
    public void initialize() {
        ObservableList<String> transactions = FXCollections.observableArrayList(
                "Giao dịch 1: 5000đ",
                "Giao dịch 2: 3000đ",
                "Giao dịch 3: 1000đ"
        );

        // Thêm dữ liệu vào ListView
        transactionListView.setItems(transactions);
        // Tùy chỉnh cách hiển thị các mục trong ListView
        transactionListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                    }
                };
            }
        });

    }
}
