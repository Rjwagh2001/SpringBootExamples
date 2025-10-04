package com.rahul.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//before db connection
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class SpringBootRestApIExamplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApIExamplesApplication.class, args);
	}

}
