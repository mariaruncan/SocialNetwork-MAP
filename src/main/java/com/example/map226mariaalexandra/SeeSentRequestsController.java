package com.example.map226mariaalexandra;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.FriendRequestDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.utils.Observer;
import socialnetwork.service.Service;

import java.io.IOException;
import java.util.List;

public class SeeSentRequestsController implements Observer {
    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;
    private  ObservableList<FriendRequestDTO> requests;


    @FXML
    private TableColumn<FriendRequestDTO, String> name;
    @FXML
    private  TableColumn<FriendRequestDTO, String> status;
    @FXML
    private  TableColumn<FriendRequestDTO, Long> userId;

    @FXML
    private TableView<FriendRequestDTO> tableView;

    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    public void onCancelButtonClick(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select a friend request!");
            return;
        }
        Long id = tableView.getSelectionModel().getSelectedItem().getUserId();
        User userTo = srv.getUser(id);
        FriendRequest f = srv.removeFriendRequest(user, userTo);
        if(f != null) {
            showAlert("Yay", "You canceled your friend request!");
            showFriendRequests();
        }
        else
            showAlert("Ops", "Can not cancel! :(");
    }


    private void init() {
        showFriendRequests();
    }

    public void showFriendRequests(){
        tableView.getItems().clear();
        List<FriendRequest> friendRequests = srv.getUserSentFriendRequests(user.getId());

        userId.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, Long>("userId"));
        name.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("name"));
        status.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("status"));

        for(FriendRequest f : friendRequests)
            requests.add(new FriendRequestDTO(f.getTo().getId(), f.getTo().getFirstName() + " " + f.getTo().getLastName(), f.getStatus()));

        tableView.setItems(requests);
        this.srv.addObserver(this);

    }

    public void setService(Service srv) {
        this.srv = srv;
        this.requests = FXCollections.observableArrayList();
        init();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void switchWelcomePage(ActionEvent event) throws IOException {

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


    @Override
    public void update() {
        this.requests.clear();
        List<FriendRequest> friendRequests = srv.getUserSentFriendRequests(user.getId());
        for(FriendRequest f : friendRequests)
            requests.add(new FriendRequestDTO(f.getTo().getId(), f.getTo().getFirstName() + " " + f.getTo().getLastName(), f.getStatus()));

    }
}
