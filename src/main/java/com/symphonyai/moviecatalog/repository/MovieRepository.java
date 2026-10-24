package com.symphonyai.moviecatalog.repository;

import com.symphonyai.moviecatalog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByDirectorId(Long directorId);

    @Query("SELECT DISTINCT m FROM Movie m " +
           "LEFT JOIN m.genres g " +
           "WHERE (:year IS NULL OR m.releaseYear = :year) " +
           "AND (:genre IS NULL OR LOWER(g.name) = LOWER(:genre))")
    List<Movie> findByGenreAndYear(@Param("genre") String genre, @Param("year") Integer year);

    @Query("SELECT m FROM Movie m " +
           "LEFT JOIN FETCH m.genres " +
           "LEFT JOIN FETCH m.director " +
           "WHERE m.id = :id")
    Optional<Movie> findByIdWithDetails(@Param("id") Long id);
}
