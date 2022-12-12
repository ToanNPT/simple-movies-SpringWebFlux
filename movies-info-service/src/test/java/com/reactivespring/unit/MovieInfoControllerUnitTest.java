package com.reactivespring.unit;

import com.reactivespring.controller.MoviesInfoController;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {
    static String MOVIES_INFO_URL = "/movieinfos";

    @MockBean
    private MovieInfoService moviesInfoServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllMovieInfos() {

        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(moviesInfoServiceMock.findAll()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        var id = "abc";

        when(moviesInfoServiceMock.findById(isA(String.class)))
                .thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    public void addMovieInfoTest(){
        MovieInfo model = new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        var added = Mono.just(model);
        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(added);
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(model)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(p -> {
                    var saved = p.getResponseBody();
                    assert Objects.requireNonNull(saved).getMovieInfoId() != null;
                });
    }

    @Test
    public void addMovieInfoTest_validation(){
        MovieInfo model = new MovieInfo("abc", "",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        var added = Mono.just(model);
        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(added);
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(model)
                .exchange()
                .expectBody(String.class)
                .consumeWith(p -> {
                    var error = p.getResponseBody();
                    System.out.println("Error: " + error);
                    var expectedResult = "movieInfo.name must be present";
                    assertEquals(expectedResult, error);
                });
    }

    @Test
    public void updateMovieInfo(){
        var id = "abc";
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));

        when(moviesInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class)))
                .thenReturn(Mono.just(updatedMovieInfo));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                    assertEquals("Dark Knight Rises 1", movieInfo.getName());
                });
    }

    @Test
    public void updateMovieInfo_notFound(){
        var id = "abc";
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 1",
                2013, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2012-07-20"));

        when(moviesInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL +"/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void deleteMovieInfo(){
        var id = "abc";
        when(moviesInfoServiceMock.deleteMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

}
