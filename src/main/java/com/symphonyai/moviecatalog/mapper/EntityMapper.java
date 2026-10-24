package com.symphonyai.moviecatalog.mapper;

import com.symphonyai.moviecatalog.dto.response.DirectorResponse;
import com.symphonyai.moviecatalog.dto.response.GenreResponse;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;
import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.entity.Movie;

import java.util.stream.Collectors;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static DirectorResponse toDirectorResponse(Director director) {
        if (director == null) {
            return null;
        }
        return new DirectorResponse(
                director.getId(),
                director.getName(),
                director.getNationality(),
                director.getBirthYear()
        );
    }

    public static GenreResponse toGenreResponse(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreResponse(
                genre.getId(),
                genre.getName()
        );
    }

    public static MovieResponse toMovieResponse(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getRating(),
                toDirectorResponse(movie.getDirector()),
                movie.getGenres() != null
                        ? movie.getGenres().stream()
                               .map(EntityMapper::toGenreResponse)
                               .collect(Collectors.toSet())
                        : null
        );
    }
}
