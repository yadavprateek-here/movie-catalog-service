package com.symphonyai.moviecatalog.service;

import com.symphonyai.moviecatalog.config.CatalogProperties;
import com.symphonyai.moviecatalog.dto.request.MovieCreateRequest;
import com.symphonyai.moviecatalog.dto.request.MovieUpdateRequest;
import com.symphonyai.moviecatalog.dto.response.MovieResponse;
import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.entity.Movie;
import com.symphonyai.moviecatalog.exception.BusinessValidationException;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.repository.DirectorRepository;
import com.symphonyai.moviecatalog.repository.GenreRepository;
import com.symphonyai.moviecatalog.repository.MovieRepository;
import com.symphonyai.moviecatalog.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private GenreRepository genreRepository;

    private CatalogProperties catalogProperties;
    private MovieServiceImpl movieService;

    private Director director;
    private Genre genreSciFi;
    private Genre genreAction;
    private Movie movie;

    @BeforeEach
    void setUp() {
        catalogProperties = new CatalogProperties(5, 5.0);
        movieService = new MovieServiceImpl(movieRepository, directorRepository, genreRepository, catalogProperties);

        director = new Director(1L, "Christopher Nolan", "British-American", 1970);
        genreSciFi = new Genre(1L, "Sci-Fi");
        genreAction = new Genre(2L, "Action");

        movie = new Movie(10L, "Inception", 2010, 8.8, director);
        movie.setGenres(new HashSet<>(Set.of(genreSciFi, genreAction)));
    }

    @Test
    @DisplayName("createMovie: Should create movie with supplied rating and genres")
    void createMovie_success_withCustomRating() {
        MovieCreateRequest request = new MovieCreateRequest("Inception", 2010, 8.8, 1L, Set.of(1L, 2L));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genreSciFi));
        when(genreRepository.findById(2L)).thenReturn(Optional.of(genreAction));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.createMovie(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Inception");
        assertThat(response.getRating()).isEqualTo(8.8);
        assertThat(response.getDirector().getName()).isEqualTo("Christopher Nolan");
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("createMovie: Should apply default rating when rating is not provided")
    void createMovie_success_withDefaultRatingFallback() {
        MovieCreateRequest request = new MovieCreateRequest("Interstellar", 2014, null, 1L, Set.of(1L));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genreSciFi));

        Movie savedMovie = new Movie(11L, "Interstellar", 2014, 5.0, director);
        savedMovie.setGenres(Set.of(genreSciFi));
        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        MovieResponse response = movieService.createMovie(request);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("createMovie: Should throw BusinessValidationException when genres count exceeds max limit")
    void createMovie_exceedsMaxGenres_throwsException() {
        catalogProperties.setMaxGenresPerMovie(2); // override limit for test
        MovieCreateRequest request = new MovieCreateRequest("Epic", 2024, 7.5, 1L, Set.of(1L, 2L, 3L));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        assertThatThrownBy(() -> movieService.createMovie(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("A movie cannot have more than 2 genres");

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    @DisplayName("createMovie: Should throw ResourceNotFoundException when director not found")
    void createMovie_directorNotFound_throwsException() {
        MovieCreateRequest request = new MovieCreateRequest("Unknown", 2024, 7.0, 999L, Set.of());
        when(directorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.createMovie(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Director not found with id: 999");
    }

    @Test
    @DisplayName("createMovie: Should throw ResourceNotFoundException when genre ID does not exist")
    void createMovie_genreNotFound_throwsException() {
        MovieCreateRequest request = new MovieCreateRequest("Unknown", 2024, 7.0, 1L, Set.of(404L));

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(genreRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.createMovie(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Genre not found with id: 404");
    }

    @Test
    @DisplayName("getAllMovies: Should return filtered movie list")
    void getAllMovies_withFilters() {
        when(movieRepository.findByGenreAndYear("Sci-Fi", 2010)).thenReturn(List.of(movie));

        List<MovieResponse> result = movieService.getAllMovies("Sci-Fi", 2010);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
        verify(movieRepository, times(1)).findByGenreAndYear("Sci-Fi", 2010);
    }

    @Test
    @DisplayName("getMovieById: Should return movie when ID exists")
    void getMovieById_success() {
        when(movieRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(movie));

        MovieResponse response = movieService.getMovieById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("getMovieById: Should throw ResourceNotFoundException when movie not found")
    void getMovieById_notFound_throwsException() {
        when(movieRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie not found with id: 999");
    }

    @Test
    @DisplayName("updateMovie: Should successfully update movie details")
    void updateMovie_success() {
        MovieUpdateRequest updateRequest = new MovieUpdateRequest("Inception: Director's Cut", 2010, 9.2, null, null);
        when(movieRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.updateMovie(10L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(movie.getTitle()).isEqualTo("Inception: Director's Cut");
        assertThat(movie.getRating()).isEqualTo(9.2);
    }

    @Test
    @DisplayName("deleteMovie: Should delete movie when ID exists")
    void deleteMovie_success() {
        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        doNothing().when(movieRepository).delete(movie);

        movieService.deleteMovie(10L);

        verify(movieRepository, times(1)).delete(movie);
    }

    @Test
    @DisplayName("getMoviesByDirector: Should return movies for an existing director")
    void getMoviesByDirector_success() {
        when(directorRepository.existsById(1L)).thenReturn(true);
        when(movieRepository.findByDirectorId(1L)).thenReturn(List.of(movie));

        List<MovieResponse> result = movieService.getMoviesByDirector(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("getMoviesByDirector: Should throw ResourceNotFoundException when director not found")
    void getMoviesByDirector_directorNotFound_throwsException() {
        when(directorRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> movieService.getMoviesByDirector(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Director not found with id: 999");
    }

    @Test
    @DisplayName("associateGenre: Should associate new genre to movie")
    void associateGenre_success() {
        Genre drama = new Genre(3L, "Drama");
        movie.setGenres(new HashSet<>(Set.of(genreSciFi))); // 1 genre currently

        when(movieRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(3L)).thenReturn(Optional.of(drama));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.associateGenre(10L, 3L);

        assertThat(response).isNotNull();
        assertThat(movie.getGenres()).contains(drama);
    }

    @Test
    @DisplayName("associateGenre: Should throw BusinessValidationException if limit reached")
    void associateGenre_exceedsMaxLimit_throwsException() {
        catalogProperties.setMaxGenresPerMovie(2); // set max to 2
        Genre drama = new Genre(3L, "Drama");
        movie.setGenres(new HashSet<>(Set.of(genreSciFi, genreAction))); // already 2 genres

        when(movieRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(3L)).thenReturn(Optional.of(drama));

        assertThatThrownBy(() -> movieService.associateGenre(10L, 3L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Maximum limit of 2 genres reached");

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    @DisplayName("removeGenre: Should remove genre from movie")
    void removeGenre_success() {
        when(movieRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genreSciFi));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieResponse response = movieService.removeGenre(10L, 1L);

        assertThat(response).isNotNull();
        assertThat(movie.getGenres()).doesNotContain(genreSciFi);
    }
}
