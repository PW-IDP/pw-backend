package com.pweb.backend.service;

import com.pweb.backend.model.Sharing;
import com.pweb.backend.repository.SharingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SharingService {
    private final SharingRepository sharingRepository;

    public SharingService(SharingRepository sharingRepository) {
        this.sharingRepository = sharingRepository;
    }

    public boolean isValidRequest(Map<String, Object> request) {
        return ((request.containsKey("title")) &&
                (request.containsKey("description")) &&
                (request.containsKey("residence_id")));
    }

    public List<Sharing> findSharingsByResidence(Long residenceId) {
        return this.sharingRepository.findSharingsByResidence(residenceId);
    }

    public void save(Sharing sharing) {
        this.sharingRepository.save(sharing);
    }
}
