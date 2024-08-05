package edu.school21.chat.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.chat.models.User;
import edu.school21.chat.repositories.UsersRepositoryJdbcImpl;

import javax.sql.DataSource;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/chat");
            config.setUsername("postgres");
            config.setPassword("postgres");
            DataSource dataSource = new HikariDataSource(config);
            UsersRepositoryJdbcImpl usersRepositoryJdbc = new UsersRepositoryJdbcImpl(dataSource);
            List<User> userList = usersRepositoryJdbc.findAll(2, 2);
            for(User user : userList) {
                System.out.println(user);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
