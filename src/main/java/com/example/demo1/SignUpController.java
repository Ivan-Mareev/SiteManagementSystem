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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authSignUpButton;

    @FXML
    private Button btnBack;

    @FXML
    private TextField loginField;

    @FXML
    private TextField nameField1;

    @FXML
    private TextField otchestvoField;

    @FXML
    private PasswordField passField;

    @FXML
    private TextField surnameField2;

    @FXML
    void initialize() {
//        assert authSignUpButton != null : "fx:id=\"authSignUpButton\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert nameField1 != null : "fx:id=\"nameField1\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert otchestvoField != null : "fx:id=\"otchestvoField\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert passField != null : "fx:id=\"passField\" was not injected: check your FXML file 'Sign-Up.fxml'.";
//        assert surnameField2 != null : "fx:id=\"surnameField2\" was not injected: check your FXML file 'Sign-Up.fxml'.";

        authSignUpButton.setOnAction(actionEvent -> {
            signUpNewUser();
        });

        btnBack.setOnAction(actionEvent -> {
            btnBack.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo1/hello-view.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });

    }

    private void signUpNewUser() {
//        DataBaseHandler dataBaseHandler = new DataBaseHandler();

        String firstName = nameField1.getText();
        String lastName = surnameField2.getText();
        String patronymic = otchestvoField.getText();
        String login = loginField.getText();
        String password = passField.getText();

        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            if (!firstName.isBlank() && !lastName.isBlank() && !patronymic.isBlank() && !login.isBlank() && !loginField.getText().isBlank() && !password.isBlank()) {
                if (!registeredUserVerification(loginField.getText())) { /////////
                    User user = new User(firstName, lastName, patronymic, login, password);
                    DataBaseHandler.signUpUser(connection, user);

                    authSignUpButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/demo1/Main-window.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Ошибка!");

                    Label label = new Label("Пользователь с таким логином уже существует!");

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

    public boolean registeredUserVerification(String logintext) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler();
            User user = new User();
            user.setLogin(logintext);

            ResultSet resultSet = dataBaseHandler.getUserLogin(connection, user);

            // Проверяем, есть ли хотя бы одна запись с таким логином
            return resultSet != null && resultSet.next();
        }
    }
}
