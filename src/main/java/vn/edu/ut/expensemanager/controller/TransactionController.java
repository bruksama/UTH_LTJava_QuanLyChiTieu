package main.java.vn.edu.ut.expensemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.vn.edu.ut.expensemanager.model.Category;
import main.java.vn.edu.ut.expensemanager.util.SessionManagerUtil;
import main.java.vn.edu.ut.expensemanager.dao.TransactionDAO;
import main.java.vn.edu.ut.expensemanager.dao.CategoryDAO;
import main.java.vn.edu.ut.expensemanager.model.Transaction;

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
    private TextField descriptionField;

    @FXML
    private Label totalIncomeLabel;

    @FXML
    private Label totalExpenseLabel;

    @FXML
    private Button addButton;

    @FXML
    public void initialize() {
        transactionDAO = new TransactionDAO();
        categoryDAO = new CategoryDAO();
        currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        navigateReturnBtn.setOnAction(e -> navigateLogin());
        navigateDashboardBtn.setOnAction(e -> navigateDashboard());
        navigateCategoryBtn.setOnAction(e -> navigateCategory());
        navigateReportBtn.setOnAction(e -> navigateReport());
        addButton.setOnAction(e -> handleAddTransaction());

        transactionObservableList = FXCollections.observableArrayList();
        transactionListView.setItems(transactionObservableList);

        amountField.setOnAction(e -> handleAddTransaction());
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> loadTransactions());

        transactionListView.setOnContextMenuRequested(event -> {
            Transaction selected = transactionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Xóa giao dịch");
                deleteItem.setOnAction(e -> handleDeleteTransaction(selected));
                contextMenu.getItems().add(deleteItem);
                contextMenu.show(transactionListView, event.getScreenX(), event.getScreenY());
            }
        });

        transactionListView.setCellFactory(param -> new ListCell<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                    return;
                }

                String categoryName = "Không có danh mục";
                try {
                    if (categoryDAO.isCategoryExist(item.getCategoryId(), currentProfileId)) {
                        Category category = categoryDAO.getCategoryById(item.getCategoryId());
                        if (category != null) categoryName = category.getName();
                    } else {
                        System.err.println("Danh mục với ID " + item.getCategoryId() + " không tồn tại.");
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy danh mục: " + e.getMessage());
                }

                Label typeLabel = new Label(item.getType() + ": ");
                Label amountLabel = new Label(String.format("%,.0fđ", item.getAmount()));
                Label categoryLabel = new Label("(" + categoryName + ")");

                HBox topLine = new HBox(typeLabel, amountLabel, categoryLabel);
                HBox.setHgrow(amountLabel, Priority.ALWAYS);

                VBox vbox = new VBox();
                vbox.getChildren().add(topLine);

                String description = item.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    Label descriptionLabel = new Label("Mô tả: " + description);
                    descriptionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
                    vbox.getChildren().add(descriptionLabel);
                }

                setGraphic(vbox);

                ContextMenu contextMenu = new ContextMenu();
                MenuItem deleteMenuItem = new MenuItem("Xóa giao dịch");
                deleteMenuItem.setOnAction(event -> handleDeleteTransaction(item));
                contextMenu.getItems().add(deleteMenuItem);
                setContextMenu(contextMenu);
            }
        });

        loadCategories();
        loadTransactions();
    }

    @FXML
    private void loadTransactions() {
        LocalDate selectedDate = (datePicker.getValue() != null) ? datePicker.getValue() : LocalDate.now();
        String formattedDate = selectedDate.toString();
        List<Transaction> transactions = transactionDAO.getTransactionsByDate(currentProfileId, formattedDate);
        transactionObservableList.setAll(transactions);
        updateTotalByDate();
    }

    private void loadCategories() {
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        for (Category category : categoryDAO.getCategoriesByProfile(currentProfileId)) {
            categoryNames.add(category.getName());
        }
        categoryComboBox.setItems(categoryNames);
    }

    @FXML
    private void handleAddTransaction() {
        String amountText = amountField.getText().trim();
        String selectedCategoryName = categoryComboBox.getValue();
        String note = descriptionField.getText().trim();

        if (amountText.isEmpty() || selectedCategoryName == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Thông tin chưa đầy đủ",
                    "Vui lòng nhập số tiền và chọn danh mục.");
            return;
        }

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

        String transactionDateString = LocalDate.now().toString();
        if (note.isEmpty()) {
            note = "Giao dịch không mô tả";
        }

        Transaction newTransaction = new Transaction(
                0, currentProfileId, transactionType, categoryId, note, transactionDateString, amount
        );

        boolean success = transactionDAO.addTransaction(newTransaction);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm giao dịch thành công", "Giao dịch đã được thêm vào cơ sở dữ liệu.");
            loadTransactions();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm giao dịch", "Đã xảy ra lỗi khi thêm giao dịch vào cơ sở dữ liệu.");
        }

        amountField.clear();
        descriptionField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    private void handleDeleteTransaction(Transaction transaction) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xác nhận xóa giao dịch");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa giao dịch \"" + transaction.getType() + ": " + transaction.getAmount() + "\"?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = transactionDAO.deleteTransaction(transaction.getId());
            if (success) {
                loadTransactions();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa giao dịch thành công", "Giao dịch đã được xóa thành công.");
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
    private void handleClearDescription() {
        descriptionField.clear();
    }

    @FXML
    private void handleBackspace() {
        String currentText = amountField.getText();
        if (currentText.length() > 0) {
            amountField.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
    private void updateTotalByDate() {
        LocalDate selectedDate = (datePicker.getValue() != null) ? datePicker.getValue() : LocalDate.now();
        String formattedDate = selectedDate.toString();

        double totalIncome = transactionDAO.getTotalIncomeByDate(formattedDate, currentProfileId);
        double totalExpense = transactionDAO.getTotalExpenseByDate(formattedDate, currentProfileId);

        totalIncomeLabel.setText(Math.round(totalIncome) + " VND");
        totalExpenseLabel.setText(Math.round(totalExpense) + " VND");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/vn/edu/ut/expensemanager/view/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) navigateDashboardBtn.getScene().getWindow();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/vn/edu/ut/expensemanager/view/Report.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) navigateReportBtn.getScene().getWindow();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/vn/edu/ut/expensemanager/view/Category.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) navigateCategoryBtn.getScene().getWindow();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/vn/edu/ut/expensemanager/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) navigateReportBtn.getScene().getWindow();
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