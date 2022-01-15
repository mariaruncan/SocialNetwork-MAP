package socialnetwork.repository.database.db;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.sql.DriverManager.getConnection;

public class FriendshipDbRepository implements Repository<Tuple<User, User>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public int size() {
        AtomicInteger n = new AtomicInteger();
        Iterable<Friendship> users = findAll();
        users.forEach(x -> n.getAndIncrement());
        return n.get();
    }

    @Override
    public boolean exists(Tuple<User, User> t) {
        return findOne(t) != null;
    }

    @Override
    public void deleteAll(Iterable<Friendship> list) {
        list.forEach(x -> delete(x.getId()));
    }

    @Override
    public void saveAll(Iterable<Friendship> list) {
        list.forEach(this::save);
    }

    @Override
    public Friendship findOne(Tuple<User, User> t) {
        if (t == null)
            throw new IllegalArgumentException("Tuple of users must not be null!");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM friendships WHERE user1 = ? AND user2 = ?")) {
            statement.setLong(1, t.getLeft().getId());
            statement.setLong(2, t.getRight().getId());
            ResultSet resultSet = statement.executeQuery();
            Friendship friendship = null;
            while (resultSet.next()) {
                long idUser1 = resultSet.getLong("user1");
                long idUser2 = resultSet.getLong("user2");
                Date date = resultSet.getDate("date");
                User user1 = null;
                User user2 = null;
                try (PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?")){
                     statement1.setDouble(1, idUser1);
                     ResultSet resultSet1 = statement1.executeQuery();
                     while (resultSet1.next()) {
                         Long id1 = resultSet1.getLong("id");
                         String firstName = resultSet1.getString("first_name");
                         String lastName = resultSet1.getString("last_name");
                         user1 = new User(firstName, lastName);
                         user1.setId(id1);
                    }
                     statement1.setDouble(1, idUser2);
                     ResultSet resultSet2 = statement1.executeQuery();
                     while (resultSet2.next()) {
                         Long id2 = resultSet2.getLong("id");
                         String firstName = resultSet2.getString("first_name");
                         String lastName = resultSet2.getString("last_name");
                         user2 = new User(firstName, lastName);
                         user2.setId(id2);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
                friendship = new Friendship(user1, user2);
                friendship.setDate(date);
            }
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long idUser1 = resultSet.getLong("user1");
                long idUser2 = resultSet.getLong("user2");
                Date date = resultSet.getDate("date");
                User user1 = null;
                User user2 = null;
                Friendship friendship;
                try (PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                     statement1.setDouble(1, idUser1);
                     ResultSet resultSet1 = statement1.executeQuery();
                     while (resultSet1.next()) {
                         Long id1 = resultSet1.getLong("id");
                         String firstName = resultSet1.getString("first_name");
                         String lastName = resultSet1.getString("last_name");
                         user1 = new User(firstName, lastName);
                         user1.setId(id1);
                    }

                     statement1.setDouble(1, idUser2);
                     ResultSet resultSet2 = statement1.executeQuery();
                     while (resultSet2.next()) {
                         Long id2 = resultSet2.getLong("id");
                         String firstName = resultSet2.getString("first_name");
                         String lastName = resultSet2.getString("last_name");
                         user2 = new User(firstName, lastName);
                         user2.setId(id2);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
                if (idUser1 < idUser2)
                    friendship = new Friendship(user1, user2);
                else
                    friendship = new Friendship(user2, user1);
                friendship.setDate(date);
                friendships.add(friendship);

        }
        return friendships;
    } catch(SQLException e) {
        e.printStackTrace();
        return friendships;
    }
}

    @Override
    public Friendship save(Friendship entity) {
        String sql = "INSERT INTO friendships (user1, user2, date ) VALUES (?, ?, ?)";
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if(entity.getUser1().getId() < entity.getUser2().getId()){
                ps.setLong(1, entity.getUser1().getId());
                ps.setLong(2, entity.getUser2().getId());
            }
            else {
                ps.setLong(2, entity.getUser1().getId());
                ps.setLong(1, entity.getUser2().getId());
            }
            ps.setDate(3, entity.getDate());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Friendship delete(Tuple<User, User> ids) {
        String sql = "DELETE FROM friendships WHERE user1 = ? AND user2 = ?";
        Friendship p = findOne(ids);
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            if(ids.getLeft().getId() < ids.getRight().getId()){
                ps.setLong(1, ids.getLeft().getId());
                ps.setLong(2, ids.getRight().getId());}
            else {
                ps.setLong(2, ids.getLeft().getId());
                ps.setLong(1, ids.getRight().getId());}
            ps.executeUpdate();
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Friendship update(Friendship entity) {
        String sql = "UPDATE friendships SET date = ? WHERE user1 = ? AND user2 = ? ";
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            if(entity.getUser1().getId() < entity.getUser2().getId()){
                ps.setLong(2, entity.getUser1().getId());
                ps.setLong(3, entity.getUser2().getId());}
            else {
                ps.setLong(3, entity.getUser1().getId());
                ps.setLong(2, entity.getUser2().getId());
            }
            ps.setDate(1, entity.getDate());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
