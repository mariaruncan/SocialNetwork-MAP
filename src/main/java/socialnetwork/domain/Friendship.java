package socialnetwork.domain;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

/***
 * class for friendship between 2 users
 * inherits from Entity<Tuple<User, User>>
 */
public class Friendship extends Entity<Tuple<User,User>> {

    private User user1;
    private User user2;
    Date date;

    /**
     * getter for user1
     * @return user1
     */
    public User getUser1() {
        return user1;
    }

    /**
     * getter for user2
     * @return user2
     */
    public User getUser2() {
        return user2;
    }

    /**
     * getter for date
     * @return the date when the friendship was created
     */
    public Date getDate() {
        return date;
    }

    /**
     * setter for user1
     * @param user1
     */
    public void setUser1(User user1) {
        this.user1 = user1;
    }

    /**
     * setter for user2
     * @param user2
     */
    public void setUser2(User user2) {
        this.user2 = user2;
    }

    /**
     * constructor with params
     * @param u1
     * @param u2
     */
    public Friendship(User u1, User u2) {
        this.user1 = u1;
        this.user2 = u2;
        if(u1.getId() < u2.getId())
            setId(new Tuple<>(u1,u2));
        else
            setId(new Tuple<>(u2,u1));
        date = Date.valueOf(LocalDate.now());
    }

    /**
     * setter for date
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * toString method
     * @return string representing a friendship
     */
    @Override
    public String toString() {
        return "Friendship: {" + user1.getId() + " " + user1.getFirstName() + " " + user1.getLastName() + "} " +
                           "{" + user2.getId() + " " + user2.getFirstName() + " " + user2.getLastName() + "} " + date;
    }

    /**
     * verify if 2 friendships are equal
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship friendship = (Friendship) o;
        return (Objects.equals(user1, friendship.getUser1()) && Objects.equals(user2, friendship.getUser2())) ||
                (Objects.equals(user2, friendship.getUser1()) && Objects.equals(user1, friendship.getUser2()));
    }

    /**
     * hashCode of a friendship
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, user1, user2);
    }

}
