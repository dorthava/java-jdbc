package edu.school21.chat.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.chat.models.Chatroom;
import edu.school21.chat.models.Message;
import edu.school21.chat.models.User;
import edu.school21.chat.repositories.MessagesRepositoryJdbcImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/chat");
            config.setUsername("postgres");
            config.setPassword("postgres");
            DataSource dataSource = new HikariDataSource(config);
            MessagesRepositoryJdbcImpl messagesRepositoryJdbc = new MessagesRepositoryJdbcImpl(dataSource);
            User creator = new User(5L, "user", "user", new ArrayList<>(), new ArrayList<>());
            Chatroom room = new Chatroom(4L, "room", creator, new ArrayList<>());
            Message message = new Message(null, creator, room, "Hello!", LocalDateTime.now());
            messagesRepositoryJdbc.save(message);
            System.out.println(message.getId());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
