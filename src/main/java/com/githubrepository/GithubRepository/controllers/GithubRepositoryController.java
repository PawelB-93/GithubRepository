package com.githubrepository.GithubRepository.controllers;

import com.githubrepository.GithubRepository.models.RepositoryDto;
import com.githubrepository.GithubRepository.services.GithubRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GithubRepositoryController {

    private final GithubRepositoryService service;

    @Autowired
    public GithubRepositoryController(final GithubRepositoryService service) {
        this.service = service;
    }

    @GetMapping("/{ownerLogin}")
    public Mono<ResponseEntity<List<RepositoryDto>>> getAllRepositories(@PathVariable String ownerLogin) {
        return service.getByOwnerLogin(ownerLogin).map(repos -> ResponseEntity.ok().body(repos));
    }
}
