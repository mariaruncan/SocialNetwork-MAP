package socialnetwork.domain;

public class LogInCredentials {
    private final Long id;
    private final String username;
    private final String hashedPassword;

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
