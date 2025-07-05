package com.cinebee.controller;

import java.util.List;

import com.cinebee.dto.request.MovieRequest;
import com.cinebee.dto.response.MovieResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import com.cinebee.service.MovieService;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie", description = "APIs for Movie Management")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Operation(summary = "Get trending movies", description = "Returns a list of the top 10 trending movies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
    })
    @GetMapping("/trending")
    public ResponseEntity<List<MovieResponse>> getTrendingMovies() {
        List<MovieResponse> trendingMovies = movieService.getTrendingMovies(10);
        return ResponseEntity.ok(trendingMovies);
    }

    @Operation(summary = "Search for movies", description = "Searches for movies by title with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
    })
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchSuggestMovies(
            @Parameter(description = "Title of the movie to search for") @RequestParam String title) {

        List<MovieResponse> movies = movieService.searchTrendingMoviesByTitle(title, 0, 20);
        return ResponseEntity.ok(movies);
    }


    @Operation(summary = "Add a new movie", description = "Adds a new movie with its poster image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(value = "/add-new-film", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponse> addMovie(
            @Parameter(description = "JSON object with movie information") @Valid @RequestPart("info") MovieRequest movieRequest,
            @Parameter(description = "Poster image file") @RequestPart(value = "posterImageFile", required = false) MultipartFile posterImageFile) {
        MovieResponse saved = movieService.addMovie(movieRequest, posterImageFile);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a movie", description = "Updates an existing movie's information and poster image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping(value = "/update-film", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponse> updateMovie(
            @Parameter(description = "ID of the movie to update") @RequestParam("id") Long id,
            @Parameter(description = "JSON object with updated movie information") @Valid @RequestPart("info") MovieRequest movieRequest,
            @Parameter(description = "New poster image file") @RequestPart(value = "posterImageFile", required = false) MultipartFile posterImageFile) {
        MovieResponse updated = movieService.updateMovie(id, movieRequest, posterImageFile);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMovie(@Parameter(description = "ID of the movie to delete") @RequestParam Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all movies", description = "Returns a paginated list of all movies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
    })
    @GetMapping("/list-movies")
    public ResponseEntity<?> getAllMoviesPaged(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(movieService.getAllMoviesPaged(page, size));
    }
}
