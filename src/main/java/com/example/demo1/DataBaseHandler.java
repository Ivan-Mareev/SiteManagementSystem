package com.example.demo1;

import java.sql.*;
import java.sql.ResultSet;

public class DataBaseHandler extends Configs {

    public static void checkDriver () {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту согласно инструкции.");
            throw new RuntimeException(e);
        }
    }

    public static void checkDB () {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS);
        } catch (SQLException e) {
            System.out.println("Нет базы данных! Проверьте имя базы, путь к базе или разверните " +
                    "локально резервную копию согласно инструкции");
            throw new RuntimeException(e);
        }
    }

    public static void signUpUser(Connection connection, User user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + Const.USER_TABLE + "(" +
                        Const.USERS_FIRSTNAME + "," + Const.USERS_LASTNAME + "," +
                        Const.USERS_PATRONYMIC + "," + Const.USERS_LOGIN + "," + Const.USERS_PASSWORD + ")" + " VALUES(?,?,?,?,?)");
        preparedStatement.setString(1, user.getFirstName());
        preparedStatement.setString(2, user.getLastName());
        preparedStatement.setString(3, user.getPatronymic());
        preparedStatement.setString(4, user.getLogin());
        preparedStatement.setString(5, user.getPassword());
        preparedStatement.executeUpdate();
    }

    public ResultSet getUser(Connection connection, User user) throws SQLException {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USERS_LOGIN + "=? AND " + Const.USERS_PASSWORD + "=?";

        PreparedStatement preparedStatement = connection.prepareStatement(select);

        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPassword());

        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    public ResultSet getUserLogin(Connection connection, User user) throws SQLException {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USERS_LOGIN + "=?";

        PreparedStatement preparedStatement = connection.prepareStatement(select);

        preparedStatement.setString(1, user.getLogin());

        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    public ResultSet getAdminLogin(Connection connection, User user) throws SQLException {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.Admins_TABLE + " WHERE " +
                Const.ADMIN_LOGIN + "=?";

        PreparedStatement preparedStatement = connection.prepareStatement(select);

        preparedStatement.setString(1, user.getLogin());

        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    public static void addOnPosition(Connection connection, String login, String position) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + Const.positions_TABLE + "(" +
                        Const.LOGIN + "," + Const.POSITION + ") VALUES(?, ?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, position);
        preparedStatement.executeUpdate();
    }

    public ResultSet checkUserLogin(Connection connection, User user) throws SQLException {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USERS_LOGIN + "=?";

        PreparedStatement preparedStatement = connection.prepareStatement(select);
        preparedStatement.setString(1, user.getLogin());
        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }

    public static void removeFromPosition(Connection connection, String login) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM " + Const.positions_TABLE + " WHERE " + Const.LOGIN + " = ?");
        preparedStatement.setString(1, login);
        preparedStatement.executeUpdate();
    }

    public static ResultSet getWorkerLogin(Connection connection, String login) throws SQLException {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Const.positions_TABLE + " WHERE " +
                Const.LOGIN + "=?";

        PreparedStatement preparedStatement = connection.prepareStatement(select);

        preparedStatement.setString(1, login);

        resultSet = preparedStatement.executeQuery();

        return resultSet;
    }


    //Добавление публикации главный редактор zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz
    public static void publicationAdd(Connection connection, String themeName) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        String query = "INSERT INTO " + Const.publications_TABLE + " (" + Const.publication_name + ") VALUES (?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    public static void publicationDelete(Connection connection, String themeName) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        // Исправленный SQL-запрос
        String query = "DELETE FROM " + Const.publications_TABLE + " WHERE " + Const.publication_name + " = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Тема не найдена: " + themeName);
            } else {
                System.out.println("Тема успешно удалена: " + themeName);
            }
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    public static Boolean checkPublicationInDB(Connection connection, String themeName) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        // Исправленный SQL-запрос
        String query = "SELECT * FROM " + Const.publications_TABLE + " WHERE " + Const.publication_name + " = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Проверяем, есть ли хотя бы одна запись
                return resultSet.next(); // Вернет true, если запись найдена
            }
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }


    public static Boolean checkUserInDB(Connection connection, String login) throws SQLException {
        if (login == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        // Исправленный SQL-запрос
        String query = "SELECT * FROM " + Const.positions_TABLE + " WHERE " + Const.LOGIN + " = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Проверяем, есть ли хотя бы одна запись
                return resultSet.next(); // Вернет true, если запись найдена
            }
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    public static void addThemeToAuthor(Connection connection, String themeName, String login) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        String query = "INSERT INTO " + Const.theme_name_for_author_from_redotd_TABLE + " (" + Const.theme_name + ", " + Const.author_name + ") VALUES (?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            preparedStatement.setString(2, login);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    private void handleSQLException(SQLException e) {
        // Обработка различных SQL ошибок
        if (e.getSQLState().startsWith("23")) { // Код состояния для дублирования данных
            System.out.println("Ошибка: Дублирование данных");
        } else {
            System.out.println("Ошибка SQL: " + e.getMessage());
            e.printStackTrace(); // Для отладки
        }
    }

    public static void addPublicationForRedOtd(Connection connection, String themeName, String publication, String login) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        String query = "INSERT INTO " + Const.theme_name_for_redotd_from_author_TABLE + " (" + Const.theme + ", " + Const.publication + ", " + Const.author_login + ") VALUES (?,?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            preparedStatement.setString(2, publication);
            preparedStatement.setString(3, login);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    public static void addPublicationToAuthorOnDorabotka(Connection connection, String themeName, String publication) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        String query = "INSERT INTO " + Const.publicationforauthorondorabotka_TABLE + " (" + Const.theme_dorabotka + ", " + Const.publication_dorabotka + ") VALUES (?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            preparedStatement.setString(2, publication);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }

    public static void addToGlavRed(Connection connection, String themeName, String publication, String login) throws SQLException {
        if (themeName == null) {
            throw new IllegalArgumentException("Theme name cannot be null");
        }

        String query = "INSERT INTO " + Const.forglavred_TABLE + " (" + Const.themeForGlavRed + ", " + Const.publicationForGlavRed + ", " + Const.author_loginForGlavRed + ") VALUES (?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, themeName);
            preparedStatement.setString(2, publication);
            preparedStatement.setString(3, login);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // Обработка исключения
            e.printStackTrace();
            throw e; // или выбросить пользовательское исключение
        }
    }
    public static boolean isUserInPositionTable(Connection connection, String login) throws SQLException {
        String select = "SELECT COUNT(*) FROM " + Const.positions_TABLE + " WHERE " +
                Const.LOGIN + "=? AND " + Const.POSITION + "=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(select)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, "Автор");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Если количество найденных записей больше 0, возвращаем true
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        // Если записи не найдены, возвращаем false
        return false;
    }


}
