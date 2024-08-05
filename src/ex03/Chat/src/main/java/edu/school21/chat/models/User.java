package edu.school21.chat.models;

import java.util.List;
import java.util.Objects;

public class User {
    private final Long id;
    private final String login;
    private final String password;
    private final List<Long> createdRooms;
    private final List<Chatroom> rooms;

    public User(Long id, String login, String password, List<Long> createdRooms, List<Chatroom> rooms) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.createdRooms = createdRooms;
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", createdRooms='" + createdRooms + '\'' +
                ", rooms='" + rooms + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(login, user.login) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, createdRooms, rooms);
    }

    public Long getId() {
        return id;
    }
}
