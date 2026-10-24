package com.symphonyai.moviecatalog.dto.response;

import java.util.HashSet;
import java.util.Set;

public class MovieResponse {

    private Long id;
    private String title;
    private Integer releaseYear;
    private Double rating;
    private DirectorResponse director;
    private Set<GenreResponse> genres = new HashSet<>();

    public MovieResponse() {
    }

    public MovieResponse(Long id, String title, Integer releaseYear, Double rating, DirectorResponse director, Set<GenreResponse> genres) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.director = director;
        this.genres = genres != null ? genres : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public DirectorResponse getDirector() {
        return director;
    }

    public void setDirector(DirectorResponse director) {
        this.director = director;
    }

    public Set<GenreResponse> getGenres() {
        return genres;
    }

    public void setGenres(Set<GenreResponse> genres) {
        this.genres = genres != null ? genres : new HashSet<>();
    }
}
