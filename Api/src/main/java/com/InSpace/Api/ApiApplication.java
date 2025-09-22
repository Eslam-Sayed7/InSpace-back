package com.InSpace.Api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.InSpace.Api")
@EntityScan(basePackages = "com.InSpace.Api.domain")
@EnableJpaRepositories(basePackages = "com.InSpace.Api.infra.repository")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApiApplication.class);
		// app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>)
		// event -> {
		// System.out.println(">>> Property sources: " +
		// event.getEnvironment().getPropertySources());
		// String dbName = event.getEnvironment().getProperty("DB_NAME");
		// String dbUserName = event.getEnvironment().getProperty("DB_USERNAME");
		// String dbPassword = event.getEnvironment().getProperty("DB_PASSWORD");
		// String dbDriver = event.getEnvironment().getProperty("DB_DRIVER");
		// System.out.println(">>> DB_NAME resolved by Spring = " + dbName);
		// System.out.println(">>> DB_USERNAME resolved by Spring = " + dbUserName);
		// System.out.println(">>> DB_PASSWORD resolved by Spring = " + dbPassword);
		// System.out.println(">>> DB_DRIVER resolved by Spring = " + dbDriver);
		// });
		app.run(args);
	}

}
