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
import socialnetwork.domain.*;
import socialnetwork.domain.utils.Observer;
import socialnetwork.service.Service;

import java.io.IOException;
import java.util.List;

public class WelcomePageController implements Observer {
    @FXML
    private Label userLabel;
    @FXML
    private Button logOut;
    @FXML
    public Button seeRequestsButton;
    @FXML
    private ChoiceBox<Integer> monthChoiceBox;
    @FXML
    private  TableColumn<FriendDTO,String> Friends;
    @FXML
    private  TableColumn<FriendDTO,Long> Id;
    @FXML
    private  TableColumn<MessageDTO,String> From;
    @FXML
    private  TableColumn<MessageDTO,String> Inbox;

    @FXML
    private TableView<FriendDTO> tableFriends;
    @FXML
    private TableView<MessageDTO> tableInbox;

    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;
    private Page page;
    private  ObservableList<FriendDTO> friendList;
    private  ObservableList<MessageDTO> messageList;


    private  void  displayName(){
        userLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
    }


    @FXML
    public void showFriends(){
        tableFriends.getItems().clear();
        Friends.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("name"));
        Id.setCellValueFactory(new PropertyValueFactory<FriendDTO,Long>("id"));
        for(User f : page.getFriends())
                friendList.add(new FriendDTO(f.getId(),f.getFirstName() + " " + f.getLastName()));
        tableFriends.setItems(friendList);
        this.srv.addObserver(this);

    }
    @FXML
    public void showInbox(){
        tableInbox.getItems().clear();
        From.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("from"));
        Inbox.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("text"));
        for(Message m : page.getReceivedMessages())
                messageList.add(new MessageDTO(m));
        tableInbox.setItems(messageList);
        this.srv.addObserver(this);

    }

    public void switchToLogInPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("logIn.fxml"));
        root=loader.load();

        LogInController controller = loader.getController();
        controller.setRepo(srv.getUserRepo());
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToFriendRequestsPage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("friendRequests.fxml"));
        root = loader.load();

        FriendRequestsController controller = loader.getController();
        controller.setUser(user);
        controller.setService(srv);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onMessengerButtonClick(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("searchFriends.fxml"));
        root = loader.load();

        SearchFriendsController controller = loader.getController();
        controller.setService(srv);
        controller.setUser(user);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void generateRaport(ActionEvent event) throws IOException {
        int month= monthChoiceBox.getValue();
        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            List<User> friends = srv.reportUsersFriendsMonth(user.getId(), month).getRight();
            List<Message> messages = srv.reportUsersMessagesMonth(user.getId(),month).getRight();

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(25, 700);
                String line1 = "The user "+user.getFirstName()+" "+user.getLastName()+" made friends in the month "+ String.valueOf(month)+ " :";
                cont.showText(line1);
                cont.newLine();
                for(User u : friends) {
                    String line2 = u.getFirstName()+" "+u.getLastName()+";";
                    cont.showText(line2);
                }
                cont.newLine();

                String line3 ="The user "+user.getFirstName()+" "+user.getLastName()+" received the following messages in the month "+ String.valueOf(month)+ " :";
                cont.showText(line3);
                cont.newLine();
                for(Message m : messages) {
                    String line2 = m.getFrom().getFirstName()+" "+m.getFrom().getLastName()+": "+m.getText()+";";
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

            doc.save("src/main/resources/Recent activities report.pdf");
        }



    }

    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void onRemoveFriendButtonClick(){
        if(tableFriends.getSelectionModel().getSelectedItem() == null) {
            showAlert("Ops", "Please select a friend!");
            return;
        }
        Long id = tableFriends.getSelectionModel().getSelectedItem().getId();
        srv.removeFriendship(user.getId(), id);

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

    public void setUser(User userr) {
        this.user = userr;
        user.setFriends(srv.reportUserFriends(user.getId()));
        setPage();
        displayName();
        showFriends();
        showInbox();
        initReport();
    }

    private void initReport(){
        monthChoiceBox.setValue(1);
            for(int i=1;i<=12;i++)
                monthChoiceBox.getItems().add(i);
    }

    private void setPage() {
        this.page= new Page(user.getFirstName(), user.getLastName(),user.getFriends(),srv.getInbox(user),srv.getUserFriendRequests(user.getId()));
    }

    public void setService(Service service) {
        this.srv=service;
        this.friendList=FXCollections.observableArrayList();
        this.messageList=FXCollections.observableArrayList();
    }

    @Override
    public void update() {
        this.friendList.clear();
        this.messageList.clear();
        user.setFriends(srv.reportUserFriends(user.getId()));
        page.setFriends(user.getFriends());
        page.setReceivedMessages(srv.getInbox(user));
        for(User f : page.getFriends())
            friendList.add(new FriendDTO(f.getId(),f.getFirstName() + " " + f.getLastName()));
        for(Message m : page.getReceivedMessages())
            messageList.add(new MessageDTO(m));
    }

}