package com.example.map226mariaalexandra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import java.io.IOException;

public class AddFriendController {
    @FXML
    private Label friendName;
    @FXML
    private ChoiceBox<Long> choiceBox;

    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;


    private void init() {
        if(srv!=null)
            for( User u : srv.getAllUsers())
                choiceBox.getItems().add(u.getId().longValue());
        choiceBox.setOnAction(this::onIdUserSelect);
    }
    private void onIdUserSelect(ActionEvent event) {
        Long idUser = choiceBox.getValue();
        User u=srv.getUser(idUser);
        friendName.setText("  "+u.getFirstName()+" "+u.getLastName());

    }
    public void setService(Service srv) {
        this.srv=srv;
        init();
    }

    public void setUser(User user) {
        this.user=user;
    }

    public void switchMainPage(ActionEvent event) throws IOException {

        srv.addFriendRequest(new FriendRequest(user, srv.getUser(choiceBox.getValue())));
        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("loggedIn.fxml"));
        root=loader.load();
        MainPageController controller = loader.getController();
        controller.setService(srv);
        controller.setUser(user);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}
