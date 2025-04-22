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
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.model.Transaction;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.util.SessionManagerUtil;

import java.io.IOException;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardController implements Initializable {

    @FXML
    private ToggleButton navigateTransactionBtn;
    @FXML
    private ToggleButton navigateCategoryBtn;
    @FXML
    private ToggleButton navigateReportBtn;
    @FXML
    private Button addTransaction;
    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;
    @FXML
    private Button navigateLoginBtn;

    @FXML
    private Label profileName1;
    @FXML
    private Label profileName2;
    @FXML
    private MenuButton tuychon1;
    @FXML
    private MenuButton tuychon2;
    @FXML
    private MenuItem updateProfileName;
    @FXML
    private MenuItem deleteProfileName;
    @FXML
    private Button profileList;
    private ProfileDAO profileDAO;
    private TransactionDAO transactionDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        profileDAO = new ProfileDAO();
        navigateCategoryBtn.setOnAction(event -> navigateCategory());
        navigateTransactionBtn.setOnAction(event -> navigateTransaction());
        navigateReportBtn.setOnAction(event -> navigateReport());
        navigateLoginBtn.setOnAction(event -> navigateLogin());
        addTransaction.setOnAction(event -> navigateToTransaction());
        profileList.setOnAction(event -> handleProfileList());
        transactionDAO = new TransactionDAO();

        // Lấy profileId từ SessionManager
        int profileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        List<Transaction> transactions = transactionDAO.getTransactionsByProfile(profileId);

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        // Lấy ngày hiện tại và tháng hiện tại
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        // Duyệt qua các giao dịch và tính tổng thu/chi theo tháng
        for (Transaction transaction : transactions) {
            // Chuyển đổi ngày giao dịch từ String sang LocalDate
            String transactionDateStr = transaction.getDate();
            LocalDate transactionDate = LocalDate.parse(transactionDateStr, DateTimeFormatter.ISO_DATE);

            // Kiểm tra xem giao dịch có trong tháng hiện tại không
            if (transactionDate.getMonthValue() == currentMonth && transactionDate.getYear() == currentYear) {
                if ("Thu".equals(transaction.getType())) {
                    totalIncome += transaction.getAmount();
                } else if ("Chi".equals(transaction.getType())) {
                    totalExpense += transaction.getAmount();
                }
            }
        }
        totalIncomeLabel.setText(String.format("%,.0fđ", totalIncome));
        totalExpenseLabel.setText(String.format("%,.0fđ", totalExpense));
        loadProfileNames();
       updateProfileName.setOnAction(event -> handleRenameProfile());
        deleteProfileName.setOnAction(event -> handleDeleteProfile());
    }

    // Tải và hiển thị các profile vào các Label
    private void loadProfileNames() {
        List<String> profileNames = profileDAO.getAllProfileNames();  // Lấy danh sách các tên profile từ cơ sở dữ liệu

        if (profileNames.size() >= 1) {
            profileName1.setText(profileNames.get(0));  // Hiển thị profile đầu tiên
        } else {
            profileName1.setText("Chưa có profile");  // Nếu không có profile nào
        }

        if (profileNames.size() >= 2) {
            profileName2.setText(profileNames.get(1));  // Hiển thị profile thứ hai
        } else {
            profileName2.setText("Chưa có profile thứ hai");  // Nếu không có profile thứ hai
        }
    }

    // Xử lý sự kiện "Đổi tên" profile
    @FXML
    private void handleRenameProfile() {
        // Lấy tên mới từ người dùng
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Đổi tên profile");
        dialog.setHeaderText("Nhập tên mới cho profile");
        dialog.setContentText("Tên mới:");

        dialog.showAndWait().ifPresent(newName -> {
            String currentProfileName = profileName1.getText();  // Lấy tên profile hiện tại
            Profile profileToUpdate = profileDAO.getProfileByUsername(currentProfileName);

            if (profileToUpdate != null) {
                profileToUpdate.setName(newName);
                if (profileDAO.updateProfile(profileToUpdate)) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi tên thành công", "Tên profile đã được cập nhật.");
                    loadProfileNames();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật tên", "Đã xảy ra lỗi khi cập nhật tên profile.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", "Không tìm thấy profile để cập nhật.");
            }
        });
    }

    // Xử lý sự kiện "Xóa" profile
    @FXML
    private void handleDeleteProfile() {
        // Xác nhận xóa
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Bạn có chắc chắn muốn xóa profile?");
        confirmDialog.setContentText("Profile sẽ bị xóa vĩnh viễn.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String currentProfileName = profileName1.getText();  // Lấy tên profile hiện tại
            Profile profileToDelete = profileDAO.getProfileByUsername(currentProfileName);

            if (profileToDelete != null) {
                if (profileDAO.deleteProfile(profileToDelete.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa profile thành công", "Profile đã được xóa.");
                    loadProfileNames();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa profile", "Đã xảy ra lỗi khi xóa profile.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", "Không tìm thấy profile để xóa.");
            }
        }
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

    private void navigateLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Login.fxml"));
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
    private void handleProfileList() {
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