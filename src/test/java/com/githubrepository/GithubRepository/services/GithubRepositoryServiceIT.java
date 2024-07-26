package com.githubrepository.GithubRepository.services;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@WireMockTest(httpPort = 8181)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class GithubRepositoryServiceIT {

    @Autowired
    private WebTestClient webTestClient;

    private final String ownerLogin = "repositoryOwner";

    @Test
    public void testGetAllRepositories() {
        //GIVEN
        stubFor(WireMock.get(urlEqualTo("/users/" + this.ownerLogin + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"repository1\",\"owner\":{\"login\":\"repositoryOwner\"},\"fork\":false}]")));

        stubFor(WireMock.get(urlEqualTo("/repos/" + this.ownerLogin + "/repository1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\": \"main\", \"commit\": {\"sha\": \"12345\"}}]")));
        //WHEN
        //THEN
        this.webTestClient.get().uri("/github/repositoryOwner")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].login").isEqualTo("repositoryOwner")
                .jsonPath("$[0].name").isEqualTo("repository1");
    }

    @Test
    public void testRepositoriesShouldNotBeReturnedWhenRepositoryIsFork() {
        //GIVEN
        stubFor(WireMock.get(urlEqualTo("/users/" + this.ownerLogin + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"repository1\",\"owner\":{\"login\":\"repositoryOwner\"},\"fork\":true}]")));

        stubFor(WireMock.get(urlEqualTo("/repos/" + this.ownerLogin + "/repository1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\": \"main\", \"commit\": {\"sha\": \"12345\"}}]")));
        //WHEN
        //THEN
        this.webTestClient.get().uri("/github/repositoryOwner")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEmpty();
    }

    @Test
    public void testGetAllRepositories_UserNotFound() {
        //GIVEN
        stubFor(WireMock.get(urlEqualTo("/users/" + this.ownerLogin + "/repos"))
                .willReturn(aResponse().withStatus(404)));
        //WHEN
        //THEN
        this.webTestClient.get().uri("/github/repositoryOwner")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("404")
                .jsonPath("$.message").isEqualTo("Owner not found!!!");
    }
}