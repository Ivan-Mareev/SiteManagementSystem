package com.example.demo1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class RedaktorOtdelaPrisvoitPanel {

    @FXML
    private Button addBtn;

    @FXML
    private TextField themeNameField;
    @FXML
    private Button btnBack;

    @FXML
    private TextField loginField;

    @FXML
    private TableColumn<Publication, Integer> publication_id; // Указываем тип Publication и Integer

    @FXML
    private TableColumn<Publication, String> publication_name; // Указываем тип Publication и String

    @FXML
    private TableView<Publication> table; // Указываем тип Publication

    // ObservableList для хранения данных из базы данных
    private ObservableList<Publication> publications = FXCollections.observableArrayList();

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

        loadPublicationsFromDatabase();
        table.setItems(publications); // Устанавливаем данные в TableView

        // Настройка столбцов с использованием PropertyValueFactory
        publication_id.setCellValueFactory(new PropertyValueFactory<>("id")); // Используем имя поля
        publication_name.setCellValueFactory(new PropertyValueFactory<>("publicationName")); // Используем имя поля

        addBtn.setOnAction(actionEvent -> {
            try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
                String theme = themeNameField.getText();
                String login = loginField.getText();
                if (DataBaseHandler.checkUserInDB(connection, login)) {
                    DataBaseHandler.addThemeToAuthor(connection, theme, login);
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Тема присвоена автору!");

                    Scene scene = new Scene(label, 400, 200);
                    newWindow.setScene(scene);

                    newWindow.show();
                } else {
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Успех!");

                    Label label = new Label("Автор не найден!");

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

    public void addThemeToAuthor() {

        try (Connection connection = DriverManager.getConnection(Configs.DATABASE_URL, Configs.USER_NAME, Configs.DATABASE_PASS)) {
            String themeName = themeNameField.getText();
            String login = loginField.getText();
            if (DataBaseHandler.checkUserInDB(connection, login)) {
                try {
                    DataBaseHandler.addThemeToAuthor(connection, themeName, login);
                    showSuccessWindow("Тема присвоена автору!");
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            } else {
                showErrorWindow("Автор не найден!");
            }
        } catch (SQLException e) {
            showErrorWindow("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }
    }

    private void showSuccessWindow(String message) {
        Stage newWindow = new Stage();
        newWindow.setTitle("Успех!");

        Label label = new Label(message);
        Scene scene = new Scene(label, 400, 200);
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void showErrorWindow(String message) {
        Stage newWindow = new Stage();
        newWindow.setTitle("Ошибка!");

        Label label = new Label(message);
        Scene scene = new Scene(label, 400, 200);
        newWindow.setScene(scene);
        newWindow.show();
    }

    private void handleSQLException(SQLException e) {
        if (e.getSQLState().startsWith("23")) {
            System.out.println("Произошло дублирование данных");
        } else {
            System.err.println("Ошибка SQL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }




    private void loadPublicationsFromDatabase() {
        try {
            // Подключение к базе данных
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/siteManagementSystem", "postgres", "postgres");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, publication_name FROM publications");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String publicationName = resultSet.getString("publication_name");

                // Создание объекта Publication и добавление в ObservableList
                publications.add(new Publication(id, publicationName));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Класс Publication
    public class Publication {
        private final SimpleIntegerProperty id; // Используем свойства
        private final SimpleStringProperty publicationName; // Используем свойства

        public Publication(int id, String publicationName) {
            this.id = new SimpleIntegerProperty(id);
            this.publicationName = new SimpleStringProperty(publicationName);
        }

        public int getId() {
            return id.get(); // Возвращаем значение свойства
        }

        public String getPublicationName() {
            return publicationName.get(); // Возвращаем значение свойства
        }
    }


}
