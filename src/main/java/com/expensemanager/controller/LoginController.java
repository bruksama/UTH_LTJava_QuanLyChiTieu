package main.java.com.expensemanager.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.com.expensemanager.Application;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.service.ProfileService;
public class LoginController {
    @FXML
    private ComboBox<String> profileComboBox;  // ComboBox để chọn profile có sẵn
    @FXML
    private TextField newProfileTextField;  // TextField để tạo hồ sơ mới
    @FXML
    private Button okButton;  // Nút OK để tiếp tục

    private ProfileService profileService;  // Dịch vụ quản lý profile

    // Khởi tạo ProfileService
    public LoginController() {
        this.profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO());
    }

    // Xử lý sự kiện khi người dùng chọn profile từ ComboBox
    @FXML
    private void handleProfileSelection() {
        String selectedProfile = profileComboBox.getValue();  // Lấy giá trị profile được chọn
        System.out.println("Đã chọn profile: " + selectedProfile);  // In ra giá trị đã chọn
    }

    // Xử lý sự kiện khi người dùng nhập tên hồ sơ mới
    @FXML
    private void handleCreateProfile() {
        String newProfileName = newProfileTextField.getText();  // Lấy tên profile mới từ TextField

        if (newProfileName != null && !newProfileName.isEmpty()) {
            // Tạo profile mới
            Profile newProfile = new Profile();
            newProfile.setName(newProfileName);  // Cập nhật tên profile mới

            boolean success = profileService.createProfile(newProfile);  // Lưu profile mới vào DB

            if (success) {
                showAlert("Thành công", "Hồ sơ người dùng đã được tạo thành công.");
                System.out.println("Đã tạo hồ sơ người dùng mới: " + newProfileName);
                // Chuyển sang giao diện Dashboard sau khi tạo profile thành công
                Application app = new Application();
                app.showDashboard();
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();  // Đóng cửa sổ Login
            } else {
                showAlert("Lỗi", "Không thể tạo hồ sơ người dùng mới.");
            }
        } else {
            showAlert("Lỗi nhập liệu", "Vui lòng nhập tên hồ sơ.");
        }
    }

    // Xử lý sự kiện khi người dùng nhấn nút OK

    // Xử lý sự kiện khi người dùng nhấn nút OK
    @FXML
    public void handleOk() {
        String selectedProfile = profileComboBox.getValue();  // Lấy giá trị profile được chọn
        String newProfile = newProfileTextField.getText();     // Lấy tên hồ sơ mới

        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Người dùng chọn một profile có sẵn
            System.out.println("Đã chọn profile: " + selectedProfile);  // In ra profile đã chọn

            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Nếu tìm thấy profile, chuyển sang Dashboard
                Application app = new Application();
                app.showDashboard();  // Chuyển sang Dashboard sau khi đăng nhập
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();  // Đóng cửa sổ Login
            }
        } else if (newProfile != null && !newProfile.isEmpty()) {
            // Người dùng tạo profile mới
            System.out.println("Đang tạo hồ sơ mới: " + newProfile);  // In ra hồ sơ mới

            Profile newProfileObj = new Profile();
            newProfileObj.setName(newProfile);  // Cập nhật tên hồ sơ mới

            boolean success = profileService.createProfile(newProfileObj);  // Lưu hồ sơ mới vào DB

            if (success) {
                showAlert("Thành công", "Hồ sơ người dùng đã được tạo thành công.");
                System.out.println("Đã tạo hồ sơ người dùng mới: " + newProfile);
                // Chuyển sang giao diện Dashboard sau khi tạo profile thành công
                Application app = new Application();
                app.showDashboard();
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();  // Đóng cửa sổ Login
            } else {
                showAlert("Lỗi", "Không thể tạo hồ sơ người dùng mới.");
            }
        } else {
            showAlert("Lỗi nhập liệu", "Vui lòng chọn hoặc tạo hồ sơ.");
        }
    }

    // Hiển thị thông báo lỗi nếu đăng nhập hoặc tạo hồ sơ không thành công
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
