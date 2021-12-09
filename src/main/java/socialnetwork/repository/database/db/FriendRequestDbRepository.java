package socialnetwork.repository.database.db;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.DriverManager.getConnection;

public class FriendRequestDbRepository implements Repository<Tuple<User, User>, FriendRequest>{
    private final String url;
    private final String username;
    private final String password;
    private final Validator<FriendRequest> validator;

    public FriendRequestDbRepository(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public int size() {
        try (Connection connection1 = getConnection(url, username, password);
             PreparedStatement statement = connection1.prepareStatement("SELECT COUNT(*) from friend_requests")) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("count");
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean exists(Tuple<User, User> t) {
        return findOne(t) != null;
    }

    @Override
    public void deleteAll(Iterable<FriendRequest> list) {
        list.forEach(x -> delete(x.getId()));
    }

    @Override
    public void saveAll(Iterable<FriendRequest> list) {
        list.forEach(x -> save(x));
    }

    @Override
    public FriendRequest findOne(Tuple<User, User> t) {
        if (t == null)
            throw new IllegalArgumentException("Tuple of users must not be null!");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * from friend_requests WHERE friend_requests.from=? and friend_requests.to=?")) {
            statement.setLong(1, t.getLeft().getId());
            statement.setLong(2, t.getRight().getId());
            ResultSet resultSet = statement.executeQuery();
            FriendRequest friendRequest = null;
            while (resultSet.next()) {
                Long idFrom = resultSet.getLong("from");
                Long idTo = resultSet.getLong("to");
                String status = resultSet.getString("status");
                User from = findUser(idFrom);
                User to = findUser(idTo);
                friendRequest = new FriendRequest(from, to);
                friendRequest.setStatus(status);
            }
            return friendRequest;
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        ArrayList<FriendRequest> requestsList = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friend_requests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long idFrom = resultSet.getLong("from");
                Long idTo = resultSet.getLong("to");
                String status = resultSet.getString("status");
                User userFrom = findUser(idFrom);
                User userTo = findUser(idTo);
                FriendRequest friendRequest = new FriendRequest(userFrom, userTo);
                friendRequest.setStatus(status);
                requestsList.add(friendRequest);

            }
            return requestsList;
        } catch(
                SQLException e)

        {
            e.printStackTrace();
            return requestsList;
        }
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        validator.validate(entity);
        String sql = "insert into friend_requests values (?, ?, ?)";
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getFrom().getId());
            ps.setLong(2, entity.getTo().getId());
            ps.setString(3, entity.getStatus());
            ps.executeUpdate();

            return entity;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public FriendRequest delete(Tuple<User, User> t) {
        String sql = "delete from friend_requests where friend_requests.from = ? and friend_requests.to =?";
        FriendRequest fr = findOne(t);
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1, t.getLeft().getId());
            ps.setLong(2, t.getRight().getId());
            ps.executeUpdate();
            return fr;
        } catch (SQLException throwables) {
            return null;
        }
    }

    @Override
    public FriendRequest update(FriendRequest entity) {
        validator.validate(entity);
        String sql = "update friend_requests set status = ? where friend_requests.from = ? and friend_requests.to = ? ";
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getStatus());
            ps.setLong(2, entity.getFrom().getId());
            ps.setLong(3, entity.getTo().getId());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e){
            return null;
        }
    }

    private User findUser(Long aLong){
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE id=?")) {
            statement.setDouble(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName, lastName);
                user.setId(id);
                return user;
            }
            return null;
        } catch (SQLException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
