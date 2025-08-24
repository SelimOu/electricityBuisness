package com.example.electricitybusiness.security;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @RequestMapping(path = "**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("http://localhost:4200");
        headers.setAccessControlAllowMethods(Arrays.asList(org.springframework.http.HttpMethod.GET,
                org.springframework.http.HttpMethod.POST, org.springframework.http.HttpMethod.PUT,
                org.springframework.http.HttpMethod.DELETE, org.springframework.http.HttpMethod.OPTIONS));
        headers.setAccessControlAllowHeaders(Arrays.asList("*"));
        headers.setAccessControlAllowCredentials(true);
        return new ResponseEntity<>(null, headers, HttpStatus.OK);
    }
}
