package com.cinebee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cinebee.entity.Comment;
import com.cinebee.entity.Movie;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // JPA method naming: count by movie entity
    long countByMovie(Movie movie);
}
