package com.symphonyai.moviecatalog.service.impl;

import com.symphonyai.moviecatalog.config.CatalogProperties;
import com.symphonyai.moviecatalog.dto.request.MovieCreateRequest;
import com.symphonyai.moviecatalog.dto.request.MovieUpdateRequest;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;
import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.entity.Movie;
import com.symphonyai.moviecatalog.exception.BusinessValidationException;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.mapper.EntityMapper;
import com.symphonyai.moviecatalog.repository.DirectorRepository;
import com.symphonyai.moviecatalog.repository.GenreRepository;
import com.symphonyai.moviecatalog.repository.MovieRepository;
import com.symphonyai.moviecatalog.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;
    private final CatalogProperties catalogProperties;

    public MovieServiceImpl(MovieRepository movieRepository,
                            DirectorRepository directorRepository,
                            GenreRepository genreRepository,
                            CatalogProperties catalogProperties) {
        this.movieRepository = movieRepository;
        this.directorRepository = directorRepository;
        this.genreRepository = genreRepository;
        this.catalogProperties = catalogProperties;
    }

    @Override
    public MovieResponse createMovie(MovieCreateRequest request) {
        Director director = directorRepository.findById(request.getDirectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + request.getDirectorId()));

        Set<Long> genreIds = request.getGenreIds() != null ? request.getGenreIds() : new HashSet<>();
        if (genreIds.size() > catalogProperties.getMaxGenresPerMovie()) {
            throw new BusinessValidationException("A movie cannot have more than " 
                    + catalogProperties.getMaxGenresPerMovie() + " genres. Provided: " + genreIds.size());
        }

        Set<Genre> genres = new HashSet<>();
        for (Long gid : genreIds) {
            Genre genre = genreRepository.findById(gid)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + gid));
            genres.add(genre);
        }

        Double rating = request.getRating() != null ? request.getRating() : catalogProperties.getDefaultRating();

        Movie movie = new Movie();
        movie.setTitle(request.getTitle().trim());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setRating(rating);
        movie.setDirector(director);
        movie.setGenres(genres);

        Movie saved = movieRepository.save(movie);
        return EntityMapper.toMovieResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies(String genre, Integer year) {
        String cleanGenre = (genre != null && !genre.isBlank()) ? genre.trim() : null;
        List<Movie> movies = movieRepository.findByGenreAndYear(cleanGenre, year);
        return movies.stream()
                .map(EntityMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return EntityMapper.toMovieResponse(movie);
    }

    @Override
    public MovieResponse updateMovie(Long id, MovieUpdateRequest request) {
        Movie movie = movieRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        movie.setTitle(request.getTitle().trim());
        movie.setReleaseYear(request.getReleaseYear());

        if (request.getRating() != null) {
            movie.setRating(request.getRating());
        }

        if (request.getDirectorId() != null) {
            Director director = directorRepository.findById(request.getDirectorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + request.getDirectorId()));
            movie.setDirector(director);
        }

        if (request.getGenreIds() != null) {
            if (request.getGenreIds().size() > catalogProperties.getMaxGenresPerMovie()) {
                throw new BusinessValidationException("A movie cannot have more than " 
                        + catalogProperties.getMaxGenresPerMovie() + " genres. Provided: " + request.getGenreIds().size());
            }

            Set<Genre> genres = new HashSet<>();
            for (Long gid : request.getGenreIds()) {
                Genre genre = genreRepository.findById(gid)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + gid));
                genres.add(genre);
            }
            movie.setGenres(genres);
        }

        Movie updated = movieRepository.save(movie);
        return EntityMapper.toMovieResponse(updated);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByDirector(Long directorId) {
        if (!directorRepository.existsById(directorId)) {
            throw new ResourceNotFoundException("Director not found with id: " + directorId);
        }
        return movieRepository.findByDirectorId(directorId).stream()
                .map(EntityMapper::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovieResponse associateGenre(Long movieId, Long genreId) {
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));

        if (!movie.getGenres().contains(genre)) {
            if (movie.getGenres().size() >= catalogProperties.getMaxGenresPerMovie()) {
                throw new BusinessValidationException("Cannot associate genre. Maximum limit of " 
                        + catalogProperties.getMaxGenresPerMovie() + " genres reached for this movie.");
            }
            movie.addGenre(genre);
            movie = movieRepository.save(movie);
        }

        return EntityMapper.toMovieResponse(movie);
    }

    @Override
    public MovieResponse removeGenre(Long movieId, Long genreId) {
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));

        movie.removeGenre(genre);
        movie = movieRepository.save(movie);
        return EntityMapper.toMovieResponse(movie);
    }
}
