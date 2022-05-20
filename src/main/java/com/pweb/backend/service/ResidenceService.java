package com.pweb.backend.service;

import com.pweb.backend.repository.ResidenceRepository;
import org.springframework.stereotype.Service;

@Service
public class ResidenceService {
    private final ResidenceRepository residenceRepository;

    public ResidenceService(ResidenceRepository residenceRepository) {
        this.residenceRepository = residenceRepository;
    }
}
