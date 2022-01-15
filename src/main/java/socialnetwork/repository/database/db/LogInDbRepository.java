package socialnetwork.repository.database.db;

import socialnetwork.domain.LogInCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class LogInDbRepository {
    private final String url;
    private final String username;
    private final String password;

    public LogInDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public LogInCredentials findOne(String name){
        if (name == null)
            throw new IllegalArgumentException("Username must be not null!");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM logins WHERE username = ?")){
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            LogInCredentials credentials = null;
            while(resultSet.next()){
                Long id = resultSet.getLong("user_id");
                String usName = resultSet.getString("username");
                String hashedPassword = resultSet.getString("hashed_password");
                credentials = new LogInCredentials(id, usName, hashedPassword);
                return credentials;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LogInCredentials save(LogInCredentials entity){
        String sql = "INSERT INTO logins(user_id, username, hashed_password) VALUES (?, ?, ?)";
        try(Connection connection = getConnection(url, username,password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getHashedPassword());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
