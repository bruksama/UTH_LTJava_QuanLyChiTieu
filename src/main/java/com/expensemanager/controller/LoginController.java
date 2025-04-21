package main.java.com.expensemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import main.java.com.expensemanager.Application;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.service.ProfileService;
import main.java.com.expensemanager.util.SessionManager;

import java.util.List;

public class LoginController {

    @FXML
    private ComboBox<String> profileComboBox;  // ComboBox để chọn profile có sẵn
    @FXML
    private TextField newProfileTextField;  // TextField để tạo hồ sơ mới
    @FXML
    private Button okButton;  // Nút OK để tiếp tục
    @FXML
    private Button exitButton;  // Nút thoát

    private ProfileService profileService;  // Dịch vụ quản lý profile

    // Khởi tạo ProfileService
    public LoginController() {
        this.profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO());
    }

    @FXML
    public void initialize() {
        handleProfileSelection();
    }

    // Xử lý sự kiện khi người dùng chọn profile từ ComboBox
    @FXML
    private void handleProfileSelection() {
        // Lấy tất cả các profile từ cơ sở dữ liệu và điền vào ComboBox
        List<String> profileList = profileService.getAllProfileNames();

        // Cập nhật ComboBox với danh sách các profile
        profileComboBox.getItems().clear();  // Xóa tất cả các mục cũ trong ComboBox
        profileComboBox.getItems().addAll(profileList);  // Thêm các profile mới vào ComboBox
    }

    // Xử lý sự kiện khi người dùng nhấn nút OK
    @FXML
    public void handleOk() {
        String selectedProfile = profileComboBox.getValue();  // Lấy giá trị profile được chọn
        String newProfile = newProfileTextField.getText();     // Lấy tên hồ sơ mới

        // Kiểm tra sự tồn tại của profile trước khi tiếp tục
        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Kiểm tra xem profile có tồn tại trong cơ sở dữ liệu không
            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Lưu profileId vào SessionManager
                SessionManager.getInstance().setCurrentProfileId(profile.getId());

                // Điều hướng sang Dashboard
                navigateDashboard();
            } else {
                // Nếu không tìm thấy profile với tên này
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng nhập thất bại", "Tên đăng nhập không đúng.");
            }
        } else if (newProfile != null && !newProfile.isEmpty()) {
            // Tạo profile mới nếu chưa tồn tại
            boolean profileExists = profileService.isProfileExistByName(newProfile);

            if (profileExists) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng ký thất bại", "Hồ sơ với tên này đã tồn tại.");
                return;
            }

            Profile newProfileObj = new Profile();
            newProfileObj.setName(newProfile);

            boolean success = profileService.createProfile(newProfileObj);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Hồ sơ đã được tạo", "Hồ sơ người dùng đã được tạo thành công.");

                // Điều hướng sang Dashboard
                navigateDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng ký thất bại", "Không thể tạo hồ sơ người dùng.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin không hợp lệ", "Vui lòng chọn hoặc tạo hồ sơ.");
        }
    }

    // Method tạo hồ sơ mới
    @FXML
    private void handleCreateProfile() {
        // Lấy tên profile mới từ TextField
        String newProfileName = newProfileTextField.getText();

        // Kiểm tra nếu tên profile không trống
        if (newProfileName != null && !newProfileName.isEmpty()) {
            // Kiểm tra xem profile đã tồn tại trong cơ sở dữ liệu chưa
            boolean profileExists = profileService.isProfileExistByName(newProfileName);

            if (profileExists) {
                // Nếu profile đã tồn tại, hiển thị thông báo lỗi
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Hồ sơ với tên này đã tồn tại!", "Vui lòng chọn tên khác.");
                return;  // Dừng lại nếu hồ sơ đã tồn tại
            }

            // Tạo profile mới nếu chưa tồn tại
            Profile newProfile = new Profile();
            newProfile.setName(newProfileName);  // Cập nhật tên profile mới

            // Lưu hồ sơ mới vào cơ sở dữ liệu
            boolean success = profileService.createProfile(newProfile);

            if (success) {
                // Nếu thành công, hiển thị thông báo và chuyển sang màn hình Dashboard
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Hồ sơ người dùng đã được tạo thành công.", "Đã tạo hồ sơ người dùng mới: " + newProfileName);

                // Lưu profileId vào SessionManager
                SessionManager.getInstance().setCurrentProfileId(newProfile.getId());

                // Điều hướng sang Dashboard
                navigateDashboard();
            } else {
                // Nếu không thành công, hiển thị thông báo lỗi
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo hồ sơ người dùng mới.", "Vui lòng thử lại sau.");
            }
        } else {
            // Nếu tên profile trống, hiển thị thông báo lỗi
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Vui lòng nhập tên hồ sơ.", "Tên hồ sơ không được để trống.");
        }
    }


    // Phương thức để điều hướng sang màn hình Dashboard
    private void navigateDashboard() {
        try {
            // Tải màn hình Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/DashBroad.fxml"));
            Parent root = loader.load();

            // Lấy cửa sổ hiện tại và thiết lập scene mới
            Stage stage = (Stage) okButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải màn hình chính", "Đã xảy ra lỗi khi chuyển đến màn hình chính: " + e.getMessage());
        }
    }

    // Phương thức để hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleExit() {
        // Lấy cửa sổ hiện tại và đóng nó
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }


}
