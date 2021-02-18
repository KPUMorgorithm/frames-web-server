package org.morgorithm.frames;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FramesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FramesApplication.class, args);
    }

}
