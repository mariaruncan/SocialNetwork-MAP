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
import socialnetwork.domain.utils.MyThread;
import socialnetwork.domain.utils.Observer;
import socialnetwork.service.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WelcomePageController implements Observer {
    @FXML
    public TextField eventNameTextField;
    @FXML
    private Label userLabel;
    @FXML
    public Button seeRequestsButton;
    @FXML
    private ChoiceBox<Integer> monthChoiceBox;
    @FXML
    private  TableColumn<FriendDTO,String> friends;
    @FXML
    private  TableColumn<FriendDTO,Long> id;
    @FXML
    private  TableColumn<MessageDTO,String> from;
    @FXML
    private  TableColumn<MessageDTO,String> inbox;
    @FXML
    private TableColumn<EventDTO, String> eventName;
    @FXML
    private TableColumn<EventDTO, LocalDate> eventDate;
    @FXML
    private TableColumn<EventDTO, Boolean> eventSub;
    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<EventDTO> tableEvents;
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
    private ObservableList<FriendDTO> friendList;
    private ObservableList<MessageDTO> messageList;
    private ObservableList<EventDTO> eventList;
    private MyThread notifyThread = null;


    private  void  displayName(){
        userLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
    }


    @FXML
    public void showFriends(){
        tableFriends.getItems().clear();
        friends.setCellValueFactory(new PropertyValueFactory<>("name"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        for(User f : page.getFriends())
                friendList.add(new FriendDTO(f.getId(),f.getFirstName() + " " + f.getLastName()));
        tableFriends.setItems(friendList);

    }
    @FXML
    public void showInbox(){
        tableInbox.getItems().clear();
        from.setCellValueFactory(new PropertyValueFactory<>("from"));
        inbox.setCellValueFactory(new PropertyValueFactory<>("text"));
        for(Message m : page.getReceivedMessages())
                messageList.add(new MessageDTO(m));
        tableInbox.setItems(messageList);
    }

    @FXML
    public void showEvents(){
        tableEvents.getItems().clear();
        eventName.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventSub.setCellValueFactory(new PropertyValueFactory<>("subscribed"));
        for(Event e : page.getEventList()){
            boolean sub = false;
            User us = e.getSubscribedUsers().stream()
                    .filter(x -> x.getId().equals(this.user.getId()))
                    .findAny()
                    .orElse(null);
            if(us != null)
                sub = true;
            EventDTO eventDTO = new EventDTO(e.getId(), e.getName(), e.getDate(), sub);
            eventList.add(eventDTO);
        }
        tableEvents.setItems(eventList);
    }

    public void switchToLogInPage(ActionEvent event) throws IOException {
        this.notifyThread.stop();
        this.notifyThread = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("logIn.fxml"));
        root = loader.load();

        LogInController controller = loader.getController();
        controller.init();
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

    public void generateReport(ActionEvent event) throws IOException {
        int month = monthChoiceBox.getValue();
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
                String line1 = "The user " + user.getFirstName() + " " + user.getLastName() + " made friends in the month "
                        + month + " :";
                cont.showText(line1);
                cont.newLine();
                for(User u : friends) {
                    String line2 = u.getFirstName() + " " + u.getLastName() + ";";
                    cont.showText(line2);
                }
                cont.newLine();

                String line3 = "The user " + user.getFirstName() + " " + user.getLastName() +
                        " received the following messages in the month " + month + " :";
                cont.showText(line3);
                cont.newLine();
                for(Message m : messages) {
                    String line2 = m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " +
                            m.getText() + ";";
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
        this.srv.addObserver(this);
        srv.removeFriendship(user.getId(), id);
    }

    public void onSeeRequestsButtonClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("seeSentRequests.fxml"));
        root = loader.load();
        SeeSentRequestsController controller = loader.getController();
        controller.setUser(user);
        controller.setService(srv);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setUser(User user) {
        this.user = user;
        user.setFriends(srv.reportUserFriends(user.getId()));
        setPage();
        displayName();
        showFriends();
        showInbox();
        showEvents();
        initReport();
        this.notifyThread = new MyThread(this);
    }

    public void notifyEvents(){
        StringBuilder message = new StringBuilder();
        List<Event> upcomingEvents = page.getEventList().stream()
                .filter(ev -> ev.getSubscribedUsers().stream().anyMatch(u -> u.getId().equals(this.user.getId())))
                .collect(Collectors.toList());

        message = new StringBuilder("You have " + upcomingEvents.size() + " upcoming events.\n");
        for(Event ev : upcomingEvents){
            message.append(ev.getName());
            message.append(" - ");
            message.append(ev.getDate());
            message.append("\n");
        }
        showAlert("Events", message.toString());
    }

    private void initReport(){
        monthChoiceBox.setValue(1);
        for(int i = 1; i <= 12; i++)
            monthChoiceBox.getItems().add(i);
    }

    private void setPage() {
        this.page = new Page(user.getFirstName(), user.getLastName(), user.getFriends(), srv.getInbox(user),
                srv.getUserFriendRequests(user.getId()), StreamSupport.stream(srv.findAllEvents().spliterator(), false)
                .collect(Collectors.toList()));
    }

    public void setService(Service service) {
        this.srv = service;
        this.friendList = FXCollections.observableArrayList();
        this.messageList = FXCollections.observableArrayList();
        this.eventList = FXCollections.observableArrayList();
    }

    @Override
    public void update() {
        this.friendList.clear();
        this.messageList.clear();
        this.eventList.clear();

        user.setFriends(srv.reportUserFriends(user.getId()));
        page.setFriends(user.getFriends());
        page.setReceivedMessages(srv.getInbox(user));
        page.setEventList(StreamSupport.stream(srv.findAllEvents().spliterator(), false)
                .collect(Collectors.toList()));

        for(Event e : page.getEventList()){
            boolean sub = false;
            User us = e.getSubscribedUsers().stream()
                    .filter(x -> x.getId().equals(this.user.getId()))
                    .findAny()
                    .orElse(null);
            if(us != null)
                sub = true;
            EventDTO eventDTO = new EventDTO(e.getId(), e.getName(), e.getDate(), sub);
            eventList.add(eventDTO);
        }
        for(User f : page.getFriends())
            friendList.add(new FriendDTO(f.getId(),f.getFirstName() + " " + f.getLastName()));
        for(Message m : page.getReceivedMessages())
            messageList.add(new MessageDTO(m));
        this.srv.removeObserver(this);
    }

    public void onAddEventButtonClicked(ActionEvent actionEvent) {
        String name = eventNameTextField.getText();
        if(name.isEmpty()) {
            showAlert("Ops", "Please enter event name!");
            return;
        }

        LocalDate date = datePicker.getValue();
        this.srv.addObserver(this);
        srv.addEvent(name, date);

        eventNameTextField.setText("");
    }

    public void onSubUnsubButtonClicked(ActionEvent actionEvent) {
        EventDTO ev = tableEvents.getSelectionModel().getSelectedItem();
        if(ev == null){
            showAlert("Ops", "Please select an event!");
            return;
        }

        Boolean sub = ev.getSubscribed();
        this.srv.addObserver(this);
        if(sub)
            srv.unsubscribeFromEvent(ev.getId(), user.getId());
        else
            srv.subscribeToEvent(ev.getId(), user.getId());
    }
}