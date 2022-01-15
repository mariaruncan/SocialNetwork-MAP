package com.example.map226mariaalexandra;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.LogInCredentials;
import socialnetwork.domain.User;
import socialnetwork.domain.utils.HashPassword;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.db.LogInDbRepository;
import socialnetwork.service.Service;

import java.io.IOException;

public class SignUpController {
    
    public TextField firstNameTextField;
    public PasswordField passwordField;
    public TextField lastNameTextField1;
    public TextField usernameTextField;
    public PasswordField repeatPasswordField;

    private Stage stage;
    private Service srv;
    private LogInDbRepository logInRepo;

    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void createAccount(ActionEvent actionEvent) throws IOException {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField1.getText();
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String rePassword = repeatPasswordField.getText();

        if(firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
            showAlert("Ops", "Please do not leave empty fields!");
            return;
        }

        try{
            HashPassword.validate(password, rePassword);
            User user = srv.addUser(firstName, lastName);
            logInRepo.save(new LogInCredentials(user.getId(), username, HashPassword.hash(password)));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("logIn.fxml"));
            Parent root = loader.load();

            LogInController controller = loader.getController();
            controller.init();
            stage =(Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (ValidationException ex){
            showAlert("Ops", ex.getMessage());
        }
    }

    public void setSrv(Service srv) {
        this.srv = srv;
    }

    public void setLogInRepo(LogInDbRepository logInRepo) {
        this.logInRepo = logInRepo;
    }
}
