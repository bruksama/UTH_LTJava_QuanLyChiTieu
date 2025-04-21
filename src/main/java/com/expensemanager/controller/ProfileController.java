package main.java.com.expensemanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Profile;

import java.util.Collections;
import java.util.List;

public class ProfileController {

    @FXML
    private ListView<String> profileListView;  // Hiển thị danh sách các profile
    @FXML
    private TextField newProfileTextField;     // TextField nhập tên profile mới
    @FXML
    private Button createProfileButton;        // Nút tạo profile mới

    private ProfileDAO profileDAO;

    @FXML
    public void initialize() {
        profileDAO = new ProfileDAO();

        // Tải danh sách các profile hiện có
        loadProfiles();

        // Thiết lập sự kiện cho nút tạo profile mới
        createProfileButton.setOnAction(event -> createProfile());
    }

    // Tải danh sách các profile
    private void loadProfiles() {
        // Lấy tất cả tên profile và hiển thị trong ListView
        List<String> profiles = profileDAO.getAllProfileNames();
        profileListView.getItems().clear();
        profileListView.getItems().addAll(profiles);
    }

    // Tạo profile mới
    private void createProfile() {
        String newProfileName = newProfileTextField.getText().trim();

        // Kiểm tra tên profile không trống
        if (newProfileName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên profile không hợp lệ", "Vui lòng nhập tên profile.");
            return;
        }

        // Kiểm tra xem profile đã tồn tại chưa
        if (profileDAO.isProfileExistByName(newProfileName)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile đã tồn tại", "Tên profile này đã tồn tại.");
            return;
        }

        Profile newProfile = new Profile();
        newProfile.setName(newProfileName);

        // Thêm profile vào cơ sở dữ liệu
        if (profileDAO.insertProfile(newProfile)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Profile đã được tạo", "Profile mới đã được tạo thành công.");
            loadProfiles();  // Tải lại danh sách profile
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo profile", "Đã xảy ra lỗi khi tạo profile.");
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
