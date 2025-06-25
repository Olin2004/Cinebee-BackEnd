package com.cinebee.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cinebee.dto.response.AllPagedMoviesResponse;
import com.cinebee.dto.response.TrendingMovieResponse;
import com.cinebee.dto.response.SimpleMovieResponse;
import com.cinebee.entity.Movie;
import com.cinebee.mapper.MovieMapper;
import com.cinebee.repository.MovieRepository;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<TrendingMovieResponse> getTrendingMovies(int limit) {
        List<TrendingMovieResponse> movies = movieRepository.findTopTrendingMovies(PageRequest.of(0, limit));
        IntStream.range(0, movies.size()).forEach(i -> movies.get(i).setRank(i + 1));
        return movies;
    }

    public List<TrendingMovieResponse> getAllMoviesOrderByLikes(int page, int size) {
        List<TrendingMovieResponse> movies = movieRepository.findAllMoviesOrderByLikesDesc(PageRequest.of(page, size));
        IntStream.range(0, movies.size()).forEach(i -> movies.get(i).setRank(i + 1 + page * size));
        return movies;
    }

    public AllPagedMoviesResponse<TrendingMovieResponse> getAllMoviesOrderByLikesPaged(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findAll(pageable);
        List<Movie> movieEntities = moviePage.getContent();
        List<TrendingMovieResponse> movies = movieEntities.stream()
            .sorted((a, b) -> b.getLikes().compareTo(a.getLikes()))
            .map(MovieMapper::mapToTrendingMovieResponse)
            .toList();
        return new AllPagedMoviesResponse<TrendingMovieResponse>(List.of(movies), moviePage.getTotalPages(), moviePage.getTotalElements());
    }

    public AllPagedMoviesResponse<TrendingMovieResponse> getAllMoviesPaged(int size) {
        List<Movie> allMovies = movieRepository.findAll();
        allMovies.sort((a, b) -> b.getLikes().compareTo(a.getLikes()));
        int totalElements = allMovies.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        List<List<TrendingMovieResponse>> pages = new java.util.ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            int from = i * size;
            int to = Math.min(from + size, totalElements);
            List<TrendingMovieResponse> page = new java.util.ArrayList<>();
            for (int j = from; j < to; j++) {
                Movie m = allMovies.get(j);
                TrendingMovieResponse res = MovieMapper.mapToTrendingMovieResponse(m);
                page.add(res);
            }
            pages.add(page);
        }
        return new AllPagedMoviesResponse<>(pages, totalPages, totalElements);
    }

    public AllPagedMoviesResponse<SimpleMovieResponse> getAllMoviesSimplePaged(int size) {
        List<Movie> allMovies = movieRepository.findAll();
        allMovies.sort((a, b) -> b.getLikes().compareTo(a.getLikes()));
        int totalElements = allMovies.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        List<List<SimpleMovieResponse>> pages = new java.util.ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            int from = i * size;
            int to = Math.min(from + size, totalElements);
            List<SimpleMovieResponse> page = new java.util.ArrayList<>();
            for (int j = from; j < to; j++) {
                Movie m = allMovies.get(j);
                SimpleMovieResponse res = MovieMapper.mapToSimpleMovieResponse(m);
                page.add(res);
            }
            pages.add(page);
        }
        return new AllPagedMoviesResponse<>(pages, totalPages, totalElements);
    }
}
