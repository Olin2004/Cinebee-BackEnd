package com.cinebee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinebee.dto.response.PageResponse;

import com.cinebee.dto.response.TrendingMovieResponse;
import com.cinebee.service.MovieService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/trending")
    public ResponseEntity<List<TrendingMovieResponse>> getTrendingMovies() {
        List<TrendingMovieResponse> trendingMovies = movieService.getTrendingMovies(10);
        return ResponseEntity.ok(trendingMovies);
    }

    @GetMapping("/all-by-likes")
    public ResponseEntity<PageResponse<TrendingMovieResponse>> getAllMoviesByLikes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        int pageIndex = Math.max(page - 1, 0);
        PageResponse<TrendingMovieResponse> pageResponse = movieService.getTrendingMoviesPageResponse(pageIndex, size);
        return ResponseEntity.ok(pageResponse);
    }


}
