package com.cinebee.service;

import com.cinebee.dto.request.MovieRequest;
import com.cinebee.dto.response.MovieResponse;
import com.cinebee.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieService {
    List<MovieResponse> getTrendingMovies(int limit);

    List<MovieResponse> searchTrendingMoviesByTitle(String title, int page, int size);

    MovieResponse addMovie(MovieRequest req, MultipartFile posterImageFile);

    MovieResponse updateMovie(Long movieId, MovieRequest req, MultipartFile posterImageFile);

    void deleteMovie(Long id);

    Page<Movie> getAllMoviesPaged(int page, int size);
}