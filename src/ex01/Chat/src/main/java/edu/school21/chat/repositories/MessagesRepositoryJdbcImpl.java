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
                LocalDateTime localDateTime = resultSet.getTimestamp("datetime").toLocalDateTime();
                message = new Message(messageId, new User(userId, login, password, null, null),
                        new Chatroom(roomId, chatroomName, null, null), text, localDateTime);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(message);
    }
}
