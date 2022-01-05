package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDTO {
    private Long id;
    private String from;
    private String to;
    private String text;
    private LocalDateTime date;
    private String reply;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public MessageDTO(Message m){
        this.id = m.getId();
        this.from = m.getFrom().getFirstName() + " " + m.getFrom().getLastName();
        List<String> toStringList = m.getTo().stream()
                .map(x -> x.getFirstName() + " " + x.getLastName() + "-")
                .collect(Collectors.toList());
        this.to = "";
        for(String s : toStringList)
            this.to += s;

        this.text = m.getText();
        this.date = m.getDate();
        if(m.getReply() == null)
            this.reply = "no";
        else
            this.reply = m.getReply().getId().toString();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
