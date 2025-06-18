package com.cinemazino.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cinemazino.dto.response.AllPagedMoviesResponse;
import com.cinemazino.dto.response.TrendingMovieResponse;
import com.cinemazino.dto.response.SimpleMovieResponse;
import com.cinemazino.entity.Movie;
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
            .map(m -> new TrendingMovieResponse(
                m.getId(),
                m.getTitle(),
                m.getOthernames(),
                m.getRating(),
                m.getVotes(),
                m.getPosterUrl(),
                m.getLikes(),
                m.getViews() != null ? m.getViews().longValue() : 0L,
                0 // rank sẽ set sau nếu cần
            ))
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
                TrendingMovieResponse res = new TrendingMovieResponse(
                    m.getId(), m.getTitle(), m.getOthernames(), m.getRating(), m.getVotes(), m.getPosterUrl(), m.getLikes(), m.getViews() != null ? m.getViews().longValue() : 0L, j + 1
                );
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
                SimpleMovieResponse res = new SimpleMovieResponse(
                    m.getId(), m.getTitle(), m.getLikes(), m.getViews(), m.getPosterUrl()
                );
                page.add(res);
            }
            pages.add(page);
        }
        return new AllPagedMoviesResponse<>(pages, totalPages, totalElements);
    }
}
