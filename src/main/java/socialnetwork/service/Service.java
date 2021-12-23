package socialnetwork.service;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.repository.database.db.Repository;
import socialnetwork.utils.Graph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Math.toIntExact;

/**
 * Service class
 */
public class Service {

    private final Repository<Long, User> usersRepo;
    private final Repository<Tuple<User, User>, Friendship> friendshipsRepo;
    private final Repository<Tuple<User, User>, FriendRequest> friendRequestsRepo;
    private final Repository<Long, Message> messageRepository;

    /**
     * constructor
     * @param usersRepo - Repository<Long, User>
     * @param friendshipsRepo - Repository<Tuple<User, User>, Friendship>
     */

    public Service(Repository<Long, User> usersRepo, Repository<Tuple<User, User>, Friendship> friendshipsRepo,
                Repository<Long, Message> messageRepository, Repository<Tuple<User, User>, FriendRequest> friendRequestsRepository) {
        this.usersRepo = usersRepo;
        this.friendshipsRepo = friendshipsRepo;
        this.messageRepository = messageRepository;
        this.friendRequestsRepo = friendRequestsRepository;
    }

    /**
     * adds an user
     * @param firstName - String
     * @param lastName - String
     * @return the saved user
     */
    public User addUser(String firstName, String lastName) {
        return usersRepo.save(new User(firstName, lastName));
    }

    /**
     * removes an user
     * @param id - User
     * @return the removed user
     */
    public User removeUser(Long id) {
        User user = usersRepo.findOne(id);
        Iterable<User> list = usersRepo.findAll();
        for (User u: list) {
            u.removeFriend(user);
            removeFriendship(u.getId(), user.getId());
            removeFriendship(user.getId(),u.getId());
        }
        return usersRepo.delete(user.getId());
    }

    /**
     * adds a friendship
     * @param u1 - long
     * @param u2 - long
     * @return friendship between users with ids u1 ans u2
     */
    public Friendship addFriendship(long u1, long u2) {
        boolean b = usersRepo.findOne(u1).addFriend(usersRepo.findOne(u2));
        boolean a = usersRepo.findOne(u2).addFriend(usersRepo.findOne(u1));
        if(a && b)
        {
            Friendship f = new Friendship(usersRepo.findOne(u1), usersRepo.findOne(u2));
            return friendshipsRepo.save(f);
        }
        return null;
    }

    /**
     * removes a friendship
     * @param u1 - long
     * @param u2 - long
     * @return the removed friendship
     */
    public Friendship removeFriendship(long u1, long u2) {
        usersRepo.findOne(u1).removeFriend(usersRepo.findOne(u2));
        usersRepo.findOne(u2).removeFriend(usersRepo.findOne(u1));
        return friendshipsRepo.delete(new Tuple<>(usersRepo.findOne(u1), usersRepo.findOne(u2)));
    }

    /**
     * returns all users
     * @return users list
     */
    public Iterable<User> getAllUsers(){
        Iterable<User> users = usersRepo.findAll();
        users.forEach(u -> {
            for (Friendship f: friendshipsRepo.findAll())
                if(f.getUser1().getId() == u.getId())
                    u.addFriend(usersRepo.findOne(f.getUser2().getId()));
                else if(f.getUser2().getId() == u.getId())
                    u.addFriend(usersRepo.findOne(f.getUser1().getId()));
            });
        return users;
    }

    /**
     * returns all friendships
     * @return friendships list
     */
    public Iterable<Friendship> getAllFriendships(){
        return friendshipsRepo.findAll();
    }

    /**
     * returns the number of communities in the social network
     * @return int
     */
    public  int communitiesNumber() {
        int size = 0;
        for (User u: usersRepo.findAll()) {
            size = toIntExact(u.getId());
        }
        Graph g = new Graph(size);
        for (Friendship f : friendshipsRepo.findAll()){
                g.addEdge(toIntExact(f.getUser1().getId()), toIntExact(f.getUser2().getId()));

        }
        g.DFS();
        int nr = g.connectedComponents();
        nr = nr - (size - usersRepo.size());
        return nr;
    }

    /**
     * returns a list of ints representing the most sociable community
     * @return List<Integer>
     */
    public List<Integer> mostSociableCommunity() {
        int size = 0;
        for (User u: usersRepo.findAll()) {
            size = toIntExact(u.getId());
        }
        Graph g = new Graph(size);
        for (Friendship f : friendshipsRepo.findAll()) {
                g.addEdge(toIntExact(f.getUser1().getId()), toIntExact(f.getUser2().getId()));

        }
        g.DFS();
        int max = 1;
        List<Integer> comp= new ArrayList<>();
        ArrayList<ArrayList<Integer> > lists = g.returnComponents();
        for (ArrayList<Integer> list:lists)
            if(list.size() >= max)
            {
                max = list.size();
                comp = list;
            }
        return comp;
    }
    public List<Friendship> reportUserFriends(Long id) {
        if(StreamSupport.stream(getAllUsers().spliterator(), false)
                .collect(Collectors.toList()).stream()
                .filter(y -> y.getId()==id).collect(Collectors.toList()).isEmpty())
        {System.out.println("no user");
            return null;}
        else {
            List<Friendship> rez =StreamSupport.stream(getAllFriendships().spliterator(), false)
                    .collect(Collectors.toList()).stream()
                    .filter(friendship -> friendship.getUser1().getId() == id  || friendship.getUser2().getId() == id)
                    .collect(Collectors.toList());
           return rez;
    }}

    public Tuple<User, List<User>> reportUsersFriendsMonth(Long id, Integer month){
        User user = usersRepo.findOne(id);
        String regex = ".{4}-0{0,1}+" + month.toString() + "-[0-9]{1,2}";
        List<User> list = StreamSupport.stream(friendshipsRepo.findAll().spliterator(), false)
                .filter(f -> f.getUser1().getId() == id || f.getUser2().getId() == id)
                .filter(f -> {
                    System.out.println(f.getDate());
                    return f.getDate().toString().matches(regex);
                })
                .map(f -> {
                    if(f.getUser1().getId() == id)
                        return f.getUser2();
                    else
                        return f.getUser1();
                })
                .collect(Collectors.toList());
        return new Tuple<>(user, list);
    }

    public List<FriendRequest> getUserFriendRequests(Long id){
        return StreamSupport.stream(friendRequestsRepo.findAll().spliterator(), false)
                .filter(fr -> fr.getTo().getId() == id)
                .collect(Collectors.toList());
    }

    public User getUser(Long id){
        User user = usersRepo.findOne(id);
        StreamSupport.stream(getAllFriendships().spliterator(), false)
                .filter(fr -> fr.getUser1().getId() == id || fr.getUser2().getId() == id)
                .map(fr -> {
                    if(fr.getUser1().getId() == id)
                        return fr.getUser2();
                    else
                        return fr.getUser1();
                })
                .forEach(u -> user.addFriend(u));
        return user;
    }

    public FriendRequest addFriendRequest(FriendRequest fr){
        FriendRequest temp = friendRequestsRepo.findOne(new Tuple<>(fr.getFrom(), fr.getTo()));
        if(temp != null)
            if(temp.getStatus().matches("rejected") ||
                    (temp.getStatus().matches("approved") && friendshipsRepo.findOne(new Tuple<>(fr.getFrom(), fr.getTo())) == null)) {
                return friendRequestsRepo.update(fr);
            }
            else
                return null;
        temp = friendRequestsRepo.findOne(new Tuple<>(fr.getTo(), fr.getFrom()));
        if(temp != null)
            if(temp.getStatus().matches("rejected")) {
                return friendRequestsRepo.save(fr);
            }
            else
                return null;
        return friendRequestsRepo.save(fr);
    }

    public Friendship acceptFriendRequest(User to, User from){
        List<FriendRequest> requestList = getUserFriendRequests(to.getId())
                .stream()
                .filter(fr -> fr.getFrom().getId() == from.getId())
                .collect(Collectors.toList());

        if(requestList.isEmpty())
            return null;

        FriendRequest fr = requestList.get(0);
        if(!fr.getStatus().matches("pending"))
            return null;

        fr.setStatus("approved");
        friendRequestsRepo.update(fr);

        return addFriendship(to.getId(), from.getId());
    }

    public boolean rejectFriendRequest(User to, User from){
        List<FriendRequest> requestList = getUserFriendRequests(to.getId())
                .stream()
                .filter(fr -> fr.getFrom().getId() == from.getId())
                .collect(Collectors.toList());

        if(requestList.isEmpty())
            return false;

        FriendRequest fr = requestList.get(0);
        if(!fr.getStatus().matches("pending"))
            return false;

        fr.setStatus("rejected");
        friendRequestsRepo.update(fr);

        return true;
    }

    public List<Message> getChats(Long id1, Long id2) {
        List<Message> list = new ArrayList<Message>();
        for (Message m: messageRepository.findAll()) {
            if(m.getFrom().getId()==id1)
            {
                for (User u: m.getTo()) {
                    if(u.getId()==id2)
                        list.add(m);
                }
            }
            if(m.getFrom().getId()==id2)
            {
                for (User u: m.getTo()) {
                    if(u.getId()==id1)
                        list.add(m);
                }
            }
        }
        return  list;
    }
    public List<Message> getInbox(User user) {
        List<Message> list = new ArrayList<Message>();
        for (Message m: messageRepository.findAll()) {
            for(User to : m.getTo())
                if(to.getId()==user.getId())
                    list.add(m);

        }
        return  list;
    }

    public void replyAll(Message m, User user, String reply) {
        ArrayList<User> toList = new ArrayList<User>();
        toList.add(m.getFrom());
        for(User u : m.getTo())
            if(u.getId()!= user.getId())
                toList.add(u);
        Message message = new Message(user,toList,reply)   ;
        message.setReply(m);
        messageRepository.save(message);
    }

    public Repository<Long, User> getUserRepo() {
        return this.usersRepo;
    }

    public List<FriendRequest> getUserSentFriendRequests(Long id) {
        return StreamSupport.stream(friendRequestsRepo.findAll().spliterator(), false)
                .filter(fr -> fr.getFrom().getId() == id)
                .collect(Collectors.toList());
    }

    public FriendRequest removeFriendRequest(User from, User to) {
        List<FriendRequest> requestList = getUserFriendRequests(to.getId())
                .stream()
                .filter(fr -> fr.getFrom().getId() == from.getId())
                .collect(Collectors.toList());

        if(requestList.isEmpty())
            return null;

        FriendRequest fr = requestList.get(0);
        if(!fr.getStatus().matches("pending"))
            return null;

        fr=friendRequestsRepo.delete(new Tuple<User,User>(from,to));

        return fr;
    }
}
