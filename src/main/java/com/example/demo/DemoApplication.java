package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    @RequestMapping("/")
    String home(){
        return "hello world !";
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
