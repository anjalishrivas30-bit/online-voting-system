package com.internship.voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point of the Online Voting System.
 * - @SpringBootApplication : component scan + auto-configuration
 * - @EnableScheduling      : lets ElectionStatusScheduler update election status every minute
 * - extends SpringBootServletInitializer : needed because JSP requires WAR packaging
 */
@SpringBootApplication
@EnableScheduling
public class OnlineVotingApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OnlineVotingApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineVotingApplication.class, args);
    }
}
