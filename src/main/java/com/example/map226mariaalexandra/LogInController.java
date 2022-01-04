package com.example.map226mariaalexandra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.repository.database.db.FriendRequestDbRepository;
import socialnetwork.repository.database.db.FriendshipDbRepository;
import socialnetwork.repository.database.db.MessageDbRepository;
import socialnetwork.repository.database.db.Repository;
import socialnetwork.service.Service;

import java.io.IOException;

public class LogInController {
    @FXML
    private Label user;
    @FXML
    private ChoiceBox<Long> idUsers;

    private Repository<Long,User> userRepo;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User userr;

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ops");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void switchToWelcomePage(ActionEvent event) throws IOException {
        if(idUsers.getValue() == null)
        {
            showAlert("Please select an user!");
            return;
        }

        String url = "jdbc:postgresql://localhost:5432/Lab4";
        String username = "postgres";
        String password = "parola";

        Repository<Tuple<User, User>, Friendship> friendshipRepository = new FriendshipDbRepository(url, username,
                password, new FriendshipValidator());
        Repository<Tuple<User, User>, FriendRequest> friendRequestRepository = new FriendRequestDbRepository(url,
                username, password,new FriendRequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository(url, username, password, userRepo);

        Service service = new Service(userRepo, friendshipRepository, messageRepo, friendRequestRepository);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcomePage.fxml"));
        root = loader.load();

        WelcomePageController controller = loader.getController();
        controller.setService(service);
        controller.setUser(userr);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void init() {
        if(userRepo != null)
            for(User u : userRepo.findAll())
                idUsers.getItems().add(u.getId());
        idUsers.setOnAction(this::onIdUserSelect);
    }

    private void onIdUserSelect(ActionEvent event) {
        Long idUser = idUsers.getValue();
        userr = userRepo.findOne(idUser);
        if(userRepo != null)
            for(User u : userRepo.findAll())
                if(u.getId().longValue() == idUser)
                    user.setText("  " + u.getFirstName() + " " + u.getLastName());
    }

    public void setRepo(Repository<Long, User> userRepo) {
        this.userRepo = userRepo;
        init();
    }
}