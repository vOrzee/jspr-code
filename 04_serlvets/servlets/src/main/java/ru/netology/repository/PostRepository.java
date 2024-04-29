package ru.netology.repository;

import ru.netology.entity.PostEntity;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<PostEntity> all();

    Optional<PostEntity> getById(long id);

    PostEntity save(Post post);

    void removeById(long id);
}
