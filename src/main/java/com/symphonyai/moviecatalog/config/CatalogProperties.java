package com.symphonyai.moviecatalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalog")
public class CatalogProperties {

    private int maxGenresPerMovie = 5;
    private double defaultRating = 5.0;

    public CatalogProperties() {
    }

    public CatalogProperties(int maxGenresPerMovie, double defaultRating) {
        this.maxGenresPerMovie = maxGenresPerMovie;
        this.defaultRating = defaultRating;
    }

    public int getMaxGenresPerMovie() {
        return maxGenresPerMovie;
    }

    public void setMaxGenresPerMovie(int maxGenresPerMovie) {
        this.maxGenresPerMovie = maxGenresPerMovie;
    }

    public double getDefaultRating() {
        return defaultRating;
    }

    public void setDefaultRating(double defaultRating) {
        this.defaultRating = defaultRating;
    }
}
