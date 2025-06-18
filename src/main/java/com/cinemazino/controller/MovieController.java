package com.cinemazino.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinemazino.dto.response.AllPagedMoviesResponse;
import com.cinemazino.dto.response.SimpleMovieResponse;
import com.cinemazino.dto.response.TrendingMovieResponse;
import com.cinemazino.service.MovieService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/trending")
    public ResponseEntity<List<TrendingMovieResponse>> getTrendingMovies() {
        List<TrendingMovieResponse> movies = movieService.getTrendingMovies(10);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/all-by-likes")
    public ResponseEntity<AllPagedMoviesResponse<TrendingMovieResponse>> getAllMoviesByLikes(@RequestParam(defaultValue = "0") int page) {
        int size = 20;
        AllPagedMoviesResponse<TrendingMovieResponse> movies = movieService.getAllMoviesOrderByLikesPaged(page, size);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/all-by-likes-paged")
    public ResponseEntity<AllPagedMoviesResponse<SimpleMovieResponse>> getAllMoviesByLikesPaged() {
        int size = 20;
        AllPagedMoviesResponse<SimpleMovieResponse> movies = movieService.getAllMoviesSimplePaged(size);
        return ResponseEntity.ok(movies);
    }
}
