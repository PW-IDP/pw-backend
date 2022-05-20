package com.pweb.backend.controller;


import com.nimbusds.jose.shaded.json.JSONObject;
import com.pweb.backend.model.User;
import com.pweb.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    UserService userService;

    @PostMapping(path = "/save")
    public ResponseEntity<?> save(@RequestHeader(name = "Authorization") String jwt, @RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!this.userService.isValidRequest(request)) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
                if (this.userService.isPresent(identity)) {
                    response.put("message", "User already saved!");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    String email = String.valueOf(request.get("email"));
                    String name = String.valueOf(request.get("name"));

                    User user = new User();
                    user.setIdentity(identity);
                    user.setEmail(email);
                    user.setName(name);

                    userService.save(user);
                    response.put("message", "User saved successfully!");
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
