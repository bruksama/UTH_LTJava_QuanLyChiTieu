package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.service.ProfileService;
import main.java.com.expensemanager.util.SessionManagerUtil;

import java.io.IOException;
import java.util.List;

public class ProfileController {

    @FXML
    private ListView<String> profileListView;  // Hiển thị danh sách các profile
    @FXML
    private TextField profileNameField;        // Hiển thị tên profile đã chọn
    @FXML
    private Button selectProfileButton;         // Nút chọn profile

    private ProfileService profileService;

    @FXML
    public void initialize() {
        profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO());

        // Tải danh sách các profile hiện có từ cơ sở dữ liệu
        loadProfiles();

        // Lắng nghe sự kiện chọn profile từ ListView
        profileListView.setOnMouseClicked(event -> handleProfileSelection());

        // Lắng nghe sự kiện nhấn nút "Chọn"
        selectProfileButton.setOnAction(event -> handleSelectProfile());
    }

    // Tải danh sách các profile vào ListView
    private void loadProfiles() {
        // Lấy tất cả các profile từ cơ sở dữ liệu
        List<String> profiles = profileService.getAllProfileNames();

        // Cập nhật danh sách profile vào ListView
        profileListView.getItems().clear();
        profileListView.getItems().addAll(profiles);
    }

    // Xử lý khi người dùng chọn một profile từ ListView
    private void handleProfileSelection() {
        String selectedProfile = profileListView.getSelectionModel().getSelectedItem();
        if (selectedProfile != null) {
            // Hiển thị tên profile vào TextField
            profileNameField.setText(selectedProfile);
        }
    }

    // Xử lý sự kiện khi người dùng nhấn nút "Chọn"
    private void handleSelectProfile() {
        String selectedProfile = profileNameField.getText();

        // Kiểm tra nếu tên profile không rỗng
        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Lấy thông tin profile từ cơ sở dữ liệu
            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Lưu profileId vào SessionManager
                SessionManagerUtil.getInstance().setCurrentProfileId(profile.getId());

                // Chuyển sang màn hình Dashboard
                navigateToDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", "Profile bạn chọn không hợp lệ.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn profile", "Vui lòng chọn một profile để tiếp tục.");
        }
    }

    // Điều hướng đến màn hình Dashboard
    private void navigateToDashboard() {
        try {
            // Tải màn hình Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Dashboard.fxml"));
            Parent root = loader.load();

            // Lấy cửa sổ hiện tại và thiết lập scene mới
            Stage stage = (Stage) selectProfileButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể chuyển đến màn hình Dashboard", "Đã xảy ra lỗi khi chuyển đến Dashboard: " + e.getMessage());
        }
    }

    // Hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
