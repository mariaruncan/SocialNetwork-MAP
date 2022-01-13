package com.example.map226mariaalexandra;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.utils.HashPassword;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.database.db.*;
import socialnetwork.service.Service;

import java.io.IOException;

public class LogInController {
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameTextField;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Service srv;
    private LogInDbRepository logInRepo;

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ops");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void switchToWelcomePage(ActionEvent event) throws IOException {
        String username = usernameTextField.getText();
        if(username.isEmpty()){
            showAlert("Please enter username!");
            return;
        }

        String password = passwordField.getText();
        if(password.isEmpty()){
            showAlert("Please enter password!");
            return;
        }

        LogInCredentials logInCredentials = logInRepo.findOne(username);
        if(logInCredentials == null){
            showAlert("The user does not exist!");
            return;
        }

        if(!HashPassword.hash(password).equals(logInCredentials.getHashedPassword())){
            showAlert("Incorrect password!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcomePage.fxml"));
        root = loader.load();

        WelcomePageController controller = loader.getController();
        controller.setService(this.srv);
        controller.setUser(srv.getUser(logInCredentials.getId()));
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        controller.notifyEvents();
    }

    public void init() {
        String urlPostgres = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String usernamePostgres = "postgres";
        String passwordPostgres = "postgres";

        this.logInRepo = new LogInDbRepository(urlPostgres, usernamePostgres, passwordPostgres);

        Repository<Long, User> userRepo = new UserDbRepository(urlPostgres, usernamePostgres, passwordPostgres,
                new UserValidator());
        Repository<Tuple<User, User>, Friendship> friendshipRepository = new FriendshipDbRepository(urlPostgres,
                usernamePostgres, passwordPostgres, new FriendshipValidator());
        Repository<Tuple<User, User>, FriendRequest> friendRequestRepository = new FriendRequestDbRepository(urlPostgres,
                usernamePostgres, passwordPostgres, new FriendRequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository(urlPostgres, usernamePostgres, passwordPostgres,
                userRepo);
        EventDbRepository eventRepo = new EventDbRepository(urlPostgres, usernamePostgres, passwordPostgres, userRepo);

        this.srv = new Service(userRepo, friendshipRepository, messageRepo, friendRequestRepository, eventRepo);
    }

    public void switchCreateAccount(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
        root = loader.load();

        SignUpController controller = loader.getController();
        controller.setSrv(this.srv);
        controller.setLogInRepo(this.logInRepo);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}