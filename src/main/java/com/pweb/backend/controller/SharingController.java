package com.pweb.backend.controller;

import com.pweb.backend.service.SharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/sharing")
public class SharingController {
    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    SharingService sharingService;
}
