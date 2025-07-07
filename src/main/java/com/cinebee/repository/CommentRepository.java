package com.cinebee.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cinebee.entity.Comment;
import com.cinebee.entity.Movie;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByMovie(Movie movie);

    @EntityGraph(attributePaths = {"movie", "user"})
    List<Comment> findByMovie(Movie movie);
}
