package socialnetwork.repository.database.db;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.sql.DriverManager.getConnection;

public class MessageDbRepository implements  Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    Iterable<User> usersList;

    public MessageDbRepository(String url, String username, String password, Repository<Long, User> usersRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.usersList = usersRepo.findAll();
    }


    @Override
    public int size() {
        AtomicInteger n = new AtomicInteger();
        Iterable<Message> users = findAll();
        users.forEach(x -> n.getAndIncrement());
        return n.get();
    }

    @Override
    public boolean exists(Long aLong) {
        return findOne(aLong) != null;
    }

    @Override
    public void deleteAll(Iterable<Message> list) {
        list.forEach(x -> delete(x.getId()));

    }

    @Override
    public void saveAll(Iterable<Message> list) {
        list.forEach(this::save);
    }

    @Override
    public Message findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id must be not null");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE id = ?");
             PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM replyto WHERE id_msg = ?")) {
            statement.setLong(1, id);
            statement1.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Message message = null;
            while (resultSet.next()) {
                Long msgId = resultSet.getLong("id");
                Long senderId = resultSet.getLong("fromm");
                String text = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
                User finalUser = null;
                List<User> receiversList = new ArrayList<>();
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    for (User u : usersList) {
                        if (u.getId().equals(senderId))
                            finalUser = u;
                        if (u.getId() == resultSet1.getLong("touser"))
                            receiversList.add(u);
                    }
                }
                message = new Message(finalUser, receiversList, text);
                message.setId(msgId);
                message.setDate(datetime);
            }
            return message;
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }

}

    @Override
    public Iterable<Message> findAll() {
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message");
             PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM replyto WHERE id_msg = ?");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long msgId = resultSet.getLong("id");
                statement1.setLong(1, msgId);
                Long senderId = resultSet.getLong("fromm");
                String text = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                Long idReply = resultSet.getLong("reply");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
                User finalUser = null;
                List<User> receiversList = new ArrayList<>();
                Message reply = findOne(idReply);

                for (User u : usersList) {
                    if (u.getId().equals(senderId))
                        finalUser = u;
                }
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next())
                    for (User u : usersList) {
                        if (u.getId() == resultSet1.getLong("touser"))
                            receiversList.add(u);
                    }
                Message message= new Message(finalUser, receiversList, text);
                message.setId(msgId);
                message.setDate(datetime);
                message.setReply(reply);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            return messages;
        }
    }

    @Override
    public Iterable<Message> findAllPagination(int t, Long id1, Long id2) {
        ArrayList<Message> messages = new ArrayList<>();

        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM message m INNER JOIN replyto r ON m.id = r.id_msg
                     WHERE (m.fromm = ? AND r.touser = ?) OR (r.touser = ? AND m.fromm = ?)
                     ORDER BY id\s
                     limit ? OFFSET ?""");
             PreparedStatement statement1 = connection.prepareStatement("SELECT touser FROM replyto WHERE id_msg = ?")) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id1);
            statement.setLong(4, id2);
            statement.setInt(5, 5);
            statement.setInt(6, (t-1)*5);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long msgId = resultSet.getLong("id");
                Long senderId = resultSet.getLong("fromm");
                String text = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                Long idReply = resultSet.getLong("reply");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(),time.toLocalTime());
                User finalUser = null;
                List<User> receiversList = new ArrayList<>();
                Message replyMsg = findOne(idReply);
                for (User u: usersList) {
                    if(u.getId().equals(senderId))
                        finalUser = u;
                }
                statement1.setLong(1, msgId);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    for (User u : usersList) {
                        if (u.getId() == resultSet1.getLong("touser"))
                            receiversList.add(u);
                    }
                }
                Message message= new Message(finalUser,receiversList,text);
                message.setId(msgId);
                message.setDate(datetime);
                message.setReply(replyMsg);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            return messages;
        }
    }

    @Override
    public Message save(Message entity) {

        String sql = "INSERT INTO message (fromm, text, date, time, reply) VALUES (?, ?, ?, ?, ?)";
        String sql1 = "INSERT INTO replyto( id_msg, touser) VALUES (?, ?)";
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps1 = connection.prepareStatement(sql1);
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM message")){

            ps.setLong(1, entity.getFrom().getId());
            ps.setString(2, entity.getText());
            ps.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            ps.setTime(4, Time.valueOf(entity.getDate().toLocalTime()));
            if(entity.getReply() != null)
                ps.setLong(5, entity.getReply().getId());
            else
                ps.setNull(5, Types.DOUBLE);
            ps.executeUpdate();

            ResultSet resultSet = statement.executeQuery();
            long maxMsgId = 0L;
            while (resultSet.next()) {
                maxMsgId = resultSet.getLong("max");
            }
            for (int i=0; i < entity.getTo().size(); i++) {
                ps1.setLong(1,maxMsgId);
                ps1.setLong(2,entity.getTo().get(i).getId());
                ps1.executeUpdate();
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Message delete(Long aLong) {
        String sql = "DELETE FROM message WHERE id = ?";
        String sql1 = "DELETE FROM replyto WHERE id = ?";
        Message m = findOne(aLong);
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement ps1 = connection.prepareStatement(sql1)){

            ps.setLong(1, aLong);
            ps1.setLong(1, aLong);
            ps.executeUpdate();
            ps1.executeUpdate();
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Message update(Message entity) {
        String sql = "UPDATE message SET text = ? WHERE id = ?";
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getText());
            ps.setLong(2, entity.getId());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
