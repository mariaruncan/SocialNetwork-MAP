package socialnetwork.domain;

import java.util.List;
import java.util.Objects;

public class Page {
    private String firstName;
    private String lastName;
    private List<User> friends;
    private List<Message> receivedMessages;
    private  List<FriendRequest> requests;

    @Override
    public String toString() {
        return "Page{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                ", receivedMessages=" + receivedMessages +
                ", requests=" + requests +
                '}';
    }

    public Page(String firstName, String lastName, List<User> friends, List<Message> receivedMessages, List<FriendRequest> requests) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = friends;
        this.receivedMessages = receivedMessages;
        this.requests = requests;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public List<FriendRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<FriendRequest> requests) {
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(firstName, page.firstName) && Objects.equals(lastName, page.lastName) && Objects.equals(friends, page.friends) && Objects.equals(receivedMessages, page.receivedMessages) && Objects.equals(requests, page.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, friends, receivedMessages, requests);
    }
}
