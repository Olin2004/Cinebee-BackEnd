package com.cinebee.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinebee.dto.response.PageResponse;
import com.cinebee.dto.response.TrendingMovieResponse;
import com.cinebee.entity.Movie;
import com.cinebee.mapper.MovieMapper;
import com.cinebee.repository.MovieRepository;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Lấy danh sách phim trending theo lượt like giảm dần, giới hạn số lượng.
     * @param limit Số lượng phim muốn lấy
     * @return Danh sách phim trending đã set rank
     */
    @Transactional(readOnly = true)
    public List<TrendingMovieResponse> getTrendingMovies(int limit) {
        List<Movie> movies = movieRepository.findAllByOrderByLikesDesc(PageRequest.of(0, limit));
        return mapAndRank(movies, 0);
    }

    /**
     * Lấy danh sách phim trending (paging) theo lượt like và view giảm dần.
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số lượng phim mỗi trang
     * @return Danh sách phim trending đã set rank
     */
    @Transactional(readOnly = true)
    public List<TrendingMovieResponse> getTrendingMoviesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findAllByOrderByLikesDescViewsDesc(pageable);
        long offset = (long) page * size;
        return mapAndRank(moviePage.getContent(), offset);
    }

    /**
     * Lấy 1 page phim trending (paging) theo lượt like và view giảm dần, trả về PageResponse.
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số lượng phim mỗi trang
     * @return PageResponse gồm content, tổng số trang, tổng số phần tử
     */
    @Transactional(readOnly = true)
    public PageResponse<TrendingMovieResponse> getTrendingMoviesPageResponse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findAllByOrderByLikesDescViewsDesc(pageable);
        long offset = (long) page * size;
        List<TrendingMovieResponse> movies = mapAndRank(moviePage.getContent(), offset);
        return new PageResponse<>(movies, moviePage.getTotalPages(), moviePage.getTotalElements());
    }

    /**
     * Tìm kiếm phim trending theo tiêu đề (có phân trang).
     * @param title Từ khóa tiêu đề
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số lượng phim mỗi trang
     * @return Danh sách phim trending phù hợp
     */
    @Transactional(readOnly = true)
    public List<TrendingMovieResponse> searchTrendingMoviesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        return moviePage.getContent().stream().map(MovieMapper::mapToTrendingMovieResponse).toList();
    }

    /**
     * Map list Movie sang TrendingMovieResponse và set rank cho từng phim.
     * @param movies Danh sách phim
     * @param offset Offset thứ tự rank (dùng cho paging)
     * @return Danh sách TrendingMovieResponse đã set rank
     */
    private List<TrendingMovieResponse> mapAndRank(List<Movie> movies, long offset) {
        List<TrendingMovieResponse> list = movies.stream()
                .map(MovieMapper::mapToTrendingMovieResponse)
                .toList();
        IntStream.range(0, list.size()).forEach(i -> list.get(i).setRank((int) (offset + i + 1)));
        return list;
    }
}
