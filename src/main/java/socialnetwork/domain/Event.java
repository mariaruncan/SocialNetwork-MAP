package socialnetwork.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event extends Entity<Long>{
    private String name;
    private LocalDate date;
    private List<User> subscribedUsers;

    public Event(String name, LocalDate date){
        this.name = name;
        this.date = date;
        subscribedUsers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<User> getSubscribedUsers() {
        return subscribedUsers;
    }

    public void setSubscribedUsers(List<User> subscribedUsers) {
        this.subscribedUsers = subscribedUsers;
    }
}
