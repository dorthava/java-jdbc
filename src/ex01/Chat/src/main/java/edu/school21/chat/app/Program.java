package edu.school21.chat.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.chat.models.Message;
import edu.school21.chat.repositories.MessagesRepositoryJdbcImpl;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class Program {
    public static void main(String[] args) {
        System.out.println("Enter a message ID");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            Long id = Long.parseLong(bufferedReader.readLine());
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/chat");
            config.setUsername("postgres");
            config.setPassword("postgres");
            DataSource dataSource = new HikariDataSource(config);
            MessagesRepositoryJdbcImpl messagesRepositoryJdbc = new MessagesRepositoryJdbcImpl(dataSource);
            Optional<Message> optionalMessage = messagesRepositoryJdbc.findById(id);
            if(optionalMessage.isPresent()) System.out.println(optionalMessage.get());
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
}
