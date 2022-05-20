package com.pweb.backend.controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.pweb.backend.model.Residence;
import com.pweb.backend.service.ResidenceService;
import com.pweb.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/residence")
public class ResidenceController {

    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    ResidenceService residenceService;

    @Autowired
    UserService userService;

    @PostMapping(path = "/add")
    public ResponseEntity<?> addResidence(@RequestHeader(name = "Authorization") String jwt, @RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!this.residenceService.isValidRequest(request)) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
                String name = String.valueOf(request.get("name"));
                Long userId = this.userService.findIdByIdentity(identity);
                if (this.residenceService.isPresent(name, userId)) {
                    response.put("message", "Name already taken for another residence of yours!");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } else {
                    Integer minCapacity = Integer.parseInt(String.valueOf(request.get("min_capacity")));
                    Integer maxCapacity = Integer.parseInt(String.valueOf(request.get("max_capacity")));
                    String address = String.valueOf(request.get("address"));
                    String county = String.valueOf(request.get("county"));
                    String city = String.valueOf(request.get("city"));

                    Residence residence = new Residence();
                    residence.setUser(this.userService.findById(userId));
                    residence.setMinCapacity(minCapacity);
                    residence.setMaxCapacity(maxCapacity);
                    residence.setName(name);
                    residence.setAddress(address);
                    residence.setCounty(county);
                    residence.setCity(city);

                    this.residenceService.save(residence);
                    response.put("residence_id", residence.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);

                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
