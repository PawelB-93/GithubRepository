package com.githubrepository.GithubRepository.services;

import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.Branch;
import com.githubrepository.GithubRepository.models.Repository;
import com.githubrepository.GithubRepository.models.RepositoryDto;
import com.githubrepository.GithubRepository.transformers.RepositoryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubRepositoryService {

    private static final String REPOSITORIES_URL = "/users/%s/repos";
    private static final String BRANCHES_URL = "/repos/%s/%s/branches";

    @Value("${github.api.url}")
    private String apiUrl;

    private final RepositoryTransformer transformer;
    private final RestTemplate restTemplate;

    @Autowired
    public GithubRepositoryService(final RepositoryTransformer transformer, final RestTemplate restTemplate) {
        this.transformer = transformer;
        this.restTemplate = restTemplate;
    }

    public List<RepositoryDto> getByOwnerLogin(final String ownerLogin) {
        List<Repository> repositories = fetchRepositoriesByOwnerLogin(ownerLogin).stream()
                .filter(repository -> !repository.fork())
                .map(repository -> new Repository(
                        repository.name(), repository.owner(), repository.fork(), fetchBranchesByRepository(ownerLogin, repository.name())))
                .toList();
        return repositories.stream().map(transformer::toRepositoryDto).collect(Collectors.toList());
    }


    private List<Repository> fetchRepositoriesByOwnerLogin(final String ownerLogin) {
        final String repositoriesUrl = String.format(apiUrl + REPOSITORIES_URL, ownerLogin);
        try {
            return this.restTemplate.exchange(repositoriesUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Repository>>() {
            }).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new OwnerNotFoundException(e.getStatusCode(), "Owner not found!!!");
            }
            throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }

    private List<Branch> fetchBranchesByRepository(final String ownerLogin, final String repositoryName) {
        final String branchesUrl = String.format(apiUrl + BRANCHES_URL, ownerLogin, repositoryName);
        return this.restTemplate.exchange(branchesUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Branch>>() {
        }).getBody();
    }
}
