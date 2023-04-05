package com.ks.sd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SvnDeployApplication {

	public static void main(String[] args) {
		SpringApplication.run(SvnDeployApplication.class, args);
	}

}
