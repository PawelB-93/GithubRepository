package com.githubrepository.GithubRepository.controllers;

import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.BranchDto;
import com.githubrepository.GithubRepository.models.RepositoryDto;
import com.githubrepository.GithubRepository.services.GithubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubRepositoryController.class)
class GithubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubRepositoryService service;

    private final List<RepositoryDto> repositories = new ArrayList<>();

    @BeforeEach
    void setUp() {
        RepositoryDto repository1 = new RepositoryDto.Builder()
                .withOwnerLogin("Owner1")
                .withRepositoryName("Repository1")
                .withBranches(List.of(new BranchDto.Builder().withName("master").withSha("123456").build())).build();
        RepositoryDto repository2 = new RepositoryDto.Builder()
                .withOwnerLogin("Owner1")
                .withRepositoryName("Repository2")
                .withBranches(List.of(new BranchDto.Builder().withName("master").withSha("654321").build())).build();
        repositories.add(repository1);
        repositories.add(repository2);
    }

    @Test
    public void testGetByOwnerLoginShouldReturnRepositories() throws Exception {
        //GIVEN
        Mockito.when(service.getByOwnerLogin("Owner1")).thenReturn(repositories);
        //WHEN
        //THEN
        mockMvc.perform(get("/github/Owner1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].repositoryName", is("Repository1")))
                .andExpect(jsonPath("$[0].ownerLogin", is("Owner1")))
                .andExpect(jsonPath("$[0].branches[0].name", is("master")))
                .andExpect(jsonPath("$[1].repositoryName", is("Repository2")))
                .andExpect(jsonPath("$[1].ownerLogin", is("Owner1")))
                .andExpect(jsonPath("$[1].branches[0].name", is("master")));
    }

    @Test
    public void testGetErrorMessageWhenOwnerDoesNotExist() throws Exception {
        //GIVEN
        Mockito.when(service.getByOwnerLogin("Owner1")).thenThrow(new OwnerNotFoundException(HttpStatus.NOT_FOUND, "Owner not found!!!"));
        //WHEN
        //THEN
        mockMvc.perform(get("/github/Owner1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("404")))
                .andExpect(jsonPath("$.message", is("Owner not found!!!")));
    }
}