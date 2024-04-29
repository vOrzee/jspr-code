package ru.netology.entity;

import ru.netology.model.Post;

import java.util.Optional;

public record PostEntity(long id, String content, boolean removed) {
    public PostEntity() {
        this(0, "", false);
    }

    public Optional<Post> toDto() {
        if (!this.removed()) {
            return Optional.of(new Post(this.id, this.content));
        }
        return Optional.empty();
    }

    public PostEntity copy() {
        return new PostEntity(this.id, this.content, this.removed);
    }

    public PostEntity copy(int id) {
        return new PostEntity(id, this.content, this.removed);
    }

    public PostEntity copy(String content) {
        return new PostEntity(this.id, content, this.removed);
    }

    public PostEntity copy(boolean removed) {
        return new PostEntity(this.id, this.content, removed);
    }

    public PostEntity copy(String content, boolean removed) {
        return new PostEntity(this.id, content, removed);
    }

    public PostEntity copy(int id, boolean removed) {
        return new PostEntity(id, this.content, removed);
    }

    public PostEntity copy(int id, String content) {
        return new PostEntity(id, content, this.removed);
    }

    public PostEntity copy(int id, String content, boolean removed) {
        return new PostEntity(id, content, removed);
    }
}
