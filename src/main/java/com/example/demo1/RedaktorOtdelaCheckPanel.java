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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RedaktorOtdelaCheckPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private Button btnBack;

    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<Publication, String> author; // Указываем конкретный тип
    @FXML
    private Button dorabotkaBtn;

    @FXML
    private TextField loginField;

    @FXML
    private TableColumn<Publication, String> publication; // Указываем конкретный тип

    @FXML
    private TextField publicationField;

    @FXML
    private TableColumn<Publication, Integer> publication_id; // Указываем конкретный тип

    @FXML
    private TableColumn<Publication, String> publication_name; // Указываем конкретный тип

    @FXML
    private TableView<Publication> table; // Указываем конкретный тип

    @FXML
    private TextField themeNameField;

    private ObservableList<Publication> publicationList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        btnBack.setOnAction(actionEvent -> {
            btnBack.getScene().getWindow().hide();
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
        });
        // Настройка колонок таблицы
        publication_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        publication_name.setCellValueFactory(new PropertyValueFactory<>("themeName"));
        publication.setCellValueFactory(new PropertyValueFactory<>("publication"));
        author.setCellValueFactory(new PropertyValueFactory<>("authorLogin"));

        // Загрузка данных из базы данных
        loadPublications();

        // Устанавливаем данные в таблицу
        table.setItems(publicationList);

        /*// Обработчик событий для кнопок
        addBtn.setOnAction(event -> handleAdd());
        dorabotkaBtn.setOnAction(event -> handleDorabotka());*/
        table.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {
                copySelectedCell();
                event.consume(); // Отменяем дальнейшую обработку события
            }
        });

        dorabotkaBtn.setOnAction(actionEvent -> {
            try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
                String login = loginField.getText();
                String themeName = themeNameField.getText();
                String publication = publicationField.getText();
                if (!login.isBlank() && !themeName.isBlank() && !publication.isBlank()) {
                    DataBaseHandler.addPublicationToAuthorOnDorabotka(connection, themeName, publication);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Публикация отправлена на доработку!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Ошибка!");

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
        addBtn.setOnAction(actionEvent -> {
            try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
                String login = loginField.getText();
                String themeName = themeNameField.getText();
                String publication = publicationField.getText();
                if (!login.isBlank() && !themeName.isBlank() && !publication.isBlank()) {
                    DataBaseHandler.addToGlavRed(connection, themeName, publication, login);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Публикация отправлена Одобрена!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Ошибка!");

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

    // Метод для загрузки публикаций из базы данных
    private void loadPublications() {
        String url = "jdbc:postgresql://localhost:5432/siteManagementSystem"; // Замените на ваш URL
        String user = "postgres"; // Замените на ваше имя пользователя
        String password = "postgres"; // Замените на ваш пароль

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, theme_name, publication, author_login FROM theme_name_for_redotd_from_author")) { // Укажите ваше имя таблицы

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String themeName = resultSet.getString("theme_name");
                String publicationText = resultSet.getString("publication");
                String authorLogin = resultSet.getString("author_login");
                publicationList.add(new Publication(id, themeName, publicationText, authorLogin)); // Добавляем данные в список
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void copySelectedCell() {
        Publication selectedPublication = table.getSelectionModel().getSelectedItem();
        if (selectedPublication != null) {
            // Получаем выбранную колонку
            int selectedIndex = table.getSelectionModel().getSelectedCells().get(0).getColumn();
            String valueToCopy = null;

            // Определяем, какое значение нужно скопировать
            switch (selectedIndex) {
                case 0: // publication_id
                    valueToCopy = String.valueOf(selectedPublication.getId());
                    break;
                case 1: // publication_name
                    valueToCopy = selectedPublication.getThemeName();
                    break;
                case 2: // publication
                    valueToCopy = selectedPublication.getPublication();
                    break;
                case 3: // author
                    valueToCopy = selectedPublication.getAuthorLogin();
                    break;
            }

            if (valueToCopy != null) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(valueToCopy);
                clipboard.setContent(content);
            }
        }
    }

    // Метод для обработки события кнопки "Одобрить"
    /*private void handleAdd() {
        System.out.println("Одобрить публикацию: " + loginField.getText());
        clearFields();
    }

    // Метод для обработки события кнопки "Отправить на доработку"
    private void handleDorabotka() {
        System.out.println("Отправить на доработку: " + publicationField.getText());
        clearFields();
    }

    // Метод для очистки текстовых полей
    private void clearFields() {
        loginField.clear();
        themeNameField.clear();
        publicationField.clear();
    }*/
    public class Publication {
        private int id;
        private String themeName;
        private String publication;
        private String authorLogin;

        public Publication(int id, String themeName, String publication, String authorLogin) {
            this.id = id;
            this.themeName = themeName;
            this.publication = publication;
            this.authorLogin = authorLogin;
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
