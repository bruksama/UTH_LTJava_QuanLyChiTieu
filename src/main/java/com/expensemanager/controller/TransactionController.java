package main.java.com.expensemanager.controller;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.util.SessionManagerUtil;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.model.Transaction;

import java.io.IOException;
import java.util.List;


public class TransactionController {
    @FXML
    private ListView<Transaction> transactionListView;

    @FXML
    private ComboBox<String> categoryComboBox;
    private ObservableList<Transaction> transactionObservableList;
    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;
    private int currentProfileId;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ToggleButton toggleTransactionButton;

    @FXML
    private ToggleButtonGroup navigationGroup;

    @FXML
    private TextField amountField;

    @FXML
    private Label totalIncomeLabel;

    @FXML
    private Label totalExpenseLabel;

    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void navigateTo(String fxmlFile, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) navigationGroup.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

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
            String text = selectedButton.getText();
            if ("Trang chủ".equals(text)) {
                navigateDashboard();
            } else if ("Danh mục".equals(text)) {
                navigateCategory();
            } else if ("Báo cáo".equals(text)) {
                navigateReport();
            }
        }
    }

    @FXML
    public void initialize() {
        // Khởi tạo các DAO
        transactionDAO = new TransactionDAO();
        categoryDAO = new CategoryDAO();
        //lấy profileId
        currentProfileId = SessionManager.getInstance().getCurrentProfileId();

        datePicker.setOnAction(event -> updateTotalsByDate(datePicker.getValue().toString()));

        // Khởi tạo ObservableList để liên kết với ListView
        transactionObservableList = FXCollections.observableArrayList();
        transactionListView.setItems(transactionObservableList);

        // Tùy chỉnh cách hiển thị các mục trong ListView
        transactionListView.setCellFactory(param -> new ListCell<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String categoryName = categoryDAO.getCategoryById(item.getCategoryId()).getName();
                    setText(categoryName + " - " + String.format("%,.0f", item.getAmount()) + "đ");
                }
            }
        });
        amountField.requestFocus();
        loadCategories();
        loadTransactions();
    }
    public void focusOnTextField() {
        amountField.requestFocus();
    }
    @FXML
    private void handleAddTransaction() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            // Hiển thị cảnh báo nếu người dùng không nhập số tiền
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Số tiền không hợp lệ", "Vui lòng nhập số tiền giao dịch.");
        } else {
            // Thực hiện logic để thêm giao dịch vào cơ sở dữ liệu hoặc các thao tác khác
            System.out.println("Thêm giao dịch với số tiền: " + amountText);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Giao dịch đã được thêm", "Số tiền giao dịch là: " + amountText);
        }
    }


    private void loadTransactions() {
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
        try {
            // Lấy tất cả giao dịch của profile hiện tại từ database
            transactionObservableList.setAll(transactionDAO.getTransactionsByProfile(currentProfileId));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải giao dịch",
                    "Đã xảy ra lỗi khi tải giao dịch từ cơ sở dữ liệu.");
        }
    }
    private void loadCategories() {
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        for (Category category : categoryDAO.getCategoriesByProfile(currentProfileId)) {
            categoryNames.add(category.getName());  // Thêm tên danh mục vào ComboBox
        }

        categoryComboBox.setItems(categoryNames);
    }
    private void updateTotalsByDate(String selectedDate) {
        if (selectedDate != null) {
            // Lấy tất cả giao dịch của profile hiện tại và lọc theo ngày
            List<Transaction> allTransactions = transactionDAO.getTransactionsByProfile(currentProfileId);
            double totalIncome = 0;
            double totalExpense = 0;

            // Lọc các giao dịch theo ngày đã chọn
            for (Transaction transaction : allTransactions) {
                if (transaction.getDate().toString().equals(selectedDate)) {
                    // Kiểm tra loại giao dịch và tính tổng
                    if (transaction.getType().equals(Transaction.TYPE_INCOME)) {
                        totalIncome += transaction.getAmount();  // Thêm vào tổng thu nhập
                    } else if (transaction.getType().equals(Transaction.TYPE_EXPENSE)) {
                        totalExpense += transaction.getAmount();  // Thêm vào tổng chi tiêu
                    }
                }
            }

            // Cập nhật Label với tổng thu và chi
            totalIncomeLabel.setText(String.format("%,.0fđ", totalIncome));
            totalExpenseLabel.setText(String.format("%,.0fđ", totalExpense));
        }
    }
}
