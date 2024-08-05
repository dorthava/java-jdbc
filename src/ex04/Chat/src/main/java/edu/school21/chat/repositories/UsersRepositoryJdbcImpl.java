package edu.school21.chat.repositories;

import edu.school21.chat.models.Chatroom;
import edu.school21.chat.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class UsersRepositoryJdbcImpl implements UsersRepository {
    DataSource dataSource;

    public UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll(int page, int size) {
        List<User> users = new ArrayList<>();
        String statement = "WITH users AS ( \n" +
                "    SELECT u.id, u.login, u.password \n" +
                "    FROM \"user\" u\n" +
                "),\n" +
                "created_rooms AS (\n" +
                "    SELECT c.owner AS user_id, array_agg(c.id) AS created_rooms\n" +
                "    FROM chatroom c\n" +
                "    GROUP BY c.owner\n" +
                "),\n" +
                "socialized_rooms AS (\n" +
                "    SELECT m.author AS user_id, array_agg(ROW(c.id, c.name, c.owner, u.login, u.password)::text) AS chatrooms_socialized\n" +
                "    FROM message m\n" +
                "    JOIN chatroom c ON m.room = c.id\n" +
                "\tJOIN users u ON u.id = c.id\n" +
                "    GROUP BY m.author\n" +
                ")\n" +
                "SELECT \n" +
                "    users.id AS user_id, \n" +
                "    users.login, \n" +
                "    users.password,\n" +
                "\tCOALESCE(cr.created_rooms, '{}') AS created_rooms, \n" +
                "    COALESCE(sr.chatrooms_socialized, '{}') AS chatrooms_socialized\n" +
                "FROM users\n" +
                "LEFT JOIN created_rooms cr ON users.id = cr.user_id\n" +
                "LEFT JOIN socialized_rooms sr ON users.id = sr.user_id\n" +
                "ORDER BY users.id\n" +
                "LIMIT ? OFFSET ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(statement)) {
            prepareStatement.setLong(1, size);
            prepareStatement.setLong(2, (long) page * size);
            prepareStatement.execute();
            ResultSet resultSet = prepareStatement.getResultSet();
            while (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                String userLogin = resultSet.getString("login");
                String userPassword = resultSet.getString("password");

                Array createdRoomsArray = resultSet.getArray("created_rooms");
                List<Long> createdRooms = new ArrayList<>();
                if (createdRoomsArray != null) {
                    Integer[] createdRoomsIds = (Integer[]) createdRoomsArray.getArray();
                    Long[] longCreatedRoomsIds = new Long[createdRoomsIds.length];
                    for (int i = 0; i != longCreatedRoomsIds.length; ++i) {
                        longCreatedRoomsIds[i] = createdRoomsIds[i].longValue();
                    }
                    createdRooms.addAll(Arrays.asList(longCreatedRoomsIds));
                }

                List<Chatroom> chatroomSocialized = new ArrayList<>();
                String socializedRoomsString = resultSet.getString("chatrooms_socialized");
                if (socializedRoomsString != null && !socializedRoomsString.isEmpty()) {
                    String[] socializedRoomsArray = socializedRoomsString.split("\",\"");
                    for (String roomStr : socializedRoomsArray) {
                        roomStr = roomStr.replaceAll("[\"{}()]", "");
                        String[] attributes = roomStr.split(",");
                        Long id = Long.parseLong(attributes[0]);
                        if (!findEnteredRoom(chatroomSocialized, id)) {
                            Chatroom chatroom = new Chatroom(id, attributes[1],
                                    new User(Long.parseLong(attributes[2]), attributes[3], attributes[4], null, null), null);
                            chatroomSocialized.add(chatroom);
                        }
                    }
                }
                User user = new User(userId, userLogin, userPassword, createdRooms, chatroomSocialized);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public boolean findEnteredRoom(List<Chatroom> chatroomList, Long id) {
        boolean result = false;
        for (int i = 0; i != chatroomList.size() && !result; ++i) {
            if(chatroomList.get(i).getId() == id) {
                result = true;
                break;
            }
        }
        return result;
    }
}
