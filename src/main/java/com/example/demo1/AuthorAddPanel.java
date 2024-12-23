package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AuthorAddPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private Button btnBack;

    @FXML
    private URL location;

    @FXML
    private Button addBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TextField publicationField;

    @FXML
    private TextField themeNameField;

    @FXML
    private TableColumn<Publication, Integer> publication_id;

    @FXML
    private TableColumn<Publication, String> publication_name;

    @FXML
    private TableView<Publication> table;

    private ObservableList<Publication> publicationList = FXCollections.observableArrayList();

    @FXML
    void initialize() {

        btnBack.setOnAction(actionEvent -> {
            btnBack.getScene().getWindow().hide();
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
        });

        addBtn.setOnAction(actionEvent -> {
            try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
                String login = loginField.getText();
                String themeName = themeNameField.getText();
                String publication = publicationField.getText();
                if (!login.isBlank() && !themeName.isBlank() && !publication.isBlank()) {
                    DataBaseHandler.addPublicationForRedOtd(connection, themeName, publication, login);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Публикация отправлена на проверку!");

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

        // Настройка cellValueFactory для колонок
        publication_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        publication_name.setCellValueFactory(new PropertyValueFactory<>("themeName"));

        loadPublications();
        table.setItems(publicationList);
    }

    // Метод для загрузки публикаций из базы данных
    private void loadPublications() {
        String url = "jdbc:postgresql://localhost:5432/siteManagementSystem"; // Замените на ваш URL
        String user = "postgres";
        String password = "postgres";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, theme_name FROM theme_name_for_author_from_redotd")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String themeName = resultSet.getString("theme_name");
                publicationList.add(new Publication(id, themeName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Класс для представления публикации
    public static class Publication {
        private int id;
        private String themeName;

        public Publication(int id, String themeName) {
            this.id = id;
            this.themeName = themeName;
        }

        public int getId() {
            return id;
        }

        public String getThemeName() {
            return themeName;
        }
    }
}
