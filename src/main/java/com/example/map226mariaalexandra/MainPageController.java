package com.example.map226mariaalexandra;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.FriendDTO;
import socialnetwork.service.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainPageController {
    @FXML
    private Label userLabel;

    @FXML
    private Button logOut;

    @FXML
    private  TableColumn<FriendDTO,Long> id;
    @FXML
    private  TableColumn<FriendDTO,String> name;
    @FXML
    private  TableColumn<FriendDTO,Date> date;

    @FXML
    private TableView<FriendDTO> tableView;

    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;


    private  void  displayName(User user){
        userLabel.setText("WELCOME "+ user.getFirstName()+" "+user.getLastName());
    }

    @FXML
    public void showFriends(){
        tableView.getItems().clear();
        List<Friendship> friends = srv.reportUserFriends(user.getId());

        id.setCellValueFactory(new PropertyValueFactory<FriendDTO,Long>("id"));
        name.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("name"));
        date.setCellValueFactory(new PropertyValueFactory<FriendDTO,Date>("date"));

        ObservableList<FriendDTO> objects = FXCollections.observableArrayList();
        for(Friendship f : friends)
            if(f.getUser1().getId()== user.getId())
                objects.add(new FriendDTO(f.getUser2().getId(),f.getUser2().getLastName()+" "+f.getUser2().getFirstName(),f.getDate()));
            else
                objects.add(new FriendDTO(f.getUser1().getId(),f.getUser1().getLastName()+" "+f.getUser1().getFirstName(),f.getDate()));

        tableView.setItems(objects);

    }

    public void switchMintPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mint.fxml"));
        root=loader.load();

        SocialNetworkController controller = loader.getController();
        controller.setRepo(srv.getUserRepo());
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchFriendRequests(){

    }

    public void addFriend(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        root=loader.load();

        AddFriendController controller = loader.getController();
        controller.setService(srv);
        controller.setUser(user);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    public void removeFriend(){
        Long id=tableView.getSelectionModel().getSelectedItem().getId();
        srv.removeFriendship(user.getId(),id);
        showFriends();

    }

    public void setUser(User userr) {
        this.user=userr;
        displayName(user);
        showFriends();
    }

    public void setService(Service service) {
        this.srv=service;
    }
}