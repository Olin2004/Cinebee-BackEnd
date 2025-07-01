package com.cinebee.service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.cinebee.dto.request.MovieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.cinebee.exception.ApiException;
import com.cinebee.exception.ErrorCode;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    @Autowired private Cloudinary cloudinary;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    /**
     * Get a list of trending movies, sorted by rating, likes, and views.
     * @param limit the maximum number of movies to return
     * @return a list of MovieResponse objects representing the trending movies
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getTrendingMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Movie> page = movieRepository.findAllByOrderByRatingDescLikesDescViewsDesc(pageable);
        return IntStream.range(0, page.getContent().size())
                .mapToObj(i -> {
                    MovieResponse res = MovieMapper.mapToTrendingMovieResponse(page.getContent().get(i));
                    res.setRank(i + 1);
                    return res;
                })
                .toList();
    }

    /**
     * Get a paginated response of trending movies, sorted by likes and views.
     * @param page the page number (0-indexed)
     * @param size the size of each page
     * @return a PageResponse containing MovieResponse objects
     */
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> getTrendingMoviesPageResponse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var moviePage = movieRepository.findAllByOrderByLikesDescViewsDesc(pageable);
        List<MovieResponse> movies = moviePage.getContent().stream()
                .map(MovieMapper::mapToHightRate)
                .toList();
        return new PageResponse<>(movies, moviePage.getTotalPages(), moviePage.getTotalElements());
    }


    /**
     * Search for trending movies by title.
     * @param title the title to search for
     * @param page the page number (0-indexed)
     * @param size the size of each page
     * @return a list of MovieResponse objects matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> searchTrendingMoviesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable)
                .getContent().stream().map(MovieMapper::mapToTrendingMovieResponse).toList();
    }

    /**
     * Add a new movie to the database.
     * @param req the MovieRequest containing movie details
     * @param posterImageFile the poster image file to upload
     * @return a MovieResponse representing the saved movie
     */

    @Transactional
    public MovieResponse addMovie(MovieRequest req, MultipartFile posterImageFile) {
        Movie movie = MovieMapper.mapAddMovieRequestToEntity(req);
        if (posterImageFile != null && !posterImageFile.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(posterImageFile.getBytes(), ObjectUtils.emptyMap());
                movie.setPosterUrl((String) uploadResult.get("secure_url"));
                movie.setPosterPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new ApiException(ErrorCode.MOVIE_IMAGE_UPLOAD_FAILED);
            }
        }
        return MovieMapper.mapToTrendingMovieResponse(movieRepository.save(movie));
    }

    /**
     * Update an existing movie in the database.
     * @param movieId the ID of the movie to update
     * @param req the MovieRequest containing updated movie details
     * @param posterImageFile the new poster image file to upload (optional)
     * @return a MovieResponse representing the updated movie
     */

    @Transactional
    public MovieResponse updateMovie(Long movieId, MovieRequest req, MultipartFile posterImageFile) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ApiException(ErrorCode.MOVIE_NOT_FOUND));
        movie.setTitle(req.getTitle());
        movie.setOthernames(req.getOthernames());
        movie.setBasePrice(req.getBasePrice());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setDescription(req.getDescription());
        if (posterImageFile != null && !posterImageFile.isEmpty()) {
            if (movie.getPosterPublicId() != null) {
                try { cloudinary.uploader().destroy(movie.getPosterPublicId(), ObjectUtils.emptyMap()); } catch (Exception ignored) {}
            }
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(posterImageFile.getBytes(), ObjectUtils.emptyMap());
                movie.setPosterUrl((String) uploadResult.get("secure_url"));
                movie.setPosterPublicId((String) uploadResult.get("public_id"));
            } catch (Exception e) {
                throw new ApiException(ErrorCode.MOVIE_IMAGE_UPLOAD_FAILED);
            }
        }
        return MovieMapper.mapToTrendingMovieResponse(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) throw new ApiException(ErrorCode.MOVIE_NOT_FOUND);
        movieRepository.deleteById(id);
    }
}
