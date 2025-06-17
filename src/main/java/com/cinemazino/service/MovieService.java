package com.cinemazino.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cinemazino.dto.response.TrendingMovieResponse;
import com.cinemazino.repository.MovieRepository;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public List<TrendingMovieResponse> getTrendingMovies(int limit) {
        List<TrendingMovieResponse> movies = movieRepository.findTopTrendingMovies(PageRequest.of(0, limit));
        IntStream.range(0, movies.size()).forEach(i -> movies.get(i).setRank(i + 1));
        return movies;
    }
}
