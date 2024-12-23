package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminDeletePanel {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button btnBack;

    @FXML
    private URL location;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField loginField;

    @FXML
    void initialize() {
//        assert deleteBtn != null : "fx:id=\"deleteBtn\" was not injected: check your FXML file 'adminDelete-panel.fxml'.";
//        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'adminDelete-panel.fxml'.";
        deleteBtn.setOnAction(actionEvent -> {
            removeFromPosition();

        });
        btnBack.setOnAction(actionEvent -> {
            btnBack.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo1/admin-panel.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    private void removeFromPosition() {
//        DataBaseHandler dataBaseHandler = new DataBaseHandler();

        String login = loginField.getText();

        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            if (!login.isBlank()) {
                if (checkWorker(login)) {
                    DataBaseHandler.removeFromPosition(connection, login);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Сотрудник снят с должности!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Ошибка!");

                    Label label = new Label("Сотрудник не найден!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                }
            } else {
                Stage newWindow = new Stage();
                newWindow.setTitle("Ошибка!");

                Label label = new Label("Заполните недостающие поля!");

                Scene scene = new Scene(label, 400, 200);
                newWindow.setScene(scene);

                newWindow.show();
            }
            } catch (SQLException ex) {
            throw new RuntimeException(ex);

        }
    }

    public boolean checkWorker(String logintext) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            ResultSet res = DataBaseHandler.getWorkerLogin(connection, logintext);
            return res != null && res.next();
        }
    }
}
