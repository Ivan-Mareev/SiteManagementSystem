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

public class AuthorDorabotkaPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addBtn;

    @FXML
    private Button btnBack;

    @FXML
    private TextField loginField;

    @FXML
    private TableColumn<Publication, String> publication;

    @FXML
    private TextField publicationField;

    @FXML
    private TableColumn<Publication, Integer> publication_id;

    @FXML
    private TableColumn<Publication, String> publication_name;

    @FXML
    private TableView<Publication> table;

    @FXML
    private TextField themeNameField;

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
        // Настройка колонок таблицы
        publication_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        publication_name.setCellValueFactory(new PropertyValueFactory<>("themeName"));
        publication.setCellValueFactory(new PropertyValueFactory<>("publication"));

        // Загрузка данных из базы данных
        loadPublications();

        // Устанавливаем данные в таблицу
        table.setItems(publicationList);

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

    }

    private void loadPublications() {
        String url = "jdbc:postgresql://localhost:5432/siteManagementSystem"; // Замените на ваш URL
        String user = "postgres"; // Замените на ваше имя пользователя
        String password = "postgres"; // Замените на ваш пароль

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, theme_name, publication FROM publicationforauthorondorabotka")) { // Замените your_table_name на имя вашей таблицы

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String themeName = resultSet.getString("theme_name");
                String publicationText = resultSet.getString("publication");
                publicationList.add(new Publication(id, themeName, publicationText));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class Publication {
        private int id;
        private String themeName;
        private String publication;
        private String authorLogin;

        public Publication(int id, String themeName, String publication) {
            this.id = id;
            this.themeName = themeName;
            this.publication = publication;
        }

        public int getId() {
            return id;
        }

        public String getThemeName() {
            return themeName;
        }

        public String getPublication() {
            return publication;
        }

        public String getAuthorLogin() {
            return authorLogin;
        }
    }
}
