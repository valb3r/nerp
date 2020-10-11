package com.valb3r.nerp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableNeo4jRepositories
@EnableTransactionManagement
@SpringBootApplication
public class NerpApplication {

	public static void main(String[] args) {
		SpringApplication.run(NerpApplication.class, args);
	}
}
