package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GlavRedCheckPanel {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button btnBack;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Object[], Object> author_login;

    @FXML
    private TableColumn<Object[], Object> publication;

    @FXML
    private TableColumn<Object[], Object> publication_id;

    @FXML
    private TableColumn<Object[], Object> publication_name;

    @FXML
    private TableView<Object[]> table;

    private ObservableList<Object[]> publicationList = FXCollections.observableArrayList();

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
        // Настройка колонок таблицы
        publication_id.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()[0]));
        publication_name.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()[1]));
        publication.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()[2]));
        author_login.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()[3]));

        // Загрузка данных из базы данных
        loadPublications();

        // Устанавливаем данные в таблицу
        table.setItems(publicationList);
    }

    private void loadPublications() {
        String url = "jdbc:postgresql://localhost:5432/siteManagementSystem";
        String user = "postgres"; // Замените на ваше имя пользователя
        String password = "postgres"; // Замените на ваш пароль

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, theme_name, publication, author_login FROM forglavred")) {

            while (resultSet.next()) {
                Object[] row = new Object[4];
                row[0] = resultSet.getInt("id");
                row[1] = resultSet.getString("theme_name");
                row[2] = resultSet.getString("publication");
                row[3] = resultSet.getString("author_login");
                publicationList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
