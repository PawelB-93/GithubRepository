package com.githubrepository.GithubRepository.controllers;

import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.BranchDto;
import com.githubrepository.GithubRepository.models.RepositoryDto;
import com.githubrepository.GithubRepository.services.GithubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GithubRepositoryController.class)
class GithubRepositoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GithubRepositoryService service;

    private final List<RepositoryDto> repositories = new ArrayList<>();

    @BeforeEach
    void setUp() {
        RepositoryDto repository1 = new RepositoryDto("Repository1", "Owner1", List.of(new BranchDto("master", "123456")));
        RepositoryDto repository2 = new RepositoryDto("Repository2", "Owner1", List.of(new BranchDto("master", "654321")));
        repositories.add(repository1);
        repositories.add(repository2);
    }

    @Test
    public void testGetByOwnerLoginShouldReturnRepositories() {
        // GIVEN
        when(service.getByOwnerLogin(anyString())).thenReturn(Mono.just(repositories));

        // WHEN & THEN
        webTestClient.get().uri("/github/Owner1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Repository1")
                .jsonPath("$[0].login").isEqualTo("Owner1")
                .jsonPath("$[0].branches[0].name").isEqualTo("master")
                .jsonPath("$[0].branches[0].sha").isEqualTo("123456")
                .jsonPath("$[1].name").isEqualTo("Repository2")
                .jsonPath("$[1].login").isEqualTo("Owner1")
                .jsonPath("$[1].branches[0].name").isEqualTo("master")
                .jsonPath("$[1].branches[0].sha").isEqualTo("654321");
    }

    @Test
    public void testGetErrorMessageWhenOwnerDoesNotExist() {
        //GIVEN
        when(service.getByOwnerLogin("Owner1")).thenThrow(new OwnerNotFoundException(HttpStatus.NOT_FOUND, "Owner not found!!!"));
        //WHEN
        //THEN
        webTestClient.get().uri("/github/Owner1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo("404")
                .jsonPath("$.message").isEqualTo("Owner not found!!!");
    }
}