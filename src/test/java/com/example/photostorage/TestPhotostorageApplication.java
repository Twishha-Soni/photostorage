package com.example.photostorage;

import org.springframework.boot.SpringApplication;

public class TestPhotostorageApplication {

	public static void main(String[] args) {
		SpringApplication.from(PhotostorageApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
