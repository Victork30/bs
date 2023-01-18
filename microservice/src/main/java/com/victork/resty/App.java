package com.victork.resty;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@SpringBootApplication
@RestController
public class App {
     
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@PostMapping("/security-check")
	public ResponseEntity<String> handleMessage(@RequestBody String message) {
		System.out.println("security-check " + message);
		return new ResponseEntity<>("Packages checked!", HttpStatus.OK);
	}

	@PostMapping("/packages")
	public ResponseEntity<String> listenForPackages(@RequestBody String packages) throws IOException {
		
		String HEALTHY_HOST = "";
		String HEALTHY_PORT = "";

		String configFilePath = "src/config.properties";
		try	{
			FileInputStream propsInput = new FileInputStream(configFilePath);
			Properties prop = new Properties();
			prop.load(propsInput);
			HEALTHY_HOST = prop.getProperty("HEALTHY_HOST");
			HEALTHY_PORT = prop.getProperty("HEALTHY_PORT");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Send the package list to the other microservice
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(packages, headers);
		HttpEntity<String> response = new HttpEntity<>(new String(), new HttpHeaders());
		response = restTemplate.exchange("http://" + HEALTHY_HOST + ":" + HEALTHY_PORT + "/security-check", HttpMethod.POST, request,
				String.class);

		System.out.println("security-check responce " + response.getBody());
		SecurityChecker securityChecker = new SecurityChecker();
		securityChecker.check(request.getBody());
		// Return a success response
		return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
	}
}
