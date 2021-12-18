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
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.repository.database.db.FriendRequestDbRepository;
import socialnetwork.repository.database.db.FriendshipDbRepository;
import socialnetwork.repository.database.db.MessageDbRepository;
import socialnetwork.repository.database.db.Repository;
import socialnetwork.service.Service;

import java.io.IOException;

public class SocialNetworkController {
    @FXML
    private Label user;
    @FXML
    private ChoiceBox<Long> idUsers;

    private Repository<Long,User> userRepo;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User userr;

    public void switchToScene2(ActionEvent event) throws IOException {

        Repository<Tuple<User, User>, Friendship> friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",new FriendshipValidator());
        Repository<Tuple<User, User>, FriendRequest> friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",new FriendRequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",userRepo);

        Service service = new Service(userRepo,friendshipRepository,messageRepo, friendRequestRepository);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("loggedIn.fxml"));
        root=loader.load();

        MainPageController controller = loader.getController();
        controller.setService(service);
        controller.setUser(userr);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void init() {
        if(userRepo!=null)
        for( User u : userRepo.findAll())
            idUsers.getItems().add(u.getId().longValue());
        idUsers.setOnAction(this::onIdUserSelect);
    }

    private void onIdUserSelect(ActionEvent event) {
        Long idUser = idUsers.getValue();
        userr=userRepo.findOne(idUser);
        if(userRepo!=null)
        for( User u : userRepo.findAll())
            if(u.getId().longValue()==idUser)
                user.setText("  "+u.getFirstName()+" "+u.getLastName());

    }

    public void setRepo(Repository<Long, User> userRepo) {
        this.userRepo=userRepo;
        init();
    }
}