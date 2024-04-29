package ru.netology.model;

import ru.netology.entity.PostEntity;

public record Post(long id, String content) {
  public Post() {
    this(0, "");
  }

  public Post copy() {
    return new Post(this.id, this.content);
  }

  public Post copy(long id) {
    return new Post(id, this.content);
  }

  public Post copy(String content) {
    return new Post(this.id, content);
  }

  public PostEntity toEntity() {
    return new PostEntity(this.id, this.content, false);
  }
}

