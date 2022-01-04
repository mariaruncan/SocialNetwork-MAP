package com.example.map226mariaalexandra;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchUsersController {
    @FXML
    public TableView<UserDTO> tableView;
    @FXML
    public TableColumn<UserDTO, Long> id;
    @FXML
    public TableColumn<UserDTO, String> name;
    @FXML
    public TextField nameTextField;
    @FXML
    public Button sendButton;
    @FXML
    public Button seeRequestsButton;


    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;
    private List<User> allUsers;


    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void init(){
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> showUsers());
    }

    private void showUsers() {
        tableView.getItems().clear();
        List<User> users = allUsers.stream()
                .filter(x -> (x.getFirstName() + " " + x.getLastName()).startsWith(nameTextField.getText()))
                .collect(Collectors.toList());


        id.setCellValueFactory(new PropertyValueFactory<UserDTO, Long>("id"));
        name.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("name"));

        ObservableList<UserDTO> objects = FXCollections.observableArrayList();
        for(User u : users)
            objects.add(new UserDTO(u.getId(), u.getFirstName() + " " + u.getLastName()));

        tableView.setItems(objects);
    }

    public void setService(Service srv) {
        this.srv = srv;
        allUsers = StreamSupport.stream(srv.getAllUsers().spliterator(), false).collect(Collectors.toList());
        init();
        showUsers();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void switchMainPage(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("welcomePage.fxml"));
        root=loader.load();
        WelcomePageController controller = loader.getController();
        controller.setService(srv);
        controller.setUser(user);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void onSendButtonClick(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select an user!");
            return;
        }

        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        User userSelected = srv.getUser(id);
        try {
            FriendRequest fr = srv.addFriendRequest(new FriendRequest(user, userSelected));
            if(fr != null) {
                showAlert("Yay", "Friend request sent to " + userSelected.getFirstName() + " " + userSelected.getLastName());
            }
            else
                showAlert("Ops", "Can not send friend request!");
        }
        catch(ValidationException ex){
            showAlert("Ops", ex.getMessage());
        }

    }

    public void onSeeRequestsButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("seeSentRequests.fxml"));
        root=loader.load();
        SeeSentRequestsController controller = loader.getController();
        controller.setUser(user);
        controller.setService(srv);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void onSeeMessagesButtonClick(ActionEvent event) throws IOException{
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select an user!");
            return;
        }


        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        if(id == user.getId()) {
            showAlert("Ops", "Can not send a message to yourself!");
            return;
        }

        User userSelected = srv.getUser(id);

        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("messagesWithUser.fxml"));
        root = loader.load();
        MessagesWithUserController controller = loader.getController();
        controller.setUserLogged(user);
        controller.setUserMessaged(userSelected);
        controller.setService(srv);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
