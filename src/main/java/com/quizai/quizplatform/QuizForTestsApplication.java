package com.quizai.quizplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class QuizForTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizForTestsApplication.class, args);
	}

}
