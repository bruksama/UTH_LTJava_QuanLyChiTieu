package main.java.com.expensemanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Mở giao diện Login khi bắt đầu
        Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/Category.fxml")));
        Scene loginScene = new Scene(loginRoot);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // Hàm để chuyển sang màn hình Dashboard sau khi đăng nhập thành công
    public void showDashboard() {
        try {
            // Mở giao diện Dashboard sau khi đăng nhập thành công
            Parent dashboardRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/DashBroad.fxml")));
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage stage = new Stage();  // Mở cửa sổ mới cho Dashboard
            stage.setScene(dashboardScene);
            stage.initStyle(StageStyle.DECORATED);  // Hiển thị cửa sổ có viền
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}