package com.harington.kata.bank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@SpringBootApplication
public class JobKataApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobKataApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter(@Value("${frontend.app.url}") String  frontendAppUrl) {
		System.out.println(frontendAppUrl);

		var corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedOrigins(List.of(frontendAppUrl));
		corsConfig.setAllowedHeaders(List.of(
				"Origin", "Access-Control-Allow-Origin",
				"Content-Type", "Accept", "Authorization",
				"Origin, Accept", "X-Request-With",
				"Access-Control-Request-Method",
				"Access-Control-Request-Headers"
		));
		corsConfig.setExposedHeaders(List.of(
				"Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials"
		));
		corsConfig.setAllowedMethods(List.of(
				"GET", "POST", "PUT", "DELETE", "OPTIONS"
		));
		var usrCorsConfig = new UrlBasedCorsConfigurationSource();
		usrCorsConfig.registerCorsConfiguration("/**", corsConfig);
		return new CorsFilter(usrCorsConfig);
	}
}
