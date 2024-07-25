package com.githubrepository.GithubRepository.utils;

import java.util.Random;

public class RandomUtils {

    public static String randomString() {
        return String.valueOf(new Random().nextLong());
    }
}
