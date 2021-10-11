package com.galvanize.prodman.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.galvanize.prodman.domain")
@EnableJpaRepositories("com.galvanize.prodman.repository")
@EnableTransactionManagement
public class DomainConfig {
}
