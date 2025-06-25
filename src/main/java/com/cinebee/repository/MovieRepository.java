package com.cinebee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinebee.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Tìm phim theo tiêu đề (search)
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Lấy phim theo lượt like giảm dần (paging)
    List<Movie> findAllByOrderByLikesDesc(Pageable pageable);

    // Lấy phim theo lượt like và view giảm dần (paging)
    Page<Movie> findAllByOrderByLikesDescViewsDesc(Pageable pageable);
}
