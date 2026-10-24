package com.symphonyai.moviecatalog.config;

import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.entity.Genre;
import com.symphonyai.moviecatalog.repository.DirectorRepository;
import com.symphonyai.moviecatalog.repository.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SampleDataInitializer {

    @Bean
    public CommandLineRunner initDatabase(DirectorRepository directorRepository,
                                          GenreRepository genreRepository) {
        return args -> {
            if (genreRepository.count() == 0) {
                genreRepository.saveAll(List.of(
                        new Genre("Action"),
                        new Genre("Drama"),
                        new Genre("Sci-Fi"),
                        new Genre("Thriller"),
                        new Genre("Comedy")
                ));
            }

            if (directorRepository.count() == 0) {
                directorRepository.saveAll(List.of(
                        new Director("Christopher Nolan", "British-American", 1970),
                        new Director("Quentin Tarantino", "American", 1963),
                        new Director("Greta Gerwig", "American", 1983)
                ));
            }
        };
    }
}
