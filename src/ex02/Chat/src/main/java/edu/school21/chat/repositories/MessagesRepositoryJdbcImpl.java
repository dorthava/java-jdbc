package edu.school21.chat.repositories;

import edu.school21.chat.models.Message;
import javax.sql.DataSource;
import java.sql.*;

public class MessagesRepositoryJdbcImpl implements MessagesRepository {
    private final DataSource dataSource;

    public MessagesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Message message) {
        if(message.getChatroom().getId() == null || message.getUser().getId() == null) {
            throw new NotSavedSubEntityException("");
        }

        String query = "INSERT INTO Message (author, room, text, datetime) VALUES (?, ?, ?, ?) RETURNING id";
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
            throw new RuntimeException();
        }
    }
}
