package socialnetwork.domain;

import java.time.LocalDate;

public class EventDTO {
    private Long id;
    private String name;
    private LocalDate date;
    private Boolean subscribed;

    public EventDTO(Long id, String name, LocalDate date, Boolean subscribed){
        this.id = id;
        this.name = name;
        this.date = date;
        this.subscribed = subscribed;
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

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
