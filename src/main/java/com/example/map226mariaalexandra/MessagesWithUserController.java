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
import javafx.scene.input.MouseEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.domain.utils.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagesWithUserController implements Observer {
    @FXML
    public TableView<MessageDTO> tableView;
    @FXML
    public TableColumn<MessageDTO, Long> id;
    @FXML
    public TableColumn<MessageDTO, String> from;
    @FXML
    public TableColumn<MessageDTO, String> to;
    @FXML
    public TableColumn<MessageDTO, String> text;
    @FXML
    public TableColumn<MessageDTO, LocalDateTime> date;
    @FXML
    public TableColumn<MessageDTO, String> reply;
    @FXML
    public TextField textField;
    @FXML
    public Button sendButton;
    @FXML
    public Button replyButton;
    @FXML
    public Button replyAllButton;
    @FXML
    public Label titleLabel;
    @FXML
    public Button nextButton;


    private Service srv;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User userLogged;
    private User userMessaged;
    private ObservableList<MessageDTO> messages;
    private int t=1;//page number

    public void setService(Service srv) {
        this.srv = srv;
        this.messages = FXCollections.observableArrayList();
        init();
    }

    private void init(){
        titleLabel.setText("Messages with " + userMessaged.getFirstName() + " " + userMessaged.getLastName());
        showMessages(1);
    }

    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showMessages(int t){
        tableView.getItems().clear();

        id.setCellValueFactory(new PropertyValueFactory<MessageDTO, Long>("id"));
        from.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("from"));
        to.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("to"));
        text.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("text"));
        date.setCellValueFactory(new PropertyValueFactory<MessageDTO, LocalDateTime>("date"));
        reply.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("reply"));

        for(Message m : srv.getChatsPagination(userLogged.getId(), userMessaged.getId(),t))
            messages.add(new MessageDTO(m));
        tableView.setItems(messages);
        this.srv.addObserver(this);
    }

    @FXML
    private void refreshTable(MouseEvent event){
        try {
            final List<Message> ms = srv.getChats(userLogged.getId(), userMessaged.getId());
            for( int i = messages.size();i<ms.size();i++)
                    this.messages.add(new MessageDTO(ms.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setUserLogged(User user) {
        this.userLogged = user;
    }

    public void setUserMessaged(User user){
        this.userMessaged = user;
    }

    public void onSendButtonClick(ActionEvent actionEvent) {
        String text = textField.getText();
        if(text.isEmpty()){
            showAlert("Ops", "Please write message text!");
            return;
        }

        textField.setText("");

        List<User> toList = new ArrayList<>();
        toList.add(userMessaged);
        srv.sendMessage(new Message(userLogged, toList, text));
    }
    public void nextPage(ActionEvent actionEvent) {
        t++;
        showMessages(t);

    }
    public void previousPage(ActionEvent actionEvent) {
        t--;
        showMessages(t);

    }

    public void onButtonReplyClick(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select a message!");
            return;
        }

        String text = textField.getText();
        if(text.isEmpty()){
            showAlert("Ops", "Please write message text!");
            return;
        }

        textField.setText("");

        Long id = tableView.getSelectionModel().getSelectedItem().getId();

        List<User> toList = new ArrayList<>();
        toList.add(userMessaged);
        Message msg = new Message(userLogged, toList, text);
        msg.setReply(srv.getMessage(id));
        srv.sendMessage(msg);
    }

    public void onReplyAllButton(ActionEvent actionEvent) {
        if(tableView.getSelectionModel().getSelectedItem() == null){
            showAlert("Ops", "Please select an user!");
            return;
        }

        String text = textField.getText();
        if(text.isEmpty()){
            showAlert("Ops", "Please write message text!");
            return;
        }

        textField.setText("");

        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        Message msg = srv.getMessage(id);
        srv.replyAll(msg, userLogged, text);

    }

    public void switchSearchUsersPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("searchFriends.fxml"));
        root = loader.load();

        SearchFriendsController controller = loader.getController();
        controller.setService(srv);
        controller.setUser(userLogged);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void update() {
        try {
            final List<Message> ms = srv.getChats(userLogged.getId(), userMessaged.getId());
            this.messages
                    .add(new MessageDTO(ms.get(ms.size() - 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
