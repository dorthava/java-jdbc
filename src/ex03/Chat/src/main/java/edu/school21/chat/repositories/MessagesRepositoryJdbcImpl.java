package edu.school21.chat.repositories;

import edu.school21.chat.models.Chatroom;
import edu.school21.chat.models.Message;
import edu.school21.chat.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class MessagesRepositoryJdbcImpl implements MessagesRepository {
    private final DataSource dataSource;

    public MessagesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Message message) {
        if(message.getChatroom().getId() == null || message.getUser().getId() == null) {
            throw new NotSavedSubEntityException("");
        }

        String query = "INSERT INTO Message (id, author, room, text, datetime) VALUES (?, ?, ?, ?) RETURNING id";
        try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, message.getUser().getId());
            preparedStatement.setLong(2, message.getChatroom().getId());
            preparedStatement.setString(3, message.getText());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(message.getDateTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                message.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findById(Long id) {
        String words = "SELECT message.id, \"user\".id AS user_id, \"user\".login, \"user\".password, chatroom.id AS chatroom_id, chatroom.name, message.text, message.datetime\n" +
                "FROM message\n" +
                "JOIN \"user\" ON \"user\".id = message.author\n" +
                "JOIN chatroom ON chatroom.id = message.room\n" +
                "WHERE message.id = ?";
        Message message = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(words)) {
            statement.setInt(1, id.intValue());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long messageId = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                Long roomId = resultSet.getLong("chatroom_id");
                String chatroomName = resultSet.getString("name");
                String text = resultSet.getString("text");
                Timestamp timestamp = resultSet.getTimestamp("datetime");
                LocalDateTime localDateTime = timestamp == null ? null : timestamp.toLocalDateTime();
                message = new Message(messageId, new User(userId, login, password, null, null),
                        new Chatroom(roomId, chatroomName, null, null), text, localDateTime);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(message);
    }

    @Override
    public void update(Message message) {
        if(message.getUser() == null || message.getChatroom() == null) {
            throw new NotSavedSubEntityException("Некорректное поле User или Room!");
        }
        String query = "UPDATE message SET author = ?, room = ?, text = ?, datetime = ? WHERE id = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, message.getUser().getId());
            preparedStatement.setLong(2, message.getChatroom().getId());
            String text = message.getText();
            if(text != null) {
                preparedStatement.setString(3, text);
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }

            LocalDateTime localDateTime = message.getDateTime();
            if(localDateTime != null) {
                preparedStatement.setTimestamp(4, Timestamp.valueOf(localDateTime));
            } else {
                preparedStatement.setNull(4, Types.TIMESTAMP);
            }

            preparedStatement.setLong(5, message.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
