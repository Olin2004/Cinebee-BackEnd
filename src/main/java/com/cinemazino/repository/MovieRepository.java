package com.cinemazino.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cinemazino.dto.response.TrendingMovieResponse;
import com.cinemazino.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("""
                SELECT new com.cinemazino.dto.response.TrendingMovieResponse(
                    m.id,
                    m.title,
                    m.othernames,
                    m.rating,
                    m.votes,
                    m.posterUrl,
                    m.likes,
                    COUNT(t.id) as ticketSales,
                    0 as rank
                )
                FROM Movie m
                LEFT JOIN Showtime s ON s.movie.id = m.id
                LEFT JOIN Ticket t ON t.showtime.id = s.id
                GROUP BY m.id, m.title, m.othernames, m.rating, m.votes, m.posterUrl, m.likes
                ORDER BY COUNT(t.id) DESC, m.likes DESC
            """)
    List<TrendingMovieResponse> findTopTrendingMovies(Pageable pageable);

    @Query("""
        SELECT new com.cinemazino.dto.response.TrendingMovieResponse(
            m.id,
            m.title,
            m.othernames,
            m.rating,
            m.votes,
            m.posterUrl,
            m.likes,
            m.views,
            0 as rank
        )
        FROM Movie m
        ORDER BY m.likes DESC
    """)
    List<TrendingMovieResponse> findAllMoviesOrderByLikesDesc(Pageable pageable);
}
