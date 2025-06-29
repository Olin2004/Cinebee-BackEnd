package com.cinebee.mapper;

import com.cinebee.dto.request.MovieRequest;
import com.cinebee.dto.response.MovieResponse;
import com.cinebee.entity.Movie;


/**
 * Utility class for mapping Movie entity to response DTOs.
 */
public class MovieMapper {
    /**
     * Maps a Movie entity to a MovieResponse DTO.
     * @param movie the Movie entity
     * @return MovieResponse DTO
     */
    public static MovieResponse mapToTrendingMovieResponse(Movie movie) {
        if (movie == null) return null;
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getOthernames(),
                movie.getRating(),
                movie.getVotes(),
                movie.getPosterUrl(), // img in DTO is actually posterUrl
                movie.getLikes(),
                // Bỏ ticketSales
                0,
                movie.getViews() // truyền views vào DTO
        );
    }
    public static  MovieResponse mapToHightRate(Movie movie){
        if (movie == null) return null;
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getOthernames(),
                null, // rating
                null, // votes
                movie.getPosterUrl(), // img in DTO is actually posterUrl
                movie.getLikes(),
                // Bỏ ticketSales
                0,
                movie.getViews() // truyền views vào DTO
        );
    };


    public static Movie mapAddMovieRequestToEntity(MovieRequest req) {
        if (req == null) return null;
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setOthernames(req.getOthernames());
        movie.setBasePrice(req.getBasePrice());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setPosterUrl(req.getPosterUrl());
        return movie;
    }
}
