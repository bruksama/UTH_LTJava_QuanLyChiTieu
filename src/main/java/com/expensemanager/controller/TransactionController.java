package main.java.com.expensemanager.controller;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import javafx.event.ActionEvent;
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
import java.util.Optional;

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
    private ToggleButton navigateDashboardBtn;

    @FXML
    private ToggleButton navigateCategoryBtn;

    @FXML
    private ToggleButton navigateReportBtn;

    @FXML
    private Button navigateReturnBtn;

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

    private void navigateDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) navigateDashboardBtn.getScene().getWindow();

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
    private void navigateLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Login.fxml"));
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
    @FXML
    public void initialize() {
        // Khởi tạo các DAO
        transactionDAO = new TransactionDAO();
        categoryDAO = new CategoryDAO();
        //lấy profileId
        currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        navigateReturnBtn.setOnAction(event -> navigateLogin());
        navigateDashboardBtn.setOnAction(event -> navigateDashboard());
        navigateCategoryBtn.setOnAction(event -> navigateCategory());
        navigateReportBtn.setOnAction(event -> navigateReport());
        addButton.setOnAction(event -> handleAddTransaction());
        // Khởi tạo ObservableList để liên kết với ListView
        transactionObservableList = FXCollections.observableArrayList();
        transactionListView.setItems(transactionObservableList);
        amountField.setOnAction(event -> handleAddTransaction());
        addButton.setOnAction(event -> handleAddTransaction());

        datePicker.setOnAction(event -> handleDatePicker());

        transactionListView.setOnContextMenuRequested(event -> {
            Transaction selectedTransaction = transactionListView.getSelectionModel().getSelectedItem();

            if (selectedTransaction != null) {
                // Nếu có giao dịch được chọn, hiển thị menu xóa
                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Xóa giao dịch");
                deleteItem.setOnAction(e -> handleDeleteTransaction(selectedTransaction)); // Gọi hàm xóa giao dịch

                contextMenu.getItems().add(deleteItem);
                contextMenu.show(transactionListView, event.getScreenX(), event.getScreenY()); // Hiển thị menu tại vị trí click
            }
        });

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
                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteMenuItem = new MenuItem("Xóa giao dịch");
                deleteMenuItem.setOnAction(event -> handleDeleteTransaction(item)); // Xử lý khi nhấn Xóa

                contextMenu.getItems().add(deleteMenuItem);
                setContextMenu(contextMenu);
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
        List<Transaction> transactions = transactionDAO.getTransactionsByProfile(currentProfileId);
        transactionObservableList.setAll(transactions);  // Sử dụng phương thức setAll để thay đổi danh sách
//        updateTotalIncomeExpense();
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
        String selectedCategoryName = categoryComboBox.getValue();

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
            totalIncomeLabel.setText(String.format("%,.đ", totalIncome));
            totalExpenseLabel.setText(String.format("%,.đ", totalExpense));
        }
    }
    private void handleDeleteTransaction(Transaction transaction) {
        // Cảnh báo xác nhận xóa giao dịch
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xác nhận xóa giao dịch");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa giao dịch \"" + transaction.getType() + ": " + transaction.getAmount() + "\"?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Tiến hành xóa giao dịch trong database
            boolean success = transactionDAO.deleteTransaction(transaction.getId());
            if (success) {
                // Cập nhật lại danh sách giao dịch sau khi xóa
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa giao dịch thành công", "Giao dịch đã được xóa thành công.");
                loadTransactions(); // Làm mới danh sách giao dịch
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa giao dịch", "Đã xảy ra lỗi khi xóa giao dịch.");
            }
        }
    }
    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        amountField.appendText(buttonText);
    }
    @FXML
    private void handleClear() {
        amountField.clear();
    }
    @FXML
    private void handleBackspace() {
        String currentText = amountField.getText();
        if (currentText.length() > 0) {
            amountField.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
}
