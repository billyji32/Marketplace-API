package com.intuit.cg.backendtechassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendTechAssessmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendTechAssessmentApplication.class, args);
	}
}
