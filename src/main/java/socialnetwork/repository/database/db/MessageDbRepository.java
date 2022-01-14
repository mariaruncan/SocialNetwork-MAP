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
    private String url;
    private String username;
    private String password;
    Iterable<User> utilizatori;

    public MessageDbRepository(String url, String username, String password, Repository<Long, User> repo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.utilizatori = repo.findAll();
    }


    @Override
    public int size() {
        AtomicInteger n = new AtomicInteger();
        Iterable<Message> users = findAll();
        users.forEach(x -> {
            n.getAndIncrement();
        });
        return n.get();
    }

    @Override
    public boolean exists(Long aLong) {
        if (findOne(aLong) != null)
            return true;
        else return false;
    }

    @Override
    public void deleteAll(Iterable<Message> list) {
        list.forEach(x -> delete(x.getId()));

    }

    @Override
    public void saveAll(Iterable<Message> list) {
        list.forEach(x -> save(x));
    }

    @Override
    public Message findOne(Long idd) {
        if (idd == null)
            throw new IllegalArgumentException("id must be not null");
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from message WHERE id=?");
             PreparedStatement statement1 = connection.prepareStatement("SELECT * from replyto WHERE id_msg=?")) {
            statement.setLong(1, idd);
            statement1.setLong(1, idd);
            ResultSet resultSet = statement.executeQuery();
            Message message = null;
            while (resultSet.next()) {
                Long Id = resultSet.getLong("id");
                Long utilizator = resultSet.getLong("fromm");
                String msg = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
                User utilizatorfinal = null;
                List<User> destinatoriList = new ArrayList<>();
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                for (User u : utilizatori) {
                    if (u.getId() == utilizator)
                        utilizatorfinal = u;
                    if (u.getId() == resultSet1.getLong("touser"))
                            destinatoriList.add(u);
                }
            }
            message = new Message(utilizatorfinal, destinatoriList, msg);
            message.setId(Id);
            message.setDate(datetime);
        }
        return message;
    } catch(SQLException e){
        return null;
    }

}

    @Override
    public Iterable<Message> findAll() {
        ArrayList<Message> mesaje = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from message");
             PreparedStatement statement1 = connection.prepareStatement("SELECT * from replyto WHERE id_msg=?");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long Id = resultSet.getLong("id");
                statement1.setLong(1, Id);
                Long utilizator = resultSet.getLong("fromm");
                String msg = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                Long IdReply = resultSet.getLong("reply");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(),time.toLocalTime());
                User utilizatorfinal = null;
                List<User> destinatoriList = new ArrayList<>();
                Message reply = findOne(IdReply);
                for (User u:utilizatori) {
                    if(u.getId()==utilizator)
                        utilizatorfinal=u;
                }
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()){
                    for (User u : utilizatori) {
                        if (u.getId() == resultSet1.getLong("touser"))
                            destinatoriList.add(u);
                    }
                }
                Message message= new Message(utilizatorfinal,destinatoriList,msg);
                message.setId(Id);
                message.setDate(datetime);
                message.setReply(reply);
                mesaje.add(message);
            }
            return mesaje;
        } catch (SQLException e) {
            return mesaje;
        }
    }

    @Override
    public Iterable<Message> findAllPagination(int t,Long id1,Long id2) {
        ArrayList<Message> mesaje = new ArrayList<>();
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from message m inner join replyto r on m.id=r.id_msg\n" +
                     "where (m.fromm =? and r.touser =?) or (r.touser=? and m.fromm=?)\n" +
                     "order by id \n" +
                     "limit ? offset ?");
             PreparedStatement statement1 = connection.prepareStatement("select touser from replyto where id_msg=?")) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id1);
            statement.setLong(4, id2);
            statement.setInt(5, 5);
            statement.setInt(6, (t-1)*5);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long Id = resultSet.getLong("id");
                Long utilizator = resultSet.getLong("fromm");
                String msg = resultSet.getString("text");
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                Long IdReply = resultSet.getLong("reply");
                LocalDateTime datetime = LocalDateTime.of(date.toLocalDate(),time.toLocalTime());
                User utilizatorfinal = null;
                List<User> destinatoriList = new ArrayList<>();
                Message reply = findOne(IdReply);
                for (User u:utilizatori) {
                    if(u.getId()==utilizator)
                        utilizatorfinal=u;
                }
                statement1.setLong(1, Id);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                    for (User u : utilizatori) {
                        if (u.getId() == resultSet1.getLong("touser"))
                            destinatoriList.add(u);
                    }
                }
                Message message= new Message(utilizatorfinal,destinatoriList,msg);
                message.setId(Id);
                message.setDate(datetime);
                message.setReply(reply);
                mesaje.add(message);
            }
            return mesaje;
        } catch (SQLException e) {
            return mesaje;
        }
    }

    @Override
    public Message save(Message entity) {

        String sql = "insert into message (fromm,text,date,time,reply) values (?,?,?,?,?)";
        String sql1 = "insert into replyto( id_msg, touser) values(?,?)";
        try (Connection connection = getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps1 = connection.prepareStatement(sql1);
             PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) from message")){

            ps.setLong(1, entity.getFrom().getId());
            ps.setString(2,entity.getText());
            ps.setDate(3,Date.valueOf(entity.getDate().toLocalDate()));
            ps.setTime(4,Time.valueOf(entity.getDate().toLocalTime()));
            if(entity.getReply()!=null)
                ps.setLong(5,entity.getReply().getId());
            else
                ps.setNull(5,Types.DOUBLE);
            ps.executeUpdate();

            ResultSet resultSet = statement.executeQuery();
            Long Id=null;
            while (resultSet.next()) {
                Id = resultSet.getLong("max");
            }
            for (int i=0;i<entity.getTo().size();i++) {
                ps1.setLong(1,Id);
                ps1.setLong(2,entity.getTo().get(i).getId());
                ps1.executeUpdate();}
            return entity;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Message delete(Long aLong) {
        String sql = "delete from message where id = ?";
        String sql1 = "delete from replyto where id = ?";
        Message m = findOne(aLong);
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement ps1 = connection.prepareStatement(sql1)){

            ps.setLong(1, aLong);
            ps1.setLong(1, aLong);
            ps.executeUpdate();
            ps1.executeUpdate();
            return m;
        } catch (SQLException throwables) {
            return null;
        }

    }

    @Override
    public Message update(Message entity) {
        String sql = "update message set text = ? where id = ?";
        try(Connection connection = getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getText());
            ps.setLong(2, entity.getId());
            ps.executeUpdate();
            return entity;
        } catch (SQLException e){
            return null;
        }
    }
}
