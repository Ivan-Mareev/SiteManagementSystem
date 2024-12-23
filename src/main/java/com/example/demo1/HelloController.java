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

public class HelloController {

    @FXML
    private URL location;

    @FXML
    private Button authSignInButton;

    @FXML
    private TextField loginField;

    @FXML
    private Button loginSignUpButton;

    @FXML
    private PasswordField passField;

    @FXML
    void initialize() {
//        assert authSignInButton != null : "fx:id=\"authSignInButton\" was not injected: check your FXML file 'hello-view.fxml'.";
//        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'hello-view.fxml'.";
//        assert loginSignUpButton != null : "fx:id=\"loginSignUpButton\" was not injected: check your FXML file 'hello-view.fxml'.";
//        assert passField != null : "fx:id=\"passField\" was not injected: check your FXML file 'hello-view.fxml'.";

        authSignInButton.setOnAction(actionEvent -> {
            String logintext = loginField.getText().trim();
            String password = passField.getText().trim();
            if (!logintext.isEmpty() && !password.isEmpty()) {
                try {
                    loginUser(logintext, password);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
//                System.out.println("логин и пароль пустые!");
                Stage newWindow = new Stage();
                newWindow.setTitle("Ошибка!");

                Label label = new Label("Заполните недостающие поля!");

                Scene scene = new Scene(label, 400, 200);
                newWindow.setScene(scene);

                newWindow.show();
            }
        });

        loginSignUpButton.setOnAction(actionEvent -> {
            loginSignUpButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo1/Sign-Up.fxml"));
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

    private void loginUser(String logintext, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler();
            User user = new User();
            user.setLogin(logintext);
            user.setPassword(password);
            ResultSet resultSet = dataBaseHandler.getUser(connection, user);
            int counter = 0;
            while (resultSet.next()) {
                counter++;
            }
            if (counter >= 1) {

                if (!loginField.getText().equals("admin") && !loginField.getText().equals("glavred") && !loginField.getText().equals("redotd") && !loginField.getText().equals("author") && !DataBaseHandler.isUserInPositionTable(connection, loginField.getText())) {
                    authSignInButton.getScene().getWindow().hide();
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
                    stage.show();

                } else if (DataBaseHandler.isUserInPositionTable(connection, loginField.getText())) { ////////////////////////
                    authSignInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/demo1/author-panel.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                } else if (loginField.getText().equals("glavred") && passField.getText().equals("glavred")) {
                    authSignInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/demo1/glavRedaktor-panel.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else if (loginField.getText().equals("author") && passField.getText().equals("author")) {
                    authSignInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/demo1/author-panel.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                }else if (loginField.getText().equals("redotd") && passField.getText().equals("redotd")) {
                    authSignInButton.getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/demo1/redaktorOtdela-panel.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else if (loginField.getText().equals("admin") && passField.getText().equals("admin")) {
                    authSignInButton.getScene().getWindow().hide();
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
                }
            }
            else {
                Stage newWindow = new Stage();
                newWindow.setTitle("Ошибка!");

                Label label = new Label("Неверный логин или пароль. Пожалуйста, проверьте введенные данные.");

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
}
