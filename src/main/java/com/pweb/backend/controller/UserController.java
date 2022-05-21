package com.pweb.backend.controller;


import com.nimbusds.jose.shaded.json.JSONObject;
import com.pweb.backend.model.Sharing;
import com.pweb.backend.model.User;
import com.pweb.backend.service.ResidenceService;
import com.pweb.backend.service.SharingService;
import com.pweb.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    UserService userService;

    @Autowired
    SharingService sharingService;

    @Autowired
    ResidenceService residenceService;

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

    @GetMapping("/statistics/offers")
    public ResponseEntity<?> getOffersStats(@RequestHeader(name = "Authorization") String jwt) {
        try {
            JSONObject response = new JSONObject();
            String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
            Long userId = this.userService.findIdByIdentity(identity);
            response.put("total_helped_people", this.sharingService.getHelpedPeopleByUser(userId));
            Integer totalOffers = this.sharingService.getAllPublishedOffersByUser(userId);
            response.put("total_offers", totalOffers);
            Integer acceptedOffers = this.sharingService.findBookings(userId).size();
            response.put("accepted_offers", acceptedOffers);
            response.put("free_offers", totalOffers - acceptedOffers);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/sharing/delete")
    public ResponseEntity<?> deleteSharing(@RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!request.containsKey("sharing_id")) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                List<Sharing> availableTotalSharings = this.sharingService.findAvailableOffersForAdmin();
                ;
                Long sharingId = Long.parseLong(String.valueOf(request.get("sharing_id")));
                Sharing toDelete = this.sharingService.findById(sharingId);
                if (toDelete == null) {
                    String failureMessage = "This sharing does not exist!";
                    response.put("message", failureMessage);
                } else {
                    for (Sharing userOffer : availableTotalSharings) {
                        if (Objects.equals(userOffer.getId(), sharingId)) {
                            this.sharingService.deleteSharing(sharingId);

                            String successfulMessage = "Sharing successfully deleted.";
                            response.put("message", successfulMessage);
                            return ResponseEntity.status(HttpStatus.OK).body(response);
                        }
                    }
                    String failureMessage = "This sharing could not be identified in the available sharings list!";
                    response.put("message", failureMessage);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/statistics/offers")
    public ResponseEntity<?> getOffersStatsForAdmin() {
        try {
            JSONObject response = new JSONObject();
            response.put("total_helped_people", this.sharingService.getHelpedPeople());
            Integer totalOffers = this.sharingService.getAllOffers();
            response.put("total_offers", totalOffers);
            Integer acceptedOffers = this.sharingService.getAcceptedOffers();
            response.put("accepted_offers", acceptedOffers);
            response.put("free_offers", totalOffers - acceptedOffers);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/statistics/residences")
    public ResponseEntity<?> getResidencesStatsForAdmin() {
        try {
            JSONObject response = new JSONObject();
            response.put("total_residences", this.residenceService.getAllResidences());
            response.put("total_hosts", this.residenceService.getAllHosts());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
