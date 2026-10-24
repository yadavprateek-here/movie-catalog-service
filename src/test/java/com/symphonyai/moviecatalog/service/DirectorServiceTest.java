package com.symphonyai.moviecatalog.service;

import com.symphonyai.moviecatalog.dto.request.DirectorRequest;
import com.symphonyai.moviecatalog.dto.response.DirectorResponse;
import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.repository.DirectorRepository;
import com.symphonyai.moviecatalog.service.impl.DirectorServiceImpl;
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
class DirectorServiceTest {

    @Mock
    private DirectorRepository directorRepository;

    @InjectMocks
    private DirectorServiceImpl directorService;

    private Director director;

    @BeforeEach
    void setUp() {
        director = new Director(1L, "Christopher Nolan", "British-American", 1970);
    }

    @Test
    @DisplayName("createDirector: Should successfully save and return DirectorResponse")
    void createDirector_success() {
        DirectorRequest request = new DirectorRequest("Christopher Nolan", "British-American", 1970);
        when(directorRepository.save(any(Director.class))).thenReturn(director);

        DirectorResponse response = directorService.createDirector(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Christopher Nolan");
        verify(directorRepository, times(1)).save(any(Director.class));
    }

    @Test
    @DisplayName("getAllDirectors: Should return list of all directors")
    void getAllDirectors_success() {
        when(directorRepository.findAll()).thenReturn(List.of(director));

        List<DirectorResponse> result = directorService.getAllDirectors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Christopher Nolan");
        verify(directorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getDirectorById: Should return director when ID exists")
    void getDirectorById_success() {
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        DirectorResponse response = directorService.getDirectorById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Christopher Nolan");
    }

    @Test
    @DisplayName("getDirectorById: Should throw ResourceNotFoundException when director not found")
    void getDirectorById_notFound_throwsException() {
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.getDirectorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Director not found with id: 99");
    }

    @Test
    @DisplayName("updateDirector: Should successfully update existing director")
    void updateDirector_success() {
        DirectorRequest updateRequest = new DirectorRequest("Nolan Updated", "British", 1970);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(directorRepository.save(any(Director.class))).thenReturn(director);

        DirectorResponse response = directorService.updateDirector(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(director.getName()).isEqualTo("Nolan Updated");
        verify(directorRepository, times(1)).save(director);
    }

    @Test
    @DisplayName("updateDirector: Should throw ResourceNotFoundException when director to update not found")
    void updateDirector_notFound_throwsException() {
        DirectorRequest updateRequest = new DirectorRequest("Nolan Updated", "British", 1970);
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.updateDirector(99L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Director not found with id: 99");
    }

    @Test
    @DisplayName("deleteDirector: Should delete director when ID exists")
    void deleteDirector_success() {
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        doNothing().when(directorRepository).delete(director);

        directorService.deleteDirector(1L);

        verify(directorRepository, times(1)).delete(director);
    }

    @Test
    @DisplayName("deleteDirector: Should throw ResourceNotFoundException when director not found")
    void deleteDirector_notFound_throwsException() {
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directorService.deleteDirector(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Director not found with id: 99");
    }
}
