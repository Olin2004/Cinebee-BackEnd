package com.cinebee.service;

import java.util.List;
import java.util.Map;

import com.cinebee.dto.request.MovieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cinebee.dto.response.PageResponse;
import com.cinebee.dto.response.MovieResponse;
import com.cinebee.entity.Movie;
import com.cinebee.mapper.MovieMapper;
import com.cinebee.repository.MovieRepository;



@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Lấy danh sách phim trending theo lượt like giảm dần, giới hạn số lượng.
     * @param limit Số lượng phim muốn lấy
     * @return Danh sách phim trending đã set rank
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getTrendingMovies(int limit) {
        List<Movie> movies = movieRepository.findAll(); // lấy tất cả phim để sort custom
        // Sắp xếp theo (rating + likes + views) giảm dần
        List<Movie> sorted = movies.stream()
                .sorted((a, b) -> Double.compare(
                        (b.getRating() + b.getLikes() + (b.getViews() != null ? b.getViews() : 0)),
                        (a.getRating() + a.getLikes() + (a.getViews() != null ? a.getViews() : 0))
                ))
                .limit(limit)
                .toList();
        return java.util.stream.IntStream.range(0, sorted.size())
                .mapToObj(i -> {
                    MovieResponse res = MovieMapper.mapToTrendingMovieResponse(sorted.get(i));
                    res.setRank(i + 1);
                    return res;
                })
                .toList();
    }



    /**ss

     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số lượng phim mỗi trang
     * @return PageResponse gồm content, tổng số trang, tổng số phần tử
     */
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> getTrendingMoviesPageResponse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findAllByOrderByLikesDescViewsDesc(pageable);
        List<MovieResponse> movies = moviePage.getContent().stream()
                .map(MovieMapper::mapToHightRate) // <-- chỉ trả về trường bạn muốn
                .toList();
        return new PageResponse<>(movies, moviePage.getTotalPages(), moviePage.getTotalElements());
    }


    @Transactional(readOnly = true)
    public List<MovieResponse> searchTrendingMoviesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        return moviePage.getContent().stream().map(MovieMapper::mapToTrendingMovieResponse).toList();
    }


    // Thêm phim mới
    @Transactional
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // Thêm phim mới với MovieRequest, trả về MovieResponse DTO

    @Transactional
    public MovieResponse addMovie(MovieRequest req, MultipartFile posterImageFile) {
        Movie movie = MovieMapper.mapAddMovieRequestToEntity(req);
        if (posterImageFile != null && !posterImageFile.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(posterImageFile.getBytes(), ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");
                movie.setPosterUrl(url);
                movie.setPosterPublicId(publicId);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi upload ảnh lên Cloudinary", e);
            }
        }
        Movie saved = movieRepository.save(movie);
        return MovieMapper.mapToTrendingMovieResponse(saved);
    }

    // Cập nhật phim
    @Transactional
    public Movie updateMovie(Movie movie) {
        if (movie.getId() == null || !movieRepository.existsById(movie.getId())) {
            throw new IllegalArgumentException("Movie not found");
        }
        return movieRepository.save(movie);
    }

    // Cập nhật phim, hỗ trợ update poster và xóa ảnh cũ trên Cloudinary nếu có
    @Transactional
    public MovieResponse updateMovie(Long movieId, MovieRequest req, MultipartFile posterImageFile) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        // Cập nhật các trường khác
        movie.setTitle(req.getTitle());
        movie.setOthernames(req.getOthernames());
        movie.setBasePrice(req.getBasePrice());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setDescription(req.getDescription());
        // Nếu có file ảnh mới thì xóa ảnh cũ trên Cloudinary và upload mới
        if (posterImageFile != null && !posterImageFile.isEmpty()) {
            // Xóa ảnh cũ nếu có publicId
            if (movie.getPosterPublicId() != null) {
                try {
                    cloudinary.uploader().destroy(movie.getPosterPublicId(), ObjectUtils.emptyMap());
                } catch (Exception e) {
                    // Có thể log lỗi nhưng không throw để không làm fail update
                }
            }
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(posterImageFile.getBytes(), ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");
                movie.setPosterUrl(url);
                movie.setPosterPublicId(publicId);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi upload ảnh lên Cloudinary", e);
            }
        }
        Movie saved = movieRepository.save(movie);
        return MovieMapper.mapToTrendingMovieResponse(saved);
    }

    // Xoá phim
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Movie not found");
        }
        movieRepository.deleteById(id);
    }
}
