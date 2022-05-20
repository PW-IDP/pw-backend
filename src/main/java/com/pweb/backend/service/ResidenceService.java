package com.pweb.backend.service;

import com.pweb.backend.model.Residence;
import com.pweb.backend.repository.ResidenceRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ResidenceService {
    private final ResidenceRepository residenceRepository;

    public ResidenceService(ResidenceRepository residenceRepository) {
        this.residenceRepository = residenceRepository;
    }

    public boolean isValidRequest(Map<String, Object> request) {
        return ((request.containsKey("name")) &&
                (request.containsKey("min_capacity")) &&
                (request.containsKey("max_capacity")) &&
                (request.containsKey("address")) &&
                (request.containsKey("county")) &&
                (request.containsKey("city")));
    }

    public boolean isPresent(String name, Long userId) {
        return this.residenceRepository.findByName(name, userId).isPresent();
    }

    public void save(Residence residence) {
        this.residenceRepository.save(residence);
    }
}
