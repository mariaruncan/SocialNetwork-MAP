package socialnetwork.domain;

public class LogInCredentials {
    private Long id;
    private String username;
    private String hashedPassword;

    public LogInCredentials(Long id, String username, String hashedPassword) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
