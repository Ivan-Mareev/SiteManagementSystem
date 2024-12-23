package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class GlavRedAddPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnBack;

    @FXML
    private Button addBtn;

    @FXML
    private TextField txtField;

    @FXML
    void initialize() {
        btnBack.setOnAction(actionEvent -> {
            btnBack.getScene().getWindow().hide();
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
        });
//        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'glavredAdd-panel.fxml'.";
//        assert txtField != null : "fx:id=\"txtField\" was not injected: check your FXML file 'glavredAdd-panel.fxml'.";
        addBtn.setOnAction(actionEvent -> {
            try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
                String theme = txtField.getText();
                if (!theme.isBlank()) {
                    DataBaseHandler.publicationAdd(connection, theme);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Тема добавлена!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Заполните недостающие поля!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                }
            } catch (SQLException e) {
                if (e.getSQLState().startsWith("23")) {
                    System.out.println("Произошло дублирование данных");
                } else {
                    System.out.println("Ошибка при добавлении темы: " + e.getMessage());
                }
            }
        });
    }
}


