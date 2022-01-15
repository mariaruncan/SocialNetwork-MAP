package socialnetwork.domain;

import java.util.Objects;

public class FriendRequest extends Entity<Tuple<User, User>>{
    private final User from;
    private final User to;
    private String status;

    public FriendRequest(User from, User to){
        this.from = from;
        this.to = to;
        this.status = "pending";
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest: {from: " + from.getId() + " " + from.getFirstName() + " " + from.getLastName() + "} " +
                "{to: " + to.getId() + " " + to.getFirstName() + " " + to.getLastName() + "} " +
                "status: " + status;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FriendRequest f = (FriendRequest) o;
        return (Objects.equals(from, f.getFrom())) && (Objects.equals(to, f.getTo()))
                && Objects.equals(status, f.getStatus());
    }

    @Override
    public int hashCode(){
        return Objects.hash(from, to, status);
    }
}
