package ru.netology.service;

import org.springframework.stereotype.Service;
import ru.netology.entity.PostEntity;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.PostRepositoryStubImpl;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
public class PostService {

    private final PostRepositoryStubImpl repository;

    public PostService(PostRepositoryStubImpl repository) {
        this.repository = repository;
    }

    public List<Post> all() {
        return repository.all().stream()
                .map(PostEntity::toDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Post getById(long id) {
        return repository.getById(id)
                .orElseThrow(NotFoundException::new)
                .toDto()
                .orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        return repository.save(post)
                .toDto()
                .orElseThrow(NotFoundException::new);//.orElseGet(() -> post.copy(-1));
    }

    public void removeById(long id) {
        repository.removeById(id);
    }
}

