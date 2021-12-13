package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private Long id;
    private User from;
    private List<User> to;
    private String text;
    private LocalDateTime date;
    private  Message reply;

    public Message(User from, List<User> to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.date=LocalDateTime.now();
        this.reply=null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
