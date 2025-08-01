package org.vetclinic.recommendationservice;

import org.springframework.boot.SpringApplication;

public class TestUniSswRecommendationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(RecommendationServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
