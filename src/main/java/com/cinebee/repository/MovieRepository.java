package com.cinebee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cinebee.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);


    List<Movie> findAllByOrderByLikesDesc(Pageable pageable);

    Page<Movie> findAllByOrderByLikesDescViewsDesc(Pageable pageable);

    @Query("SELECT m FROM Movie m ORDER BY (m.rating + m.likes + m.views + m.views) DESC")
    List<Movie> findTrendingMovies(Pageable pageable);

}
