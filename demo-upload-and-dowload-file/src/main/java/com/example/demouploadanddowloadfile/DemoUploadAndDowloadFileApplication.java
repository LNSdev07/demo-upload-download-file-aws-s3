package com.example.demouploadanddowloadfile;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoUploadAndDowloadFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoUploadAndDowloadFileApplication.class, args);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

}
