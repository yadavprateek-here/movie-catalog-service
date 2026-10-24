package com.symphonyai.moviecatalog.service;

import com.symphonyai.moviecatalog.dto.request.MovieCreateRequest;
import com.symphonyai.moviecatalog.dto.request.MovieUpdateRequest;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;

import java.util.List;

public interface MovieService {
    MovieResponse createMovie(MovieCreateRequest request);
    List<MovieResponse> getAllMovies(String genre, Integer year);
    MovieResponse getMovieById(Long id);
    MovieResponse updateMovie(Long id, MovieUpdateRequest request);
    void deleteMovie(Long id);
    List<MovieResponse> getMoviesByDirector(Long directorId);
    MovieResponse associateGenre(Long movieId, Long genreId);
    MovieResponse removeGenre(Long movieId, Long genreId);
}
