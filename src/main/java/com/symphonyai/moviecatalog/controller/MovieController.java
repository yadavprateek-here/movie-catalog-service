package com.symphonyai.moviecatalog.controller;

import com.symphonyai.moviecatalog.dto.request.MovieCreateRequest;
import com.symphonyai.moviecatalog.dto.request.MovieUpdateRequest;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;
import com.symphonyai.moviecatalog.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "Endpoints for managing movies and genre associations")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @Operation(summary = "Create a movie (include genreIds)")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieCreateRequest request) {
        MovieResponse response = movieService.createMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all movies (optional filter by genre and/or release year)")
    public ResponseEntity<List<MovieResponse>> getAllMovies(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(movieService.getAllMovies(genre, year));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID with genres")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie details")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id,
                                                     @Valid @RequestBody MovieUpdateRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a movie")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/genres/{gid}")
    @Operation(summary = "Associate a genre with a movie")
    public ResponseEntity<MovieResponse> associateGenre(@PathVariable Long id,
                                                        @PathVariable Long gid) {
        return ResponseEntity.ok(movieService.associateGenre(id, gid));
    }

    @DeleteMapping("/{id}/genres/{gid}")
    @Operation(summary = "Remove a genre from a movie")
    public ResponseEntity<MovieResponse> removeGenre(@PathVariable Long id,
                                                     @PathVariable Long gid) {
        return ResponseEntity.ok(movieService.removeGenre(id, gid));
    }
}
