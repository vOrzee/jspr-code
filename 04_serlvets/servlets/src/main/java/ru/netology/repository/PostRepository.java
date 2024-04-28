package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stub
public class PostRepository {

  private final Map<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong counter = new AtomicLong(1);

  public List<Post> all() {
    return posts.values().stream().sorted(Comparator.comparing(Post::getId)).toList();
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    counter.set(Long.max(counter.get(), post.getId()));
    if (post.getId() == 0L || post.getId() == counter.get()) {
      post.setId(counter.getAndIncrement());
    }
    return posts.put(post.getId(), post);
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
