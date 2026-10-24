package com.symphonyai.moviecatalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphonyai.moviecatalog.dto.request.DirectorRequest;
import com.symphonyai.moviecatalog.dto.request.GenreRequest;
import com.symphonyai.moviecatalog.dto.request.MovieCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: GET /api/info should return externalized app properties")
    void getInfo_shouldReturnAppMetadata() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Movie Catalog Service")))
                .andExpect(jsonPath("$.version", is("1.0.0")));
    }

    @Test
    @DisplayName("Integration: Full End-to-End Flow - Director -> Genre -> Movie -> Association")
    void fullMovieCatalogLifecycleTest() throws Exception {
        // 1. Create Director
        DirectorRequest directorReq = new DirectorRequest("Denis Villeneuve", "Canadian", 1967);
        String directorJson = mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(directorReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Denis Villeneuve")))
                .andReturn().getResponse().getContentAsString();

        Long directorId = objectMapper.readTree(directorJson).get("id").asLong();

        // 2. Create Genre
        GenreRequest genreReq = new GenreRequest("Cyberpunk");
        String genreJson = mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Cyberpunk")))
                .andReturn().getResponse().getContentAsString();

        Long genreId = objectMapper.readTree(genreJson).get("id").asLong();

        // 3. Create Movie (leave rating null to test default rating fallback)
        MovieCreateRequest movieReq = new MovieCreateRequest("Blade Runner 2049", 2017, null, directorId, Set.of(genreId));
        String movieJson = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Blade Runner 2049")))
                .andExpect(jsonPath("$.rating", is(5.0))) // fallback default rating from properties
                .andExpect(jsonPath("$.director.name", is("Denis Villeneuve")))
                .andExpect(jsonPath("$.genres[0].name", is("Cyberpunk")))
                .andReturn().getResponse().getContentAsString();

        Long movieId = objectMapper.readTree(movieJson).get("id").asLong();

        // 4. Filter movies by year and genre
        mockMvc.perform(get("/api/movies")
                        .param("genre", "Cyberpunk")
                        .param("year", "2017"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", is("Blade Runner 2049")));

        // 5. Get movies by director
        mockMvc.perform(get("/api/directors/{id}/movies", directorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", is("Blade Runner 2049")));

        // 6. Remove genre from movie
        mockMvc.perform(delete("/api/movies/{id}/genres/{gid}", movieId, genreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres", hasSize(0)));
    }

    @Test
    @DisplayName("Integration: Validation Failure returns structured 400 error")
    void createDirector_invalidInput_returns400WithErrors() throws Exception {
        DirectorRequest invalidReq = new DirectorRequest("", null, null); // blank name
        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    @DisplayName("Integration: Resource not found returns structured 404 error")
    void getDirector_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/directors/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Director not found with id: 99999")));
    }
}
