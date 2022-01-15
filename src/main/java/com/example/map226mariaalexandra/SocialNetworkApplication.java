package com.example.map226mariaalexandra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ConcurrentModificationException;

public class SocialNetworkApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoaderA = new FXMLLoader(SocialNetworkApplication.class.getResource("logIn.fxml"));
        Parent root = fxmlLoaderA.load();
        stage.setTitle("MINT");
        LogInController ctrl = fxmlLoaderA.getController();
        ctrl.init();
        Scene scene = new Scene(root);
        Image icon = new Image("/logo.jpeg");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

}