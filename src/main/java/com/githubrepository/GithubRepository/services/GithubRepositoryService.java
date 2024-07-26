package com.githubrepository.GithubRepository.services;

import com.githubrepository.GithubRepository.exceptions.OwnerNotFoundException;
import com.githubrepository.GithubRepository.models.Branch;
import com.githubrepository.GithubRepository.models.Repository;
import com.githubrepository.GithubRepository.models.RepositoryDto;
import com.githubrepository.GithubRepository.transformers.RepositoryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubRepositoryService {

    private static final String REPOSITORIES_URL = "/users/%s/repos";
    private static final String BRANCHES_URL = "/repos/%s/%s/branches";

    @Value("${github.api.url}")
    private String apiUrl;

    private final RepositoryTransformer transformer;
    private final WebClient webClient;

    @Autowired
    public GithubRepositoryService(final RepositoryTransformer transformer, WebClient webClient) {
        this.transformer = transformer;
        this.webClient = webClient;
    }

    public Mono<List<RepositoryDto>> getByOwnerLogin(final String ownerLogin) {
        return fetchRepositoriesByOwnerLogin(ownerLogin)
                .flatMapMany(Flux::fromIterable)
                .filter(repository -> !repository.fork())
                .flatMap(repository ->
                        fetchBranchesByRepository(ownerLogin, repository.name())
                                .map(branches -> new Repository(
                                        repository.name(), repository.owner(), repository.fork(), branches)))
                .collectList()
                .onErrorMap(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return new OwnerNotFoundException(e.getStatusCode(), "Owner not found!!!");
                    }
                    return e;
                })
                .map(repositories -> repositories.stream()
                        .map(transformer::toRepositoryDto)
                        .collect(Collectors.toList()));
    }

    private Mono<List<Repository>> fetchRepositoriesByOwnerLogin(final String ownerLogin) {
        final String repositoriesUrl = String.format(apiUrl + REPOSITORIES_URL, ownerLogin);
        return this.webClient.get()
                .uri(repositoriesUrl)
                .retrieve()
                .bodyToFlux(Repository.class)
                .collectList();
    }

    private Mono<List<Branch>> fetchBranchesByRepository(final String ownerLogin, final String repositoryName) {
        final String branchesUrl = String.format(apiUrl + BRANCHES_URL, ownerLogin, repositoryName);
        return this.webClient.get()
                .uri(branchesUrl)
                .retrieve()
                .bodyToFlux(Branch.class)
                .collectList();
    }
}
