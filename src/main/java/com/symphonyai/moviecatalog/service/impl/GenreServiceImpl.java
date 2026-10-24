package com.symphonyai.moviecatalog.service.impl;

import com.symphonyai.moviecatalog.dto.request.GenreRequest;
import com.symphonyai.moviecatalog.dto.response.GenreResponse;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.exception.DuplicateResourceException;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.mapper.EntityMapper;
import com.symphonyai.moviecatalog.repository.GenreRepository;
import com.symphonyai.moviecatalog.service.GenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public GenreResponse createGenre(GenreRequest request) {
        String trimmedName = request.getName().trim();
        if (genreRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Genre already exists with name: " + trimmedName);
        }

        Genre genre = new Genre(trimmedName);
        Genre saved = genreRepository.save(genre);
        return EntityMapper.toGenreResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(EntityMapper::toGenreResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        return EntityMapper.toGenreResponse(genre);
    }
}
