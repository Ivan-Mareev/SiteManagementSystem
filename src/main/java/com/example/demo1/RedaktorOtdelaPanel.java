package com.example.demo1;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RedaktorOtdelaPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addBtn;

    @FXML
    private Button checkBtn;

    @FXML
    void initialize() {
//        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'redaktorOtdela-panel.fxml'.";
//        assert checkBtn != null : "fx:id=\"checkBtn\" was not injected: check your FXML file 'redaktorOtdela-panel.fxml'.";

        addBtn.setOnAction(actionEvent -> {
            addBtn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo1/RedaktorOtdelaPrisvoit-Panel.fxml"));
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

        checkBtn.setOnAction(actionEvent -> {
            addBtn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/demo1/redaktorOtdelaCheck-panel.fxml"));
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

}
