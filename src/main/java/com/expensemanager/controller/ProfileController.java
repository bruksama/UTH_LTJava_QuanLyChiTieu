package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Profile;
import main.java.com.expensemanager.service.ProfileService;
import main.java.com.expensemanager.util.SessionManagerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProfileController {
    @FXML
    private ListView<String> profileListView;  // Hiển thị danh sách các profile
    @FXML
    private TextField profileNameField;        // Hiển thị tên profile đã chọn
    @FXML
    private Button selectProfileButton;        // Nút chọn profile
    @FXML
    private Button editProfileButton;          // Nút sửa profile
    @FXML
    private Button deleteProfileButton;        // Nút xóa profile
    @FXML
    private Button deleteAllProfileButton;     // Nút xóa tất cả profile

    private ProfileService profileService;

    @FXML
    public void initialize() {
        CategoryDAO categoryDAO = new CategoryDAO();
        ProfileDAO profileDAO = new ProfileDAO();
        profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO(),new main.java.com.expensemanager.dao.CategoryDAO());

        // Tải danh sách các profile hiện có từ cơ sở dữ liệu
        loadProfiles();

        // Lắng nghe sự kiện chọn profile từ ListView
        profileListView.setOnMouseClicked(event -> handleProfileSelection());

        // Lắng nghe sự kiện nhấn nút "Chọn"
        selectProfileButton.setOnAction(event -> handleSelectProfile());

        // Lắng nghe sự kiện nhấn nút "Sửa"
        editProfileButton.setOnAction(event -> handleEditProfile());

        // Lắng nghe sự kiện nhấn nút "Xóa"
        deleteProfileButton.setOnAction(event -> handleDeleteProfile());

        // Lắng nghe sự kiện nhấn nút "Xóa tất cả"
        deleteAllProfileButton.setOnAction(event -> handleDeleteAllProfiles());
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
        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Hiển thị tên profile vào TextField
            profileNameField.setText(selectedProfile);
        }
    }

    // Xử lý sự kiện khi người dùng nhấn nút "Chọn"
    @FXML
    private void handleSelectProfile() {
        String selectedProfile = profileNameField.getText();

        if (selectedProfile != null && !selectedProfile.isEmpty()) {

            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {

                SessionManagerUtil.getInstance().setCurrentProfileId(profile.getId());
                SessionManagerUtil.getInstance().setCurrentProfileName(profile.getName());


                navigateToDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Profile not found", "The profile you selected is invalid.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No profile selected", "Please select a profile to continue.");
        }
    }
    // Điều hướng đến màn hình Dashboard
    @FXML
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
    // Xử lý sự kiện "Sửa profile"
    @FXML
    private void handleEditProfile() {
        String selectedProfile = profileNameField.getText();

        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Lấy thông tin profile từ cơ sở dữ liệu
            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Mở cửa sổ để nhập tên mới cho profile
                TextInputDialog dialog = new TextInputDialog(profile.getName());
                dialog.setTitle("Đổi tên profile");
                dialog.setHeaderText("Nhập tên mới cho profile");
                dialog.setContentText("Tên mới:");

                dialog.showAndWait().ifPresent(newName -> {
                    // Cập nhật tên profile trong cơ sở dữ liệu
                    profile.setName(newName);
                    if (profileService.updateProfile(profile)) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi tên profile thành công", "Tên profile đã được cập nhật.");
                        loadProfiles();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật tên", "Đã xảy ra lỗi khi cập nhật tên profile.");
                    }
                });
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", "Không tìm thấy profile để sửa.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn profile", "Vui lòng chọn một profile để sửa.");
        }
    }

    // Xử lý sự kiện "Xóa profile"
    @FXML
    private void handleDeleteProfile() {
        String selectedProfile = profileNameField.getText();

        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Lấy thông tin profile từ cơ sở dữ liệu
            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Xác nhận xóa profile
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Xác nhận xóa");
                confirmDialog.setHeaderText("Xác nhận xóa profile");
                confirmDialog.setContentText("Bạn có chắc chắn muốn xóa profile này không?");

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (profileService.deleteProfile(profile.getId())) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa profile thành công", "Profile đã được xóa.");
                        loadProfiles();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa profile", "Đã xảy ra lỗi khi xóa profile.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", "Không tìm thấy profile để xóa.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Chưa chọn profile", "Vui lòng chọn một profile để xóa.");
        }
    }

    // Xử lý sự kiện "Xóa tất cả profile"
    @FXML
    private void handleDeleteAllProfiles() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa tất cả");
        confirmDialog.setHeaderText("Xác nhận xóa tất cả profile");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa tất cả profile không?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Xóa tất cả profile trong cơ sở dữ liệu
            if (profileService.deleteAllProfiles()) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa tất cả profile thành công", "Tất cả profile đã được xóa.");
                loadProfiles();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa tất cả profile", "Đã xảy ra lỗi khi xóa tất cả profile.");
            }
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
