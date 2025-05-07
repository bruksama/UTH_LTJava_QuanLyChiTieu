package main.java.com.expensemanager.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.dao.ReportDAO;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.model.Transaction;
import main.java.com.expensemanager.dao.TransactionDAO;
import main.java.com.expensemanager.util.SessionManagerUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private Button profileList;
    @FXML
    private Label profileName1;
    @FXML
    private Label profileName2;
    @FXML
    private ListView<Transaction> transactionListView;
    @FXML
    private ObservableList<Transaction> transactionObservableList;
    @FXML
    private Button profileQuick;
    @FXML
    private VBox chartContainer;
    @FXML
    private MenuItem deleteProfileMenuItem;
    @FXML
    private MenuItem renameProfileMenuItem;
    @FXML
    private MenuItem renameProfile2MenuItem;
    @FXML
    private MenuItem deleteProfile2MenuItem;



    private TransactionDAO transactionDAO;
    private CategoryDAO categoryDAO;
    private ProfileDAO profileDAO;
    private ReportDAO reportDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        navigateCategoryBtn.setOnAction(event -> navigateCategory());
        navigateTransactionBtn.setOnAction(event -> navigateTransaction());
        navigateReportBtn.setOnAction(event -> navigateReport());
        navigateLoginBtn.setOnAction(event -> navigateLogin());

        addTransaction.setOnAction(event -> navigateTransaction());

        profileList.setOnAction(event -> handleProfileList());
        profileQuick.setOnAction(event -> switchProfile());

        deleteProfileMenuItem.setOnAction(event -> handleMenuItemDeleteProfile());
        renameProfileMenuItem.setOnAction(event -> handleMenuItemRenameProfile());

        renameProfile2MenuItem.setOnAction(event -> handleMenuItemRenameProfile2());
        deleteProfile2MenuItem.setOnAction(event -> handleMenuItemDeleteProfile2());

        transactionDAO = new TransactionDAO();
        categoryDAO = new CategoryDAO();
        profileDAO = new ProfileDAO();
        reportDAO = new ReportDAO();

        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        // Lấy tên profile đang sử dụng từ cơ sở dữ liệu
        Profile currentProfile = profileDAO.getProfileById(currentProfileId);

        if (currentProfile != null) {
            // Hiển thị tên profile đang sử dụng trong profileName1
            profileName1.setText("Đang sử dụng: " + currentProfile.getName());
        } else {
            profileName1.setText("Chưa chọn profile");
        }

        // Lấy các profile còn lại và hiển thị profile thứ hai trong profileName2
        loadProfileNames(currentProfileId);

        transactionObservableList = FXCollections.observableArrayList();
        transactionListView.setItems(transactionObservableList);

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
        loadTransactions();

        loadChart();

    }

    @FXML
    private void switchProfile() {
        String currentProfileName = profileName1.getText().replace("Đang sử dụng: ", ""); // Lấy tên profile1
        String profileName = profileName2.getText(); // Lấy tên profile2

        // Đổi profile
        if (!currentProfileName.isEmpty() && !profileName.equals("Chưa có profile thứ hai")) {
            // Cập nhật session cho profile2
            Profile profile = profileDAO.getProfileByUsername(profileName);
            if (profile != null) {
                SessionManagerUtil.getInstance().setCurrentProfileId(profile.getId());
                SessionManagerUtil.getInstance().setCurrentProfileName(profile.getName());

                // Cập nhật giao diện cho profileName1 và profileName2
                profileName1.setText("Đang sử dụng: " + profile.getName());
                profileName2.setText(currentProfileName); // Đổi profileName2 thành profile đang sử dụng
            }
        }
    }

    private void loadProfileNames(int currentProfileId) {
        // Lấy danh sách tất cả profile từ cơ sở dữ liệu
        List<Profile> profiles = profileDAO.getAllProfiles();

        // Hiển thị profile thứ hai nếu có
        if (profiles.size() > 1) {
            for (Profile profile : profiles) {
                // Nếu profile không phải là profile đang được sử dụng, hiển thị vào profileName2
                if (profile.getId() != currentProfileId) {
                    profileName2.setText(profile.getName());
                    break;
                }
            }
        } else {
            profileName2.setText("Chưa có profile thứ hai");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Transaction.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Report.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Category.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) navigateLoginBtn.getScene().getWindow();

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
            // Tải trang Profile.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Profile.fxml"));
            Parent root = loader.load();

            // Lấy cửa sổ hiện tại và thiết lập scene mới
            Stage stage = (Stage) profileList.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể chuyển đến trang profile", "Đã xảy ra lỗi khi chuyển đến trang profile: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
        try {
            // Lấy tất cả giao dịch của profile hiện tại từ database
            transactionObservableList.setAll(transactionDAO.getLatestFiveTransactionsByProfile(currentProfileId));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải giao dịch",
                    "Đã xảy ra lỗi khi tải giao dịch từ cơ sở dữ liệu.");
        }
    }

    private List<Transaction> getTransactionsInRange() {
        LocalDate now = LocalDate.now();
        String from = now.withDayOfMonth(1).toString();
        String to = now.withDayOfMonth(now.lengthOfMonth()).toString();

        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        try {
            return reportDAO.getTransactionsByDateRange(currentProfileId, from, to);
        } catch (Exception e) {
            System.err.println(e.getMessage());;
            return new ArrayList<>();
        }
    }


    private BarChart<String, Number> createBarChart(double totalIncome, double totalExpense) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Loại giao dịch");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Biểu đồ giao dịch");

        // Dữ liệu cho biểu đồ
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Thu");
        incomeSeries.getData().add(new XYChart.Data<>("Thu", totalIncome));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Chi");
        expenseSeries.getData().add(new XYChart.Data<>("Chi", totalExpense));

        // Thêm dữ liệu vào biểu đồ
        barChart.getData().addAll(incomeSeries, expenseSeries);

//        barChart.setPrefSize(500, 400);  // Đặt kích thước cho biểu đồ

        return barChart;
    }

    private void loadChart() {
        List<Transaction> transactions = getTransactionsInRange();

        double totalIncome = transactions.stream()
                .filter(t -> "Thu".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> "Chi".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        totalIncomeLabel.setText(Math.round(totalIncome) + " VND");
        totalExpenseLabel.setText(Math.round(totalExpense) + " VND");

        BarChart<String, Number> barChart = createBarChart(totalIncome, totalExpense);
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(barChart);
    }

    @FXML
    private void handleMenuItemDeleteProfile() {
        // Lấy profile đang sử dụng
        String currentProfileName = profileName1.getText().replace("Đang sử dụng: ", "");
        if (currentProfileName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có profile", "Bạn cần chọn một profile để xóa.");
            return;
        }

        // Kiểm tra xem có phải profile đang sử dụng không
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
        Profile profile = profileDAO.getProfileById(currentProfileId);

        if (profile != null) {
            // Nếu đây là profile đang sử dụng, không thể xóa
            if (profile.getName().equals(currentProfileName)) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa hồ sơ đang sử dụng", "Bạn không thể xóa hồ sơ đang sử dụng.");
                return;
            }

            // Xóa profile
            boolean success = profileDAO.deleteProfile(profile.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa hồ sơ thành công", "Hồ sơ đã được xóa.");
                // Cập nhật lại giao diện (có thể chuyển về màn hình login hoặc chọn profile khác)
                loadProfileNames(currentProfileId); // Tải lại danh sách profile
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa hồ sơ", "Đã xảy ra lỗi khi xóa hồ sơ.");
            }
        }
    }

    @FXML
    private void handleMenuItemRenameProfile() {
        // Lấy tên profile đang sử dụng
        String currentProfileName = profileName1.getText().replace("Đang sử dụng: ", "");

        if (currentProfileName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có profile", "Bạn cần chọn một profile để đổi tên.");
            return;
        }

        // Hiển thị hộp thoại nhập tên mới
        TextInputDialog dialog = new TextInputDialog(currentProfileName);
        dialog.setTitle("Đổi tên hồ sơ");
        dialog.setHeaderText("Nhập tên hồ sơ mới:");
        dialog.setContentText("Tên hồ sơ mới:");

        // Lấy tên mới từ hộp thoại
        dialog.showAndWait().ifPresent(newProfileName -> {
            if (newProfileName != null && !newProfileName.trim().isEmpty()) {
                // Kiểm tra tên mới hợp lệ
                if (profileDAO.isProfileExistByName(newProfileName)) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên hồ sơ đã tồn tại", "Hồ sơ với tên này đã tồn tại.");
                    return;
                }

                // Cập nhật tên hồ sơ trong cơ sở dữ liệu
                int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
                Profile profile = profileDAO.getProfileById(currentProfileId);
                if (profile != null) {
                    profile.setName(newProfileName);

                    boolean success = profileDAO.updateProfile(profile);
                    if (success) {
                        SessionManagerUtil.getInstance().setCurrentProfileName(newProfileName); // Cập nhật tên trong session
                        profileName1.setText("Đang sử dụng: " + newProfileName); // Cập nhật giao diện

                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi tên hồ sơ thành công", "Tên hồ sơ đã được đổi thành: " + newProfileName);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể đổi tên hồ sơ", "Đã xảy ra lỗi khi đổi tên hồ sơ.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên hồ sơ không hợp lệ", "Tên hồ sơ không được để trống.");
            }
        });
    }

    @FXML
    private void handleMenuItemRenameProfile2() {
        // Lấy tên của Profile 2
        String currentProfileName = profileName2.getText();

        if (currentProfileName.equals("Chưa có profile thứ hai")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có profile thứ hai", "Bạn cần tạo một profile thứ hai.");
            return;
        }

        // Hiển thị hộp thoại nhập tên mới
        TextInputDialog dialog = new TextInputDialog(currentProfileName);
        dialog.setTitle("Đổi tên hồ sơ");
        dialog.setHeaderText("Nhập tên hồ sơ mới:");
        dialog.setContentText("Tên hồ sơ mới:");

        dialog.showAndWait().ifPresent(newProfileName -> {
            if (newProfileName != null && !newProfileName.trim().isEmpty()) {
                // Kiểm tra tên mới hợp lệ
                if (profileDAO.isProfileExistByName(newProfileName)) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên hồ sơ đã tồn tại", "Hồ sơ với tên này đã tồn tại.");
                    return;
                }

                // Cập nhật tên hồ sơ trong cơ sở dữ liệu
                int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
                Profile profile = profileDAO.getProfileById(currentProfileId);
                if (profile != null) {
                    profile.setName(newProfileName);

                    boolean success = profileDAO.updateProfile(profile);
                    if (success) {
                        profileName2.setText(newProfileName); // Cập nhật giao diện cho profileName2

                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi tên hồ sơ thành công", "Tên hồ sơ đã được đổi thành: " + newProfileName);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể đổi tên hồ sơ", "Đã xảy ra lỗi khi đổi tên hồ sơ.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên hồ sơ không hợp lệ", "Tên hồ sơ không được để trống.");
            }
        });
    }

    @FXML
    private void handleMenuItemDeleteProfile2() {
        String currentProfileName = profileName2.getText();

        if (currentProfileName.equals("Chưa có profile thứ hai")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không có profile thứ hai", "Bạn cần tạo một profile thứ hai.");
            return;
        }

        // Xác nhận xóa hồ sơ
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa hồ sơ này?");
        alert.setContentText("Hồ sơ sẽ bị xóa vĩnh viễn.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();
                Profile profile = profileDAO.getProfileById(currentProfileId);
                if (profile != null) {
                    boolean success = profileDAO.deleteProfile(profile.getId());
                    if (success) {
                        profileName2.setText("Chưa có profile thứ hai");
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa hồ sơ thành công", "Hồ sơ đã được xóa.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa hồ sơ", "Đã xảy ra lỗi khi xóa hồ sơ.");
                    }
                }
            }
        });
    }


}