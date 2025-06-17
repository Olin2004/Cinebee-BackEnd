package com.cinemazino.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cinemazino.entity.Comment;
import com.cinemazino.entity.Movie;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.movie = :movie")
    long countByMovie(Movie movie);
}
