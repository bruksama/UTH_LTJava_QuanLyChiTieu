package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.com.expensemanager.dao.ReportDAO;
import main.java.com.expensemanager.model.Transaction;
import main.java.com.expensemanager.service.ReportService;
import main.java.com.expensemanager.util.SessionManagerUtil;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportController {

    @FXML private ToggleButton navigateDashboardBtn;
    @FXML private ToggleButton navigateTransactionBtn;
    @FXML private ToggleButton navigateCategoryBtn;
    @FXML private Button navigateLoginBtn;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button btnExport;

    @FXML
    private Button btnCreate;

    @FXML
    private Label lblTongThu;

    @FXML
    private Label lblTongChi;


    @FXML
    private VBox chartContainer;


    @FXML
    private ListView<String> listViewChi;

    @FXML
    private ListView<String> listViewThu;



    private final ReportService reportService = new ReportService();
    private List<Transaction> transactionsInRange;

    @FXML
    public void initialize() {
        // Gắn sự kiện cho nút "Tạo báo cáo" và "Xuất báo cáo"
        btnExport.setOnAction(e -> exportCSV());
        btnCreate.setOnAction(e -> showReportWithSummary());  // Gọi phương thức tạo báo cáo

        navigateCategoryBtn.setOnAction(event -> navigateCategory());
        navigateTransactionBtn.setOnAction(event -> navigateTransaction());
        navigateDashboardBtn.setOnAction(event -> navigateDashboard());
        navigateLoginBtn.setOnAction(event -> navigateLogin());
    }


    private final ReportDAO reportDAO = new ReportDAO(null); // null vì bên trong nó gọi ConnectorDAO.getInstance()


    private void showReportWithSummary() {
        // Kiểm tra ngày có hợp lệ không
        if (!isDateValid()) return;

        List<Transaction> transactions = getTransactionsInRange();
        if (transactions.isEmpty()) {
            lblTongThu.setText("Tổng thu 💰: 0.0 VND");
            lblTongChi.setText("Tổng chi 💸: 0.0 VND");

            chartContainer.getChildren().clear();

            Label noDataLabel = new Label("⚠ Không có giao dịch nào trong khoảng thời gian đã chọn.");
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");
            noDataLabel.setWrapText(true);

            // Căn giữa nội dung trong VBox
            chartContainer.setSpacing(20);
            chartContainer.setStyle("-fx-alignment: center;"); // Căn giữa cả ngang và dọc

            chartContainer.getChildren().add(noDataLabel);
            return;
        }




        // Tính tổng thu
        double totalIncome = transactions.stream()
                .filter(t -> "Thu".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Tính tổng chi
        double totalExpense = transactions.stream()
                .filter(t -> "Chi".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        // In ra tổng thu và chi
        lblTongThu.setText("Tổng thu 💰: " + Math.round(totalIncome) + " VND");
        lblTongChi.setText("Tổng chi 💸: " + Math.round(totalExpense) + " VND");

        System.out.println("Tổng thu: " + Math.round(totalIncome));
        System.out.println("Tổng chi: " + Math.round(totalExpense));

        // Hiển thị thông tin tổng thu và tổng chi
        Label summary = new Label("Tổng thu: " + totalIncome + " | Tổng chi: " + totalExpense);
        summary.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Nếu không có dữ liệu thu chi
        if (totalIncome == 0 && totalExpense == 0) {
            summary.setText("Không có dữ liệu thu chi");
        }

        // Tạo biểu đồ so sánh thu chi
        BarChart<String, Number> barChart = createBarChart(totalIncome, totalExpense);

        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(summary);
        chartContainer.getChildren().add(barChart);

        // Hiển thị danh sách giao dịch thu và chi
        listViewChi.getItems().clear();
        listViewThu.getItems().clear();

        for (Transaction gd : transactions) {
            String dong = gd.getDate() + " - " + gd.getDescription() + " - " + gd.getAmount();
            if ("Chi".equalsIgnoreCase(gd.getType())) {
                listViewChi.getItems().add(dong);
            } else if ("Thu".equalsIgnoreCase(gd.getType())) {
                listViewThu.getItems().add(dong);
            }
        }


    }
    //Truyền dữ liệu (dltest)
    private List<Transaction> getTransactionsInRange() {
        String from = fromDatePicker.getValue().toString();
        String to = toDatePicker.getValue().toString();
        int currentProfileId = SessionManagerUtil.getInstance().getCurrentProfileId();

        try {
            return reportDAO.getTransactionsByDateRange(currentProfileId, from, to);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Không thể lấy dữ liệu từ cơ sở dữ liệu.");

            return new ArrayList<>();
        }
    }


    private BarChart<String, Number> createBarChart(double totalIncome, double totalExpense) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Loại giao dịch");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("So sánh Thu và Chi");

        // Dữ liệu cho biểu đồ
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Thu");
        incomeSeries.getData().add(new XYChart.Data<>("Thu", totalIncome));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Chi");
        expenseSeries.getData().add(new XYChart.Data<>("Chi", totalExpense));

        // Thêm dữ liệu vào biểu đồ
        barChart.getData().addAll(incomeSeries, expenseSeries);

        barChart.setPrefSize(500, 400);  // Đặt kích thước cho biểu đồ


        System.out.println("Số lượng dữ liệu trong Thu: " + incomeSeries.getData().size());
        System.out.println("Số lượng dữ liệu trong Chi: " + expenseSeries.getData().size());
        return barChart;
    }

    private void exportCSV() {
        if (!isDateValid()) return;

        List<Transaction> transactions = getTransactionsInRange();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo CSV");
        fileChooser.setInitialFileName("BaoCao.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Loại,Ghi chú,Số tiền,Ngày\n");
                for (Transaction t : transactions) {
                    writer.write(String.format("%s,%s,%.2f,%s\n",
                            t.getType(), t.getDescription(), t.getAmount(), t.getDate()));

                }
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xuất báo cáo thành công", "Đường dẫn: " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể ghi file", "Lỗi chi tiết: " + e.getMessage());

            }
        }
    }

    private boolean isDateValid() {
        // Kiểm tra nếu không chọn đầy đủ ngày bắt đầu và kết thúc
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            // Chỉ hiển thị thông báo nếu thiếu cả 2 ngày
            if (fromDatePicker.getValue() == null && toDatePicker.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi nhập dữ liệu", "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc.");

            }
            return false;
        }
        System.out.println("Ngày bắt đầu: " + fromDatePicker.getValue());
        System.out.println("Ngày kết thúc: " + toDatePicker.getValue());
        return true;
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

    private void navigateDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/com/expensemanager/view/Dashboard.fxml"));
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

    public void setTransactionsInRange(List<Transaction> transactionsInRange) {
        this.transactionsInRange = transactionsInRange;
    }
}