package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.entity.PostEntity;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryStubImpl implements PostRepository {

  private final Map<Long, PostEntity> posts = new ConcurrentHashMap<>();
  private final AtomicLong counter = new AtomicLong(1);

  public List<PostEntity> all() {
    return posts.values().stream().sorted(Comparator.comparing(PostEntity::id)).toList();
  }

  public Optional<PostEntity> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public PostEntity save(Post post) {
    counter.set(Long.max(counter.get(), post.id()));
    if (post.id() == 0L || post.id() == counter.get()) {
      post = post.copy(counter.getAndIncrement());
    }
    posts.put(post.id(), post.toEntity());
    return posts.get(post.id());
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
