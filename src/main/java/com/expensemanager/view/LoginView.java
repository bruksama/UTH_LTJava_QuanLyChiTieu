package main.java.com.expensemanager.view;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.java.com.expensemanager.controller.LoginController;
public class LoginView {
    @FXML
    private ComboBox<String> profileComboBox;  // ComboBox để chọn profile có sẵn
    @FXML
    private TextField newProfileTextField;  // TextField để tạo hồ sơ mới
    @FXML
    private Button okButton;  // Nút OK để tiếp tục

    private LoginController loginController;  // Controller xử lý đăng nhập

    // Khởi tạo LoginController
    public LoginView() {
        // Khởi tạo đối tượng LoginController, liên kết với logic xử lý đăng nhập
        this.loginController = new LoginController();
    }

    // Xử lý sự kiện khi người dùng nhấn nút OK
    @FXML
    private void handleOkButtonAction() {
        String selectedProfile = profileComboBox.getValue();  // Lấy giá trị của profile được chọn
        String newProfile = newProfileTextField.getText();     // Lấy tên hồ sơ mới

        // Gọi phương thức handleOk() trong LoginController để xử lý việc đăng nhập hoặc tạo hồ sơ mới
        loginController.handleOk();
    }

    // Hiển thị thông báo lỗi nếu đăng nhập hoặc tạo hồ sơ không thành công
    public void showAlert(String title, String message) {
        // Tạo Alert thông báo lỗi
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Phương thức chuyển đổi đến Dashboard (hoặc các giao diện khác)
    public void showDashboard() {
        // Điều hướng đến Dashboard sau khi đăng nhập hoặc tạo hồ sơ thành công
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();  // Đóng cửa sổ Login
    }
}
