package socialnetwork.ui;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/***
 * Ui class
 * connects user to app
 */
public class Ui {

    private final Service srv;
    private final Scanner in = new Scanner(System.in);
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    /**
     *constructor
     * @param service - Service
     */
    public Ui(Service service) {
        this.srv = service;
    }

    public void run(){
        System.out.println("Social Network");
        int cmd = -1;
        while(cmd != 0){
            try{
                cmd = readCommand();
                switch(cmd){
                    case 0:
                        break;
                    case 1:
                        uiAddUser();
                        break;
                    case 2:
                        uiRemoveUser();
                        break;
                    case 3:
                        uiPrintUsers();
                        break;
                    case 4:
                        uiAddFriendship();
                        break;
                    case 5:
                        uiRemoveFriendship();
                        break;
                    case 6:
                        uiPrintFriendships();
                        break;
                    case 7:
                        uiCommunitiesNumber();
                        break;
                    case 8:
                        uiMostSociableCommunity();
                        break;
                    case 9:
                        uiReportUsersFriends();
                        break;
                    case 10:
                        uiReportUsersFriendsMonth();
                        break;
                    case 11:
                        uiChronologicalMessages();
                        break;
                    case 12:
                        uiLogIn();
                        break;
                    default:
                        System.out.println("Invalid command!");
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Byeeee :)");
    }


    private int readCommand(){
        System.out.println("---------------------------");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Print all users");
        System.out.println("4. Add friendship");
        System.out.println("5. Remove friendship");
        System.out.println("6. Print all friendships");
        System.out.println("7. Communities number");
        System.out.println("8. The most sociable community");
        System.out.println("9. Report1: user's friends");
        System.out.println("10. Report2: user's friends from a given month");
        System.out.println("11. Show messages in chronological order for 2 users");
        System.out.println("12. Log in");
        System.out.println("0. Exit");
        System.out.println("---------------------------");
        System.out.println("Your choice: ");

        return in.nextInt();
    }

    private void uiAddUser(){
        System.out.println("First name: ");
        String firstName = in.next();
        System.out.println("Last name: ");
        String lastName = in.next();
        User user = srv.addUser(firstName, lastName);
        if(user != null)
            System.out.println("User added!");
        else
            System.out.println();
    }


    private void uiRemoveUser(){
        System.out.println("Id: ");
        String input = in.next();
        long id = Long.parseLong(input);
        User user = srv.removeUser(id);
        if(user != null)
            System.out.println(user + " has been removed!");
        else
            System.out.println("There is no user with id " + id);
    }

    private void uiPrintUsers(){
        System.out.println("All users:");
        for(User user: srv.getAllUsers())
            System.out.println(user);
    }

    private void uiAddFriendship(){
        System.out.println("Users, separated by enter: ");
        long id1 = in.nextLong();
        long id2 = in.nextLong();
        Friendship friendship = srv.addFriendship(id1, id2);
        if(friendship != null)
            System.out.println("The users are now friends!");
        else
            System.out.println("The users were already friends!");
    }

    private void uiRemoveFriendship(){
        System.out.println("Users, separated by enter: ");
        long id1 = in.nextLong();
        long id2 = in.nextLong();
        Friendship friendship = srv.removeFriendship(id1, id2);
        if(friendship == null)
            System.out.println("The users don't exist or they were not friends!");
        else
            System.out.println("The users are no longer friends!");
    }

    private void uiPrintFriendships(){
        System.out.println("All friendships:");
        for(Friendship friendship: srv.getAllFriendships())
            System.out.println(friendship);
    }

    private void uiCommunitiesNumber(){
        System.out.println("There are " + srv.communitiesNumber() + " communities!");
    }

    private void uiMostSociableCommunity(){
        List<Integer> list = srv.mostSociableCommunity();
        System.out.println("The most sociable community is formed from users: ");
        for(int i : list)
            System.out.println(i + " ");
        System.out.println();
    }


    private void uiReportUsersFriends() {
        System.out.println("User's id:");
        Long id = in.nextLong();
        List<Friendship> rez = srv.reportUserFriends(id);
        if(rez.isEmpty())
            System.out.println("No friendships");
        rez.forEach(x -> {
            if (x.getUser1().getId().equals(id))
                System.out.println(x.getUser2().getFirstName() + " " + x.getUser2().getLastName() + " " + x.getDate());

            if (x.getUser2().getId().equals(id))
                System.out.println(x.getUser1().getFirstName() + " " + x.getUser1().getLastName() + " " + x.getDate());
        });

    }

    private void uiReportUsersFriendsMonth(){
        System.out.println("User's id:");
        Long id = in.nextLong();
        System.out.println("Month:");
        int month = in.nextInt();
        if(month < 1 || month > 12) {
            System.out.println("Invalid month!");
            return;
        }

        Tuple<User, List<User>> result = srv.reportUsersFriendsMonth(id, month);
        User user = result.getLeft();
        List<User> list = result.getRight();
        System.out.println("User " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() +
                " friends from month " + month + " are:");
        for(User u: list)
            System.out.println(u.getId() + " " + u.getFirstName() + " " + u.getLastName());
    }


    private void uiLogIn(){
        System.out.println("User's id:");
        Long id = in.nextLong();
        User user = srv.getUser(id);

        if(user == null){
            System.out.println("The user with the given id does not exist!");
            return;
        }

        System.out.println("Logged in as " + user);
        uiLogInMenu(user);
    }

    private void uiLogInMenu(User user){
        int cmd = -1;
        while(cmd != 0){
            try{
                cmd = readCommandLogIn();
                switch(cmd){
                    case 0:
                        break;
                    case 1:
                        uiPrintFriendRequests(user);
                        break;
                    case 2:
                        uiSendFriendRequest(user);
                        break;
                    case 3:
                        uiAcceptFriendRequest(user);
                        break;
                    case 4:
                        uiRejectFriendRequest(user);
                        break;
                    case 5:
                        uiReplyAll(user);
                        break;
                    default:
                        System.out.println("Invalid command!");
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Logged out");
    }


    private int readCommandLogIn(){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("1. Print friend requests");
        System.out.println("2. Send friend request");
        System.out.println("3. Accept friend request");
        System.out.println("4. Reject friend request");
        System.out.println("5. Reply all to a message");
        System.out.println("0. Log out");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Your choice: ");

        return in.nextInt();
    }

    private void uiPrintFriendRequests(User user){
        srv.getUserFriendRequests(user.getId()).forEach(System.out::println);
    }

    private void uiSendFriendRequest(User from) {
        System.out.println("To:");
        Long idTo = in.nextLong();
        User to = srv.getUser(idTo);

        if(to == null) {
            System.out.println("User does not exist!");
            return;
        }

        if(srv.addFriendRequest(new FriendRequest(from, to)) != null)
            System.out.println("Friend request sent!");
        else
            System.out.println("Can not send friend request!");
    }

    private void uiAcceptFriendRequest(User user){
        System.out.println("From:");
        Long idFrom = in.nextLong();
        User userFrom = srv.getUser(idFrom);
        if(userFrom == null) {
            System.out.println("User does not exist!");
            return;
        }
        Friendship friendship = srv.acceptFriendRequest(user, userFrom);
        if(friendship != null)
            System.out.println("The users are now friends!");
        else
            System.out.println("Can not accept!");
    }

    private void uiRejectFriendRequest(User user) {
        System.out.println("From:");
        Long idFrom = in.nextLong();
        User userFrom = srv.getUser(idFrom);
        if (userFrom == null) {
            System.out.println("User does not exist!");
            return;
        }
        if (srv.rejectFriendRequest(user, userFrom))
            System.out.println("Friend request rejected!");
        else
            System.out.println("Nothing to reject!");
    }

    private void uiReplyAll(User user) throws IOException {
        List<Message> inbox = srv.getInbox(user);
        if(inbox.isEmpty())
            System.out.println("No messages to reply!");
        else {
            System.out.println("Your messages are:");
            for(Message m : inbox)
                System.out.println(m.getId()+ " : "+m.getText());
            System.out.println("Choose what(id) message to reply all");
            Long idMessage = in.nextLong();
            System.out.println("And what message you want to reply with");
            String reply = stdin.readLine();
            int ok=0;
            Message t = null;
            for(Message m : inbox)
                if(m.getId().equals(idMessage)) {
                    ok++;
                    t=m;
                }
            if(ok==0)
                System.out.println("Chosen id incorrect!");
            else {
                srv.replyAll(t, user, reply);
                System.out.println("Message sent to all");
            }
        }
    }


    private void uiChronologicalMessages() {
        System.out.println("User1's id:");
        Long id1 = in.nextLong();
        System.out.println("User2's id:");
        Long id2 = in.nextLong();
        List<Message> mesaje =srv.getChats(id1,id2);
        if(mesaje.isEmpty())
            System.out.println("No chats");
        else{
            mesaje.stream().sorted(Comparator.comparing(Message::getDate))
                    .forEach(x->{
                        if(x.getReply()!=null)
                            System.out.println(x.getReply().getDate()+"  Reply la:\n"+x.getReply().getText());
                        System.out.println(x.getDate()+"  "+x.getFrom().getFirstName()+" "+x.getFrom().getLastName()+":\n"+x.getText()+"\n");
                    });
        }
    }
}
