package socialnetwork.repository.database.db;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.sql.DriverManager.getConnection;

public class UserDbRepository implements Repository<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public int size() {
        AtomicInteger n= new AtomicInteger();
        Iterable<User> users = findAll();
        users.forEach(x-> n.getAndIncrement());
        return n.get();
    }

    @Override
    public boolean exists(Long aLong) {
        return findOne(aLong) != null;
    }

    @Override
    public void deleteAll(Iterable<User> list) {
        list.forEach(x->delete(x.getId()));
    }

    @Override
    public void saveAll(Iterable<User> list) {
        list.forEach(this::save);
    }

    @Override
    public User findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id must be not null!");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")){
             statement.setDouble(1, id.doubleValue());
             ResultSet resultSet = statement.executeQuery();
             User user = null;
                while(resultSet.next()){
                Long userId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                user = new User(firstName, lastName);
                user.setId(userId);}
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long userId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName, lastName);
                user.setId(userId);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
    }

    @Override
    public User save(User entity) {
        String sql = "INSERT INTO users (first_name, last_name ) VALUES (?, ?)";
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.executeUpdate();

            try(PreparedStatement ps1 = connection.prepareStatement("SELECT MAX(id) FROM users")){
                ResultSet resultSet = ps1.executeQuery();
                while(resultSet.next())
                    entity.setId(resultSet.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User delete(Long aLong) {
        String sql = "DELETE FROM users WHERE id = ?";
        User u = findOne(aLong);
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1, aLong);
            ps.executeUpdate();
            return u;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User update(User entity) {
        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setDouble(3, entity.getId().doubleValue());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}