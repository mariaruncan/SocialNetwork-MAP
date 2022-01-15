package socialnetwork.repository.database.db;

import socialnetwork.domain.Event;
import socialnetwork.domain.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventDbRepository implements Repository<Long, Event>{

    private final String url;
    private final String username;
    private final String password;
    private final Iterable<User> users;

    public EventDbRepository(String url, String username, String password, Repository<Long, User> repo){
        this.url = url;
        this.username = username;
        this.password = password;
        this.users = repo.findAll();

        Iterable<Event> events = this.findAll();
        Iterable<Event> passedEvents = StreamSupport.stream(events.spliterator(), false)
                .filter(e -> e.getDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        this.deleteAll(passedEvents);
    }

    @Override
    public int size() {
        AtomicInteger n = new AtomicInteger();
        Iterable<Event> events = findAll();
        events.forEach(x -> n.getAndIncrement());
        return n.get();
    }

    @Override
    public boolean exists(Long aLong) {
        return findOne(aLong) != null;
    }

    @Override
    public void deleteAll(Iterable<Event> list) {
        list.forEach(x -> delete(x.getId()));
    }

    @Override
    public void saveAll(Iterable<Event> list) {
        list.forEach(this::save);
    }

    @Override
    public Event findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("id must not be null!");
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM events WHERE id = ?");
            PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM events_subscribers WHERE id = ?")) {
            statement.setLong(1, aLong);
            statement1.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            Event event;
            if (resultSet.next()){
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Date date = resultSet.getDate("date");
                LocalDate localDate = date.toLocalDate();

                List<User> subscribedUsers = new ArrayList<>();
                ResultSet resultSet1 = statement1.executeQuery();
                while(resultSet1.next()){
                    Long userId = resultSet1.getLong("user_id");
                    User user = StreamSupport.stream(users.spliterator(), false)
                            .filter(x -> x.getId().equals(userId))
                            .collect(Collectors.toList())
                            .get(0);
                    subscribedUsers.add(user);
                }

                event = new Event(name, localDate);
                event.setId(id);
                event.setSubscribedUsers(subscribedUsers);
                return event;
            }

            return null;
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<Event> findAll() {
        List<Event> events = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM events");
            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM events_subscribers WHERE event_id = ?");
            ResultSet resultSet = ps.executeQuery()){

            while(resultSet.next()){
                long id = resultSet.getLong("id");

                ps1.setLong(1, id);
                String name = resultSet.getString("name");
                Date date = resultSet.getDate("date");
                LocalDate localDate = date.toLocalDate();

                List<User> subscribedUsers = new ArrayList<>();
                ResultSet resultSet1 = ps1.executeQuery();

                while(resultSet1.next()){
                    Long userId = resultSet1.getLong("user_id");
                    User user = StreamSupport.stream(users.spliterator(), false)
                            .filter(x -> x.getId().equals(userId))
                            .collect(Collectors.toList())
                            .get(0);
                    subscribedUsers.add(user);
                }

                Event event = new Event(name, localDate);
                event.setId(id);
                event.setSubscribedUsers(subscribedUsers);
                events.add(event);
            }
            return events;
        }
        catch(SQLException e){
            e.printStackTrace();
            return events;
        }
    }

    @Override
    public Event save(Event entity) {
        String sql = "INSERT INTO events(name, date) VALUES(?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, entity.getName());
            ps.setDate(2, Date.valueOf(entity.getDate()));
            ps.executeUpdate();
            return  entity;
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Event delete(Long aLong) {
        Event event = findOne(aLong);

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM events WHERE id = ?")){

            ps.setLong(1, aLong);
            ps.executeUpdate();
            return event;
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Event update(Event entity) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("UPDATE events SET date = ? WHERE id = ?")){

            ps.setDate(1, Date.valueOf(entity.getDate()));
            ps.setLong(2, entity.getId());
            ps.executeUpdate();
            return entity;
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public void subscribe(Long eventId, Long userId){
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO events_subscribers(event_id, user_id) VALUES (?, ?)")){
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void unsubscribe(Long eventId, Long userId){
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM events_subscribers WHERE event_id = ? AND user_id = ?")){
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
