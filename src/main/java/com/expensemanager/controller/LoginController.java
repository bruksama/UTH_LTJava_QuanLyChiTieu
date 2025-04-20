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
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.service.ProfileService;
import javafx.scene.control.ListCell;

import java.util.List;

public class LoginController {
    @FXML
    private ComboBox<String> profileComboBox;  // ComboBox để chọn profile có sẵn
    @FXML
    private TextField newProfileTextField;  // TextField để tạo hồ sơ mới
    @FXML
    private Button okButton;  // Nút OK để tiếp tục

    @FXML
    private Button exitButton;

    private ProfileService profileService;  // Dịch vụ quản lý profile

    // Khởi tạo ProfileService
    public LoginController() {
        this.profileService = new ProfileService(new main.java.com.expensemanager.dao.ProfileDAO());
    }

    @FXML
    public void initialize() {
        handleProfileSelection();
        setComboBoxCellFactory(); // Gọi phương thức để tải các profile vào ComboBox khi trang load
    }

    private void setComboBoxCellFactory() {
        profileComboBox.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        // Tạo nút "X" cho mỗi item
                        Button deleteButton = new Button("X");
                        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> deleteProfileByName(item));  // Gọi hàm xóa khi nhấn X
                        setGraphic(deleteButton);
                        setText(item);  // Hiển thị tên profile
                    }
                }
            };
            return cell;
        });
    }


    // Xóa profile theo tên
    private void deleteProfileByName(String profileName) {
        // In ra thông tin về profile sẽ bị xóa
        System.out.println("Đang cố gắng xóa profile với tên: " + profileName);

        // Gọi phương thức trong ProfileService để xóa profile theo tên
        boolean result = profileService.deleteProfileByName(profileName);

        // In ra thông tin kết quả của việc xóa
        if (result) {
            System.out.println("Hồ sơ '" + profileName + "' đã được xóa thành công.");
            showAlert("Thành công", "Hồ sơ đã được xóa.");
            handleProfileSelection();  // Cập nhật lại danh sách profile trong ComboBox
        } else {
            System.out.println("Không thể xóa hồ sơ: " + profileName);
            showAlert("Lỗi", "Không thể xóa hồ sơ.");
        }
    }



    // Xử lý sự kiện khi người dùng chọn profile từ ComboBox
    @FXML
    private void handleProfileSelection() {
        // Lấy tất cả các profile từ cơ sở dữ liệu và điền vào ComboBox
        List<String> profileList = profileService.getAllProfileNames();

        // Kiểm tra danh sách profile trước khi thêm vào ComboBox
        if (profileList.isEmpty()) {
            showAlert("Lỗi", "Không có hồ sơ nào trong cơ sở dữ liệu.");
            return;
        }

        // Cập nhật ComboBox với danh sách các profile
        profileComboBox.getItems().clear();  // Xóa tất cả các mục cũ trong ComboBox
        profileComboBox.getItems().addAll(profileList);  // Thêm các profile mới vào ComboBox
        System.out.println("Danh sách profile đã được cập nhật.");
    }





    // Xử lý sự kiện khi người dùng nhập tên hồ sơ mới
    @FXML
    private void handleCreateProfile() {
        String newProfileName = newProfileTextField.getText();  // Lấy tên profile mới từ TextField

        if (newProfileName != null && !newProfileName.isEmpty()) {
            // Kiểm tra xem profile đã tồn tại chưa
            boolean profileExists = profileService.isProfileExistByName(newProfileName);

            if (profileExists) {
                showAlert("Lỗi", "Hồ sơ với tên này đã tồn tại!");  // Hiển thị thông báo nếu profile đã tồn tại
                return;  // Dừng lại nếu hồ sơ đã tồn tại
            }

            // Tạo profile mới nếu chưa tồn tại
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

        // Kiểm tra sự tồn tại của profile trước khi tiếp tục
        if (selectedProfile != null && !selectedProfile.isEmpty()) {
            // Kiểm tra xem profile có tồn tại trong cơ sở dữ liệu không
            Profile profile = profileService.getProfileByUsername(selectedProfile);
            if (profile != null) {
                // Nếu profile tồn tại, chuyển sang Dashboard
                System.out.println("Đã chọn profile: " + selectedProfile);
                try {
                    Application app = new Application();
                    app.showDashboard();  // Chuyển sang Dashboard sau khi đăng nhập
                    Stage stage = (Stage) okButton.getScene().getWindow();
                    stage.close();  // Đóng cửa sổ Login
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Lỗi", "Không thể chuyển sang Dashboard.");
                }
            } else {
                // Nếu không tìm thấy profile với tên này
                showAlert("Lỗi", "Không tìm thấy profile với tên: " + selectedProfile);
            }
        } else if (newProfile != null && !newProfile.isEmpty()) {
            // Kiểm tra sự tồn tại của hồ sơ mới trước khi tạo
            boolean profileExists = profileService.isProfileExistByName(newProfile);

            if (profileExists) {
                // Hiển thị thông báo nếu hồ sơ với tên này đã tồn tại
                showAlert("Lỗi", "Hồ sơ với tên này đã tồn tại!");
                return;
            }

            // Tạo profile mới nếu chưa tồn tại
            Profile newProfileObj = new Profile();
            newProfileObj.setName(newProfile);  // Cập nhật tên profile mới

            boolean success = profileService.createProfile(newProfileObj);  // Lưu hồ sơ mới vào DB

            if (success) {
                showAlert("Thành công", "Hồ sơ người dùng đã được tạo thành công.");
                System.out.println("Đã tạo hồ sơ người dùng mới: " + newProfile);
                // Chuyển sang giao diện Dashboard sau khi tạo profile thành công
                try {
                    Application app = new Application();
                    app.showDashboard();  // Chuyển sang Dashboard sau khi đăng nhập
                    Stage stage = (Stage) okButton.getScene().getWindow();
                    stage.close();  // Đóng cửa sổ Login
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Lỗi", "Không thể chuyển sang Dashboard.");
                }
            } else {
                showAlert("Lỗi", "Không thể tạo hồ sơ người dùng mới.");
            }
        } else {
            showAlert("Lỗi nhập liệu", "Vui lòng chọn hoặc tạo hồ sơ.");
        }
    }

    @FXML
    public void handleExit() {
        // Lấy cửa sổ hiện tại và đóng nó
        Stage stage = (Stage) okButton.getScene().getWindow();  // Lấy cửa sổ hiện tại
        stage.close();  // Đóng cửa sổ
    }

    // Hiển thị thông báo lỗi nếu đăng nhập hoặc tạo hồ sơ không thành công
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);  // Tạo đối tượng Alert
        alert.setTitle(title);  // Đặt tiêu đề cho thông báo
        alert.setHeaderText(null);  // Không có tiêu đề phụ
        alert.setContentText(message);  // Nội dung thông báo
        alert.showAndWait();  // Hiển thị thông báo và chờ người dùng đóng
    }


}
