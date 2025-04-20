package main.java.com.expensemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.expensemanager.dao.ConnectorDAO;
import main.java.com.expensemanager.dao.ProfileDAO;
import main.java.com.expensemanager.dao.CategoryDAO;
import main.java.com.expensemanager.service.CategoryService;
import main.java.com.expensemanager.model.Category;
import main.java.com.expensemanager.model.Profile;

import java.io.File;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);  // Khởi động ứng dụng JavaFX
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Khởi tạo đường dẫn và cơ sở dữ liệu
        String appDataPath = System.getenv("LOCALAPPDATA");
        String dbPath = appDataPath + "\\QuanLyChiTieu\\database.db";
        String dbURL = "jdbc:sqlite:" + dbPath;

        // Tạo thư mục nếu chưa tồn tại
        new File(appDataPath + "\\QuanLyChiTieu").mkdirs();

        // Khởi tạo và kết nối cơ sở dữ liệu
        ConnectorDAO dbConnector = ConnectorDAO.getInstance(dbURL);
        dbConnector.initDatabase();

        if (dbConnector.isConnected()) {
            System.out.println("Kết nối thành công!");
        } else {
            System.out.println("Không thể kết nối!");
        }

        // Thực hiện tương tác với Profile
        ProfileDAO dbProfile = new ProfileDAO();
        Profile testProfile = dbProfile.getProfileById(5);
        System.out.println("Đang tương tác bằng profile: " + testProfile.getName());
        if (dbProfile.insertProfile(testProfile)) {
            System.out.println("Success");
        } else {
            System.out.println("Fail");
        }

        // Lấy thông tin danh mục và thử xóa
        CategoryDAO dbCategory = new CategoryDAO();
        CategoryService dbCategoryService = new CategoryService(dbCategory);
        Category testCategory = dbCategory.getCategoryById(3);

        System.out.println("Danh mục: " + testCategory.getName());

        if (dbCategoryService.deleteCategory(testCategory.getId(), testProfile.getId())) {
            System.out.println("Xóa thành công");
        } else {
            System.out.println("Xóa thất bại");
        }

        // Tải và hiển thị giao diện Dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardView.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Expense Manager Dashboard");
        primaryStage.show();  // Hiển thị giao diện lên màn hình
    }
}
