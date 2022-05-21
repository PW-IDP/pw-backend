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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
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

    @GetMapping(path = "/get")
    public ResponseEntity<?> getResidence(@RequestHeader(name = "Authorization") String jwt) {
        try {
            JSONObject response = new JSONObject();
            String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
            Long userId = this.userService.findIdByIdentity(identity);
            List<JSONObject> residences = new ArrayList<>();
            List<Residence> userResidences = this.residenceService.findAllResidencesFromUser(userId);
            for (Residence residence : userResidences) {
                JSONObject residenceResponse = new JSONObject();
                residenceResponse.put("residence_id", residence.getId());
                residenceResponse.put("name", residence.getName());
                residenceResponse.put("address", residence.getAddress());
                residenceResponse.put("county", residence.getCounty());
                residenceResponse.put("city", residence.getCity());
                residenceResponse.put("min_capacity", residence.getMinCapacity());
                residenceResponse.put("max_capacity", residence.getMaxCapacity());
                residences.add(residenceResponse);
            }
            response.put("residences", residences);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> deleteResidence(@RequestHeader(name = "Authorization") String jwt, @RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!request.containsKey("residence_id")) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
                Long userId = this.userService.findIdByIdentity(identity);
                Long residenceId = Long.parseLong(String.valueOf(request.get("residence_id")));
                Residence toDelete = this.residenceService.findById(residenceId);

                if (toDelete == null) {
                    String failureMessage = "This residence does not exist!";
                    response.put("message", failureMessage);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    if (!Objects.equals(toDelete.getUser().getId(), userId)) {
                        String failureMessage = "This property does not belong to you!";
                        response.put("message", failureMessage);
                        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
                    } else {
                        this.residenceService.deleteResidence(residenceId);

                        String successfulMessage = "Residence successfully deleted.";
                        response.put("message", successfulMessage);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }

            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
