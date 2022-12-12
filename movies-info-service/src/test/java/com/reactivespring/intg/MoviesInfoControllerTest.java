package com.reactivespring.intg;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class MoviesInfoControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    static String MOVIES_INFO_URL = "/movieinfos";

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo("def", "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository
                .deleteAll()
                .thenMany(movieInfoRepository.saveAll(movieinfos))
                .blockLast();
    }

    @AfterEach
    void testDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addNewMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getMovieInfoId() != null;

                });
    }

    @Test
    void findAllMoviesInfoTest(){
        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void findAllMoviesInfoByYearTest(){
        webTestClient.get()
                .uri(MOVIES_INFO_URL +"?year=2012")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoById_1() {
        var id = "def";
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .isEqualTo(new MovieInfo("def", "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")));
    }

    @Test
    void updateMovieInfoById() {
        var id = "def";
        var updatedMovieInfo = new MovieInfo("def", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .isEqualTo(updatedMovieInfo);
    }

    @Test
    void updateMovieInfoById_notFound() {
        var id = "df";
        var updatedMovieInfo = new MovieInfo("def", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteMovieInfoById(){
        var id = "def";
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL )
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }




}
