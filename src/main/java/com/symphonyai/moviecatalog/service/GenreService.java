package com.symphonyai.moviecatalog.service;

import com.symphonyai.moviecatalog.dto.request.GenreRequest;
import com.symphonyai.moviecatalog.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {
    GenreResponse createGenre(GenreRequest request);
    List<GenreResponse> getAllGenres();
    GenreResponse getGenreById(Long id);
}
