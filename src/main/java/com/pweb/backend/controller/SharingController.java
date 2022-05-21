package com.pweb.backend.controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.pweb.backend.model.Residence;
import com.pweb.backend.model.Sharing;
import com.pweb.backend.model.User;
import com.pweb.backend.service.ResidenceService;
import com.pweb.backend.service.SharingService;
import com.pweb.backend.service.UserService;
import com.pweb.backend.utils.TimeFormatter;
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
@RequestMapping(path = "/sharing")
public class SharingController {
    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    SharingService sharingService;

    @Autowired
    ResidenceService residenceService;

    @Autowired
    UserService userService;

    @PostMapping(path = "/add")
    public ResponseEntity<?> addSharing(@RequestHeader(name = "Authorization") String jwt, @RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!this.sharingService.isValidRequest(request)) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
                Long userId = this.userService.findIdByIdentity(identity);
                String title = String.valueOf(request.get("title"));
                String description = String.valueOf(request.get("description"));

                Long residenceId = Long.parseLong(String.valueOf(request.get("residence_id")));
                List<Residence> userResidences = this.residenceService.findAllResidencesFromUser(userId);

                boolean found = false;
                for (Residence residence : userResidences) {
                    if (Objects.equals(residence.getUser().getId(), userId)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    String failureMessage = "This residence does not belong to you!";
                    response.put("message", failureMessage);
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
                }

                List<Sharing> sharingsForThatResidence = this.sharingService.findSharingsByResidence(residenceId);
                for (Sharing sharing : sharingsForThatResidence) {
                    if (sharing.getStartDateTime() == null && sharing.getEndDateTime() == null) {
                        String failureMessage = "Already added an offer for this residence, but nobody took it yet!";
                        response.put("message", failureMessage);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else if (sharing.getStartDateTime() != null && sharing.getEndDateTime() == null) {
                        String failureMessage = "Residence occupied!";
                        response.put("message", failureMessage);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    } else if (sharing.getStartDateTime() == null && sharing.getEndDateTime() != null) {
                        String failureMessage = "Invalid format! Missing start date & end date completed!";
                        response.put("message", failureMessage);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                }

                Sharing sharingToAdd = new Sharing();
                sharingToAdd.setTitle(title);
                sharingToAdd.setDescription(description);
                Residence residence = this.residenceService.findById(residenceId);
                sharingToAdd.setResidence(residence);
                User nil = this.userService.findByIdentity("nil");
                sharingToAdd.setGuest(nil);

                this.sharingService.save(sharingToAdd);
                residence.getSharings().add(sharingToAdd);
                response.put("message", "Sharing created!");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAvailableOffers")
    public ResponseEntity<?> getAvailableOffers(@RequestHeader(name = "Authorization") String jwt) {
        try {
            JSONObject response = new JSONObject();
            List<JSONObject> sharings = new ArrayList<>();
            String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
            Long userId = this.userService.findIdByIdentity(identity);
            List<Sharing> availableSharings = this.sharingService.findAvailableSharings(userId);
            for (Sharing availableSharing : availableSharings) {
                JSONObject sharingResponse = new JSONObject();
                sharingResponse.put("sharing_id", availableSharing.getId());
                sharingResponse.put("title", availableSharing.getTitle());
                sharingResponse.put("description", availableSharing.getDescription());
                sharingResponse.put("name", availableSharing.getResidence().getUser().getName());
                sharingResponse.put("email", availableSharing.getResidence().getUser().getEmail());
                sharingResponse.put("address", availableSharing.getResidence().getAddress());
                sharingResponse.put("county", availableSharing.getResidence().getCounty());
                sharingResponse.put("city", availableSharing.getResidence().getCity());
                sharingResponse.put("min_capacity", availableSharing.getResidence().getMinCapacity());
                sharingResponse.put("max_capacity", availableSharing.getResidence().getMaxCapacity());
                sharings.add(sharingResponse);
            }
            response.put("avaialable_offers", sharings);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(@RequestHeader(name = "Authorization") String jwt) {
        try {
            JSONObject response = new JSONObject();
            List<JSONObject> bookings = new ArrayList<>();
            String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
            Long userId = this.userService.findIdByIdentity(identity);
            List<Sharing> userBookings = this.sharingService.findBookings(userId);
            for (Sharing booking : userBookings) {
                JSONObject bookingResponse = new JSONObject();
                bookingResponse.put("sharing_id", booking.getId());
                bookingResponse.put("title", booking.getTitle());
                bookingResponse.put("description", booking.getDescription());
                bookingResponse.put("name", booking.getResidence().getUser().getName());
                bookingResponse.put("email", booking.getResidence().getUser().getEmail());
                bookingResponse.put("address", booking.getResidence().getAddress());
                bookingResponse.put("county", booking.getResidence().getCounty());
                bookingResponse.put("city", booking.getResidence().getCity());
                bookingResponse.put("capacity", booking.getCapacity());
                bookingResponse.put("start_datetime", booking.getStartDateTime());
                if (booking.getEndDateTime() != null) {
                    bookingResponse.put("end_datetime", booking.getEndDateTime());
                }
                bookings.add(bookingResponse);
            }
            response.put("bookings", bookings);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/accept")
    public ResponseEntity<?> accept(@RequestHeader(name = "Authorization") String jwt, @RequestBody Map<String, Object> request) {
        try {
            JSONObject response = new JSONObject();
            if (!request.containsKey("sharing_id") || !request.containsKey("capacity")) {
                String failureMessage = "Missing required credentials!";
                response.put("message", failureMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                String identity = jwtDecoder.decode(jwt.substring(7)).getSubject();
                User guest = this.userService.findByIdentity(identity);
                Integer capacity = Integer.parseInt(String.valueOf(request.get("capacity")));
                Long id = Long.parseLong(String.valueOf(request.get("sharing_id")));
                Sharing toAccept = this.sharingService.findById(id);

                if (capacity < toAccept.getResidence().getMinCapacity() || capacity > toAccept.getResidence().getMaxCapacity()) {
                    String failureMessage = "Invalid capacity!";
                    response.put("message", failureMessage);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                toAccept.setCapacity(capacity);

                java.util.Date date = TimeFormatter.StringToDate();
                java.sql.Timestamp startDateTime = new java.sql.Timestamp(date.getTime());
                toAccept.setStartDateTime(startDateTime);

                toAccept.setGuest(guest);

                this.sharingService.save(toAccept);
                response.put("message", "Sharing accepted!");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
