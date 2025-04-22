package main.java.com.expensemanager.controller;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.util.SessionManagerUtil;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.model.Transaction;

import java.io.IOException;
import java.time.LocalDate;
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
    private Button addButton;

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
        currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();


        addButton.setOnAction(event -> handleAddTransaction());
        // Khởi tạo ObservableList để liên kết với ListView
        transactionObservableList = FXCollections.observableArrayList();
        transactionListView.setItems(transactionObservableList);

        datePicker.setOnAction(event -> handleDatePicker());

        // Tùy chỉnh cách hiển thị các mục trong ListView
        transactionListView.setCellFactory(param -> new ListCell<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Lấy tên danh mục từ categoryId
                    String categoryName = "Không có danh mục";  // Mặc định là "Không có danh mục"
                    try {
                        Category category = categoryDAO.getCategoryById(item.getCategoryId());
                        if (category != null) {
                            categoryName = category.getName();
                        }
                    } catch (Exception e) {
                        System.err.println("Không tìm thấy danh mục cho giao dịch: " + e.getMessage());
                    }

                    // Tạo một HBox để căn chỉnh các phần tử
                    HBox hbox = new HBox();
                    Label typeLabel = new Label(item.getType() + ": ");
                    Label amountLabel = new Label(String.format("%,.0fđ", item.getAmount()));
                    Label categoryLabel = new Label("(" + categoryName + ")");

                    // Căn chỉnh các phần tử trong HBox
                    hbox.getChildren().addAll(typeLabel, amountLabel, categoryLabel);
                    HBox.setHgrow(amountLabel, Priority.ALWAYS); // Căn giữa amountLabel

                    // Cập nhật lại nội dung của ListCell
                    setGraphic(hbox);
                }
            }
        });
        loadCategories();
        loadTransactions();
    }

    public void focusOnTextField() {
        amountField.requestFocus();
    }

    @FXML

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

    @FXML
    private void handleAddTransaction() {
        String amountText = amountField.getText().trim();
        String selectedCategoryName = categoryComboBox.getValue(); // Lấy tên danh mục đã chọn

        if (amountText.isEmpty() || selectedCategoryName == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Thông tin chưa đầy đủ",
                    "Vui lòng nhập số tiền và chọn danh mục.");
            return;
        }

        // Kiểm tra số tiền có hợp lệ không
        double amount = 0;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Số tiền không hợp lệ",
                        "Số tiền giao dịch phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Số tiền không hợp lệ",
                    "Vui lòng nhập một số tiền hợp lệ.");
            return;
        }

        int categoryId = -1;
        String transactionType = "";
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        List<Category> categories = categoryDAO.getCategoriesByProfile(currentProfileId);

        for (Category category : categories) {
            if (category.getName().equals(selectedCategoryName)) {
                categoryId = category.getId();
                transactionType = category.getType();
                break;
            }
        }

        if (categoryId == -1) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Danh mục không tồn tại", "Danh mục bạn chọn không có trong cơ sở dữ liệu.");
            return;
        }

        // Lấy ngày hiện tại
        String transactionDateString = LocalDate.now().toString(); // Chuyển LocalDate thành chuỗi (YYYY-MM-DD)

        // Tạo đối tượng giao dịch mới với ngày và giờ hiện tại
        Transaction newTransaction = new Transaction(
                0, // id tự động tăng, nên gán là 0
                currentProfileId, // lấy profileId từ SessionManager
                transactionType, // Loại giao dịch (Thu hoặc Chi)
                categoryId, // id danh mục
                "Mô tả giao dịch", // Mô tả, có thể tùy chỉnh
                transactionDateString, // Ngày giao dịch
                amount // Số tiền
        );

        // Thêm giao dịch vào cơ sở dữ liệu
        boolean success = transactionDAO.addTransaction(newTransaction);

        // Thông báo người dùng và làm mới danh sách giao dịch
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm giao dịch thành công",
                    "Giao dịch đã được thêm vào cơ sở dữ liệu.");
            loadTransactions();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm giao dịch",
                    "Đã xảy ra lỗi khi thêm giao dịch vào cơ sở dữ liệu.");
        }

        // Reset amountField và categoryComboBox sau khi thêm giao dịch
        amountField.clear();  // Xóa dữ liệu trong TextField số tiền
        categoryComboBox.setValue("Chọn danh mục");
    }

    @FXML
    private void handleDatePicker() {
        String selectedDate;

        // Kiểm tra nếu người dùng đã chọn ngày
        if (datePicker.getValue() != null) {
            selectedDate = datePicker.getValue().toString(); // Lấy ngày từ DatePicker (YYYY-MM-DD)
        } else {
            // Nếu không chọn ngày, dùng ngày hôm nay làm mặc định
            selectedDate = LocalDate.now().toString();
        }

        updateTotalsByDate(selectedDate);
    }

    private void updateTotalsByDate(String selectedDate) {
        if (selectedDate != null) {
            // Lấy tất cả giao dịch của profile hiện tại và lọc theo ngày
            List<Transaction> allTransactions = transactionDAO.getTransactionsByProfile(currentProfileId);
            double totalIncome = 0;
            double totalExpense = 0;

            // Lọc các giao dịch theo ngày đã chọn
            for (Transaction transaction : allTransactions) {
                if (transaction.getDate().equals(selectedDate)) {
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
