package com.cinebee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface CommentRepository extends JpaRepository<com.cinebee.entity.Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.movie = :movie")
    long countByMovie(com.cinebee.entity.Movie movie);
}
