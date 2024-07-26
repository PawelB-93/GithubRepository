package com.githubrepository.GithubRepository.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.*;
import com.githubrepository.GithubRepository.transformers.RepositoryTransformer;
import com.githubrepository.GithubRepository.utils.RandomUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @InjectMocks
    private GithubRepositoryService githubService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static MockWebServer webServer;

    @Mock
    private RepositoryTransformer transformer;

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        WebClient webClient = webClientBuilder.build();
        ReflectionTestUtils.setField(githubService, "webClient", webClient);
        ReflectionTestUtils.setField(githubService, "apiUrl", webServer.url("/").toString());
    }

    @BeforeAll
    static void setUpWeb() throws IOException {
        webServer = new MockWebServer();
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        webServer.shutdown();
    }

    @Test
    void testRepositoriesShouldBeReturnedWhenOwnerExist() throws Exception {
        //GIVEN
        final String ownerLogin = "Owner1";
        final Branch branch1 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final Repository repository1 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin), false, List.of(branch1));
        final List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        webServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(repositories))
                .addHeader("Content-Type", "application/json"));
        webServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(List.of(branch1)))
                .addHeader("Content-Type", "application/json"));
        when(this.transformer.toRepositoryDto(repository1)).thenReturn(
                new RepositoryDto(
                        repository1.name(),
                        ownerLogin,
                        List.of(new BranchDto(branch1.name(), branch1.commit().sha()))
                ));
        //WHEN
        List<RepositoryDto> result = githubService.getByOwnerLogin("Owner1").block();
        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getFirst().repositoryName()).isEqualTo(repository1.name());
        assertThat(result.getFirst().ownerLogin()).isEqualTo(ownerLogin);
        assertThat(result.getFirst().branches().getFirst().name()).isEqualTo(branch1.name());
        assertThat(result.getFirst().branches().getFirst().sha()).isEqualTo(branch1.commit().sha());
    }

    @Test
    void testRepositoriesShouldNotBeReturnedWhenRepositoryIsFork() throws Exception {
        //GIVEN
        final String ownerLogin = "Owner1";
        final Branch branch1 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final Repository repository1 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin), true, List.of(branch1));
        final Branch branch2 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final Repository repository2 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin), false, List.of(branch2));
        final List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);
        webServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(repositories))
                .addHeader("Content-Type", "application/json"));
        webServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(List.of(branch2)))
                .addHeader("Content-Type", "application/json"));
        when(this.transformer.toRepositoryDto(repository2)).thenReturn(
                new RepositoryDto(
                        repository2.name(),
                        ownerLogin,
                        List.of(new BranchDto(branch2.name(), branch2.commit().sha()))
                ));
        //WHEN
        List<RepositoryDto> result = githubService.getByOwnerLogin("Owner1").block();
        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getFirst().repositoryName()).isEqualTo(repository2.name());
        assertThat(result.getFirst().ownerLogin()).isEqualTo(ownerLogin);
        assertThat(result.getFirst().branches().getFirst().name()).isEqualTo(branch2.name());
        assertThat(result.getFirst().branches().getFirst().sha()).isEqualTo(branch2.commit().sha());
    }

    @Test
    public void testExceptionShouldBeThrownWhenOwnerDoesNotExist() {
        //GIVEN
        webServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));
        //THEN
        Assertions.assertThrows(OwnerNotFoundException.class, () ->
                this.githubService.getByOwnerLogin("Owner1").block());
    }
}