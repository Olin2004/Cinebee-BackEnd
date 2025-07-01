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

    Page<Movie> findAllByOrderByLikesDescViewsDesc(Pageable pageable);

    Page<Movie> findAllByOrderByRatingDescLikesDescViewsDesc(Pageable pageable);

}
