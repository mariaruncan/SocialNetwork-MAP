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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchFriendsController {
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
    private ChoiceBox<Integer> monthChoiceBox;


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
        initReport();
    }
    private void initReport(){
        monthChoiceBox.setValue(1);
        for(int i=1;i<=12;i++)
            monthChoiceBox.getItems().add(i);
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
    public void generateRaport(ActionEvent event) throws IOException {
        int month= monthChoiceBox.getValue();
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select an user!");
            return;
        }

        UserDTO friend = tableView.getSelectionModel().getSelectedItem();
        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            List<Message> messages = srv.reportUsersMessagesFriendMonth(user.getId(), friend.getId(), month).getRight();

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(25, 700);
                String line1 = "The user "+user.getFirstName()+" "+user.getLastName()+" received the following messages from the user "+
                        friend.getName()+" in the month"+String.valueOf(month)+ ":";
                cont.showText(line1);
                cont.newLine();
                for(Message m : messages) {
                    String line2 = m.getText()+";";
                    cont.newLine();
                    cont.showText(line2);
                }
                cont.newLine();
                cont.endText();
                System.out.println("finished");
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }

            doc.save("src/main/resources/Messages from a friend report.pdf");
        }



    }
}
