package main.java.com.expensemanager.controller;

import com.gluonhq.charm.glisten.control.DropdownButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    @FXML
    private TextField catNameField;
    @FXML
    private ComboBox<String> catTypeMenu;
    @FXML
    private Button catUpdateBtn;
    @FXML
    private Button catDeleteBtn;
    @FXML
    private ListView<Category> catList;
    @FXML
    private TextField catId;
    @FXML
    private HBox catParent;
    @FXML
    private ToggleButton navigateDashboardBtn;

    private CategoryDAO categoryDAO;
    private ProfileDAO profileDAO;
    private ObservableList<Category> categoryObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryDAO = new CategoryDAO();
        profileDAO = new ProfileDAO();

        SessionManager.getInstance().setCurrentProfileId(4);

        categoryObservableList = FXCollections.observableArrayList();
        catList.setItems(categoryObservableList);

        catTypeMenu.getItems().clear();
        catTypeMenu.getItems().addAll("Thu", "Chi");

        catList.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    String typeLabel = item.getType().equals(Category.TYPE_INCOME) ? "Thu" : "Chi";
                    setText(item.getName() + " (" + typeLabel + ")");
                }
            }
        });

        // Thêm sự kiện khi chọn một danh mục từ ListView
        catList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                catNameField.setText(newValue.getName());
                catId.setText(String.valueOf(newValue.getId()));
                
                // Đặt loại danh mục trong dropdown
                String typeText = newValue.getType().equals(Category.TYPE_INCOME) ? "Thu" : "Chi";
                catTypeMenu.getSelectionModel().select(typeText);
            }
        });
        
        // Thêm sự kiện khi click vào khu vực trống trên form
        catParent.setOnMouseClicked(this::handleMouseClick);
        
        // Gắn sự kiện cho nút Update
        catUpdateBtn.setOnAction(this::handleUpdateButton);
        
        // Gắn sự kiện cho nút Delete
        catDeleteBtn.setOnAction(this::handleDeleteButton);

        navigateDashboardBtn.setOnAction(event -> navigateDashboard());

        loadCategories();
    }
    
    private void loadCategories() {

        int currentProfileId = SessionManager.getInstance().getCurrentProfileId();

        if (profileDAO.isProfileExist(currentProfileId)) {
            List<Category> categories = categoryDAO.getCategoriesByProfile(currentProfileId);
            categoryObservableList.clear();
            categoryObservableList.addAll(categories);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", 
                    "Không thể tải danh mục vì profile không tồn tại.");
        }
    }
    
    private void handleMouseClick(MouseEvent event) {
        // Kiểm tra xem click có xảy ra bên ngoài ListView hay không
        Node source = (Node) event.getTarget();

        // Kiểm tra xem click có nằm trong ListView không
        while (source != null && !(source instanceof ListView)) {
            source = source.getParent();
        }

        if (source == null || catList.getSelectionModel().getSelectedItem() == null) {
            clearFields();
        }
    }
    
    private void clearFields() {
        catNameField.clear();
        catId.clear();
        catList.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleUpdateButton(ActionEvent event) {
        // Lấy ID của profile hiện tại
        int currentProfileId = SessionManager.getInstance().getCurrentProfileId();
        
        // Kiểm tra profile có tồn tại không
        if (!profileDAO.isProfileExist(currentProfileId)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Profile không tồn tại", 
                    "Không thể thực hiện thao tác vì profile không tồn tại.");
            return;
        }
        
        // Lấy giá trị từ các trường
        String categoryName = catNameField.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Tên danh mục trống", 
                    "Vui lòng nhập tên danh mục.");
            return;
        }
        
        // Lấy giá trị loại từ dropdown
        String categoryType = catTypeMenu.getValue().equals("Thu") ?
                Category.TYPE_INCOME : Category.TYPE_EXPENSE;

        String categoryIdText = catId.getText();
        
        if (categoryIdText != null && !categoryIdText.isEmpty()) {
            try {
                int categoryId = Integer.parseInt(categoryIdText);
                Category existingCategory = categoryDAO.getCategoryById(categoryId);
                
                if (existingCategory != null && existingCategory.getProfileId() == currentProfileId) {
                    existingCategory.setName(categoryName);
                    existingCategory.setType(categoryType);
                    
                    if (categoryDAO.updateCategory(categoryName, categoryType, categoryId, currentProfileId)) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                                "Cập nhật danh mục thành công", 
                                "Danh mục đã được cập nhật thành công.");
                        
                        loadCategories();
                        clearFields();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật danh mục", 
                                "Đã xảy ra lỗi khi cập nhật danh mục.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Danh mục không tồn tại", 
                            "Danh mục không tồn tại hoặc không thuộc profile hiện tại.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "ID danh mục không hợp lệ", 
                        "ID danh mục không phải là một số hợp lệ.");
            }
        } else {
            Category newCategory = new Category(0, categoryName, categoryType, currentProfileId);
            
            if (categoryDAO.insertCategory(newCategory)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                        "Tạo danh mục thành công", 
                        "Danh mục mới đã được tạo thành công.");
                
                loadCategories();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo danh mục", 
                        "Đã xảy ra lỗi khi tạo danh mục mới.");
            }
        }
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Category selectedCategory = catList.getSelectionModel().getSelectedItem();
        
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không có danh mục nào được chọn", 
                    "Vui lòng chọn một danh mục để xóa.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xác nhận xóa danh mục");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa danh mục \"" + 
                selectedCategory.getName() + "\" không?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int currentProfileId = SessionManager.getInstance().getCurrentProfileId();
            
            if (categoryDAO.deleteCategory(selectedCategory.getId(), currentProfileId)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                        "Xóa danh mục thành công", 
                        "Danh mục đã được xóa thành công.");
                
                loadCategories();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa danh mục", 
                        "Đã xảy ra lỗi khi xóa danh mục.");
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

    private void navigateDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Dashboard.fxml"));
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
}