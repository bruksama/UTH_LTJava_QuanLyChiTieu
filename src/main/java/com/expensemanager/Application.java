package main.java.com.expensemanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/Login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Quản lý chi tiêu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}