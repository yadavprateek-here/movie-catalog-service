package com.symphonyai.moviecatalog.service;

import com.symphonyai.moviecatalog.dto.request.GenreRequest;
import com.symphonyai.moviecatalog.dto.response.GenreResponse;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.exception.DuplicateResourceException;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.repository.GenreRepository;
import com.symphonyai.moviecatalog.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = new Genre(1L, "Sci-Fi");
    }

    @Test
    @DisplayName("createGenre: Should successfully create a new unique genre")
    void createGenre_success() {
        GenreRequest request = new GenreRequest("Sci-Fi");
        when(genreRepository.existsByNameIgnoreCase("Sci-Fi")).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        GenreResponse response = genreService.createGenre(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Sci-Fi");
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    @DisplayName("createGenre: Should throw DuplicateResourceException if genre name already exists")
    void createGenre_duplicate_throwsException() {
        GenreRequest request = new GenreRequest("Sci-Fi");
        when(genreRepository.existsByNameIgnoreCase("Sci-Fi")).thenReturn(true);

        assertThatThrownBy(() -> genreService.createGenre(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Genre already exists with name: Sci-Fi");

        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    @DisplayName("getAllGenres: Should return all genres")
    void getAllGenres_success() {
        when(genreRepository.findAll()).thenReturn(List.of(genre, new Genre(2L, "Action")));

        List<GenreResponse> result = genreService.getAllGenres();

        assertThat(result).hasSize(2);
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getGenreById: Should return genre when ID exists")
    void getGenreById_success() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        GenreResponse response = genreService.getGenreById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Sci-Fi");
    }

    @Test
    @DisplayName("getGenreById: Should throw ResourceNotFoundException when genre not found")
    void getGenreById_notFound_throwsException() {
        when(genreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getGenreById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Genre not found with id: 99");
    }
}
