package com.sunbasedata.intern.customer_table;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class CustomerTableApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerTableApplication.class, args);
	}

}
