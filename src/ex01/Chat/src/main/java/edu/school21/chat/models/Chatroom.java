package edu.school21.chat.models;

import java.util.List;
import java.util.Objects;

public class Chatroom {
    private final Long id;
    private final String name;
    private final Long owned;
    private final List<Message> messages;

    public Chatroom(Long id, String name, Long owned, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.owned = owned;
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owned=" + owned +
                ", messages=" + messages +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owned);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chatroom chatroom = (Chatroom) o;
        return Objects.equals(id, chatroom.id) && Objects.equals(name, chatroom.name) && Objects.equals(owned, chatroom.owned);
    }
}
