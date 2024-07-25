package com.githubrepository.GithubRepository.services;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@WireMockTest(httpPort = 8181)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GithubRepositoryServiceIT {

    @Autowired
    private MockMvc mockMvc;

    private final String ownerLogin = "repositoryOwner";

    @Test
    public void testGetAllRepositories() throws Exception {
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
        this.mockMvc.perform(get("/github/repositoryOwner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("repositoryOwner"))
                .andExpect(jsonPath("$[0].name").value("repository1"));
    }

    @Test
    public void testRepositoriesShouldNotBeReturnedWhenRepositoryIsFork() throws Exception {
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
        this.mockMvc.perform(get("/github/repositoryOwner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void testGetAllRepositories_UserNotFound() throws Exception {
        //GIVEN
        stubFor(WireMock.get(urlEqualTo("/users/" + this.ownerLogin + "/repos"))
                .willReturn(aResponse().withStatus(404)));
        //WHEN
        //THEN
        this.mockMvc.perform(get("/github/repositoryOwner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("404")))
                .andExpect(jsonPath("$.message", is("Owner not found!!!")));
    }
}