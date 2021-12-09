package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User class
 * inherits Entity<Long>
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends;

    /**
     * constructor
     * @param firstName - String
     * @param lastName - String
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<User>();
    }

    /**
     * getter for firstName
     * @return firstName - String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * setter for firstName
     * @param firstName - String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * getter for lastName
     * @return lastName - String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * setter for lastName
     * @param lastName - String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * getter for list of friends
     * @return friends - List<User>
     */
    public List<User> getFriends() {
        return friends;
    }

    /***
     * adds a friend in user's friends list
     * @param u - User
     * @return boolean
     */
    public boolean addFriend(User u) {
        if(friends.size() != 0){
            for (User user: friends) {
                if(user == u)
                    return false;
            }
        }
        this.friends.add(u);
        return true;
    }

    /**
     * removes a friend from user's friends list
     * @param u - User
     * @return boolean
     */
    public boolean removeFriend(User u) {
        if(friends != null){
            for (User user: friends) {
                if(user == u) {
                    friends.remove(user);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * turns friends list to string
     * @return string representing friends of user
     */
    private String friendsToString(){
        String s = "";
        for (User u: friends) {
            if(!s.isEmpty())
                s += ", ";
            s = s + u.getFirstName() + " " + u.getLastName();
        }
        return s;
    }

    /**
     * turns a user to string
     * @return string representing an user
     */
    @Override
    public String toString() {
        return "User: " + super.getId() + " " + firstName + " " + lastName + ", friends = {" + friendsToString() + "}";
    }

    /**
     * verify if 2 users are equal
     * @param o - Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    /**
     * haschodare
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}