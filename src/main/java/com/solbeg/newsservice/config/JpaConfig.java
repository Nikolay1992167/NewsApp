package com.solbeg.newsservice.config;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        value = "com.solbeg.newsservice.repository",
        repositoryBaseClass = BaseJpaRepositoryImpl.class)
public class JpaConfig {
}