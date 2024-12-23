package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminAddPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnBack;

    @FXML
    private Button addBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField positionField;

    @FXML
    void initialize() {
//        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'adminAdd-panel.fxml'.";
//        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'adminAdd-panel.fxml'.";
//        assert positionField != null : "fx:id=\"positionField\" was not injected: check your FXML file 'adminAdd-panel.fxml'.";
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
        addBtn.setOnAction(actionEvent -> {
            addUserOnPosition();

        });
    }

    private void addUserOnPosition() {
//        DataBaseHandler dataBaseHandler = new DataBaseHandler();

        String login = loginField.getText();
        String position = positionField.getText();

        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            if (!login.isBlank() && !position.isBlank()) {
                if (checkUserInUsers(loginField.getText())) {
                    if (!checkWorker(login)) {           ///////////
                        DataBaseHandler.addOnPosition(connection, login, position);
                        Stage newWindow = new Stage();
                        newWindow.setTitle("Успех!");

                        Label label = new Label("Пользователю выдана должность!");

                        Scene scene = new Scene(label, 400, 200);
                        newWindow.setScene(scene);

                        newWindow.show();
                    } else {
                        Stage newWindow = new Stage();
                        newWindow.setTitle("Ошибка!");

                        Label label = new Label("Пользователь уже стоит на должности!");

                        Scene scene = new Scene(label, 400, 200);
                        newWindow.setScene(scene);

                        newWindow.show();
                    }
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Ошибка!");

                    Label label = new Label("Такого пользователя не существует!");

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
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")){
                System.out.println("Произошло дублирование данных");
            } else throw new RuntimeException(e);
        }
    }

    public boolean checkUserInUsers(String logintext) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler();
            User user = new User();
            user.setLogin(logintext);

            ResultSet resultSet = dataBaseHandler.getUserLogin(connection, user);

            // Проверяем, есть ли хотя бы одна запись с таким логином
            return resultSet != null && resultSet.next();
        }
    }

    public boolean checkWorker(String logintext) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            ResultSet res = DataBaseHandler.getWorkerLogin(connection, logintext);
            return res != null && res.next();
        }
    }

}
