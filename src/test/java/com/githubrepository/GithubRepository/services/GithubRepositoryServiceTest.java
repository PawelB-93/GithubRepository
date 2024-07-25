package com.githubrepository.GithubRepository.services;

import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.*;
import com.githubrepository.GithubRepository.transformers.RepositoryTransformer;
import com.githubrepository.GithubRepository.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @InjectMocks
    private GithubRepositoryService githubService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RepositoryTransformer transformer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(githubService, "apiUrl", "https://api.github.com");
    }

    @Test
    public void testRepositoriesShouldBeReturnedWhenOwnerExist() {
        //GIVEN
        final String ownerLogin = "Owner1";
        final Branch branch1 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final List<Branch> branches1 = new ArrayList<>();
        final Repository repository1 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin),false, Collections.emptyList());
        repository1.setBranches(List.of(branch1));
        final List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        final String repositoriesUrl = String.format("https://api.github.com/repos/Owner1/%s/branches", repository1.getName());
        //WHEN
        Mockito.when(this.restTemplate.exchange(Mockito.eq("https://api.github.com/users/Owner1/repos"), eq(HttpMethod.GET), eq(null),
                        Mockito.<ParameterizedTypeReference<List<Repository>>>any()))
                .thenReturn(new ResponseEntity<>(repositories, HttpStatus.OK));
        Mockito.when(this.restTemplate.exchange(Mockito.eq(repositoriesUrl), eq(HttpMethod.GET), eq(null),
                        Mockito.<ParameterizedTypeReference<List<Branch>>>any()))
                .thenReturn(new ResponseEntity<>(branches1, HttpStatus.OK));
        Mockito.when(this.transformer.toRepositoryDto(repository1)).thenReturn(new RepositoryDto.Builder()
                .withRepositoryName(repository1.getName())
                .withOwnerLogin(ownerLogin)
                .withBranches(List.of(new BranchDto.Builder().withName(branch1.getName()).withSha(branch1.getCommit().getSha()).build())).build());
        //THEN
        List<RepositoryDto> result = githubService.getByOwnerLogin("Owner1");
        assertThat(result).isNotNull();
        assertThat(result.get(0).getRepositoryName()).isEqualTo(repository1.getName());
        assertThat(result.get(0).getOwnerLogin()).isEqualTo(ownerLogin);
        assertThat(result.get(0).getBranches().get(0).getName()).isEqualTo(branch1.getName());
        assertThat(result.get(0).getBranches().get(0).getSha()).isEqualTo(branch1.getCommit().getSha());
    }

    @Test
    public void testRepositoriesShouldNotBeReturnedWhenRepositoryIsFork() {
        //GIVEN
        final String ownerLogin = "Owner1";
        final Branch branch1 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final Branch branch2 = new Branch(RandomUtils.randomString(), new Commit(RandomUtils.randomString()));
        final List<Branch> branches = new ArrayList<>();
        final Repository repository1 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin),true, Collections.emptyList());
        final Repository repository2 = new Repository(RandomUtils.randomString(), new Owner(ownerLogin),false, Collections.emptyList());
        repository1.setBranches(List.of(branch1));
        repository2.setBranches(List.of(branch2));
        final List<Repository> repositories = new ArrayList<>();
        repositories.add(repository1);
        repositories.add(repository2);
        final String branchesUrl = String.format("https://api.github.com/repos/%s/%s/branches", ownerLogin, repository2.getName());
        //WHEN
        Mockito.when(this.restTemplate.exchange(Mockito.eq("https://api.github.com/users/Owner1/repos"), eq(HttpMethod.GET), eq(null),
                        Mockito.<ParameterizedTypeReference<List<Repository>>>any()))
                .thenReturn(new ResponseEntity<>(repositories, HttpStatus.OK));
        Mockito.when(this.restTemplate.exchange(Mockito.eq(branchesUrl), eq(HttpMethod.GET), eq(null),
                        Mockito.<ParameterizedTypeReference<List<Branch>>>any()))
                .thenReturn(new ResponseEntity<>(branches, HttpStatus.OK));
        Mockito.when(this.transformer.toRepositoryDto(repository2)).thenReturn(new RepositoryDto.Builder()
                .withRepositoryName(repository2.getName())
                .withOwnerLogin(ownerLogin)
                .withBranches(List.of(new BranchDto.Builder().withName(branch2.getName()).withSha(branch2.getCommit().getSha()).build())).build());
        //THEN
        List<RepositoryDto> result = githubService.getByOwnerLogin("Owner1");
        assertThat(result).isNotNull();
        assertThat(result.get(0).getRepositoryName()).isEqualTo(repository2.getName());
        assertThat(result.get(0).getOwnerLogin()).isEqualTo(ownerLogin);
        assertThat(result.get(0).getBranches().get(0).getName()).isEqualTo(branch2.getName());
        assertThat(result.get(0).getBranches().get(0).getSha()).isEqualTo(branch2.getCommit().getSha());
    }

    @Test
    public void testExceptionShouldBeThrownWhenOwnerDoesNotExist() {
        //GIVEN
        //WHEN
        Mockito.when(this.restTemplate.exchange(Mockito.eq("https://api.github.com/users/Owner1/repos"), eq(HttpMethod.GET),
                eq(null), Mockito.<ParameterizedTypeReference<List<Repository>>>any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        //THEN
        Assertions.assertThrows(OwnerNotFoundException.class, () ->
                this.githubService.getByOwnerLogin("Owner1"));
    }
}