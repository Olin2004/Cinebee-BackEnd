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
                movie.getPosterUrl(), // img
                0, // rank
                movie.getDuration(),
                movie.getGenre(),
                movie.getActors(),
                movie.getDirector(),
                movie.getCountry(),
                movie.getReleaseDate() != null ? java.sql.Date.valueOf(movie.getReleaseDate()) : null
        );
    }


    public static Movie mapAddMovieRequestToEntity(MovieRequest req) {
        if (req == null) return null;
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setOthernames(req.getOthernames());
        movie.setBasePrice(req.getBasePrice());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setPosterUrl(req.getPosterUrl());
        movie.setDescription(req.getDescription());
        movie.setActors(req.getActors());
        movie.setDirector(req.getDirector());
        movie.setCountry(req.getCountry());
        if (req.getReleaseDate() != null) {
            movie.setReleaseDate(req.getReleaseDate().toLocalDate());
        }
        return movie;
    }

    public static void mapUpdateMovieRequestToEntity(MovieRequest req, Movie movie) {
        if (req == null || movie == null) return;
        movie.setTitle(req.getTitle());
        movie.setOthernames(req.getOthernames());
        movie.setBasePrice(req.getBasePrice());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setDescription(req.getDescription());
        movie.setActors(req.getActors());
        movie.setDirector(req.getDirector());
        movie.setCountry(req.getCountry());
        if (req.getReleaseDate() != null) {
            movie.setReleaseDate(req.getReleaseDate().toLocalDate());
        } else {
            movie.setReleaseDate(null);
        }
    }

}
