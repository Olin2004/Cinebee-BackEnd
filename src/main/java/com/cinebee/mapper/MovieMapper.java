package com.cinebee.mapper;

import com.cinebee.entity.Movie;
import com.cinebee.dto.response.TrendingMovieResponse;
import com.cinebee.dto.response.SimpleMovieResponse;

/**
 * Utility class for mapping Movie entity to response DTOs.
 */
public class MovieMapper {
    /**
     * Maps a Movie entity to a TrendingMovieResponse DTO.
     * @param movie the Movie entity
     * @return TrendingMovieResponse DTO
     */
    public static TrendingMovieResponse mapToTrendingMovieResponse(Movie movie) {
        if (movie == null) return null;
        return new TrendingMovieResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getOthernames(),
            movie.getRating(),
            movie.getVotes(),
            movie.getPosterUrl(), // img in DTO is actually posterUrl
            movie.getLikes(),
            movie.getViews() != null ? movie.getViews().longValue() : 0L, // ticketSales
            0 // rank, should be set elsewhere if needed
        );
    }

    /**
     * Maps a Movie entity to a SimpleMovieResponse DTO.
     * @param movie the Movie entity
     * @return SimpleMovieResponse DTO
     */
    public static SimpleMovieResponse mapToSimpleMovieResponse(Movie movie) {
        if (movie == null) return null;
        return new SimpleMovieResponse(
            movie.getId(),
            movie.getTitle(),
            movie.getLikes(),
            movie.getViews(),
            movie.getPosterUrl() // img in DTO is actually posterUrl
        );
    }
}
