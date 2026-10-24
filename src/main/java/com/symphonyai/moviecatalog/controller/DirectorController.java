package com.symphonyai.moviecatalog.controller;

import com.symphonyai.moviecatalog.dto.request.DirectorRequest;
import com.symphonyai.moviecatalog.dto.response.DirectorResponse;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;
import com.symphonyai.moviecatalog.service.DirectorService;
import com.symphonyai.moviecatalog.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
@Tag(name = "Directors", description = "Endpoints for managing directors")
public class DirectorController {

    private final DirectorService directorService;
    private final MovieService movieService;

    public DirectorController(DirectorService directorService, MovieService movieService) {
        this.directorService = directorService;
        this.movieService = movieService;
    }

    @PostMapping
    @Operation(summary = "Create a new director")
    public ResponseEntity<DirectorResponse> createDirector(@Valid @RequestBody DirectorRequest request) {
        DirectorResponse response = directorService.createDirector(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all directors")
    public ResponseEntity<List<DirectorResponse>> getAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get director by ID")
    public ResponseEntity<DirectorResponse> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a director")
    public ResponseEntity<DirectorResponse> updateDirector(@PathVariable Long id,
                                                           @Valid @RequestBody DirectorRequest request) {
        return ResponseEntity.ok(directorService.updateDirector(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a director")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/movies")
    @Operation(summary = "Get all movies by a director")
    public ResponseEntity<List<MovieResponse>> getMoviesByDirector(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMoviesByDirector(id));
    }
}
