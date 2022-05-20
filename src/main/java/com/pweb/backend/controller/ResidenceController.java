package com.pweb.backend.controller;

import com.pweb.backend.service.ResidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/residence")
public class ResidenceController {

    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    ResidenceService residenceService;
}
