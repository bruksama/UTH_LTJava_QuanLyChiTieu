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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/Login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
//        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    public void showDashboard() {
        try {
            // Mở giao diện Dashboard sau khi đăng nhập thành công
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/DashBroad.fxml")));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.DECORATED); // Kiểu cửa sổ có viền
            stage.show(); // Hiển thị cửa sổ Dashboard
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}