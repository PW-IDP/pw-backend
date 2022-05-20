package com.pweb.backend.test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

public class APIController {

    @Autowired
    JwtDecoder jwtDecoder;


    @GetMapping(value = "/public")
    public Integer publicEndpoint() {
        return 0;
    }

    @GetMapping(value = "/private")
    public Integer privateEndpoint(@RequestHeader(name = "Authorization") String jwt) {
        System.out.println(jwtDecoder.decode(jwt.substring(7)).getSubject());
        return 1;
    }

    @GetMapping(value = "/private-scoped")
    public Integer privateScopedEndpoint() {
        return 2;
    }



}