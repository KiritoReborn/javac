package com.canteen.canteen_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CanteenSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanteenSystemApplication.class, args);
	}

}
