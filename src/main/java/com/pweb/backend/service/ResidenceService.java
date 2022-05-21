package com.pweb.backend.service;

import com.pweb.backend.model.Residence;
import com.pweb.backend.repository.ResidenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

    public List<Residence> findAllResidencesFromUser(Long userId) {
        return this.residenceRepository.findAllResidencesFromUser(userId).orElseThrow();
    }

    public Residence findById(Long id) {
        return this.residenceRepository.findById(id).orElse(null);
    }

    public Integer getAllResidences() {
        return this.residenceRepository.findAll().size();
    }

    public Integer getAllHosts() {
        List<Residence> totalResidences = this.residenceRepository.findAll();
        List<Long> users = new ArrayList<>();
        for (Residence residence : totalResidences) {
            users.add(residence.getUser().getId());
        }
        return new ArrayList<>(new LinkedHashSet<>(users)).size();
    }

    @Transactional
    public void deleteResidence(Long id) {
        this.residenceRepository.deleteResidence(id);
    }

    public void save(Residence residence) {
        this.residenceRepository.save(residence);
    }
}
