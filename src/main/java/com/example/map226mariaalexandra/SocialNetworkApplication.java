package com.example.map226mariaalexandra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.db.*;

import java.io.IOException;

public class SocialNetworkApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

   private Repository<Long, User> userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/Lab4",
           "postgres", "parola",new UserValidator());

    @Override
        public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoaderA = new FXMLLoader(SocialNetworkApplication.class.getResource("mint.fxml"));
        Parent root = fxmlLoaderA.load();
        stage.setTitle("MINT");
        SocialNetworkController ctrl=fxmlLoaderA.getController();
        ctrl.setRepo(userRepo);
        Scene scene = new Scene(root);
        Image icon = new Image("/logo.jpeg");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
        }

}