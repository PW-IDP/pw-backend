package com.pweb.backend.service;

import com.pweb.backend.model.Sharing;
import com.pweb.backend.repository.SharingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<Sharing> findAvailableSharings(Long userId) {
        List<Sharing> totalSharings = this.sharingRepository.findAll();
        List<Sharing> toReturn = new ArrayList<>();
        for (Sharing sharing : totalSharings) {
            if (!Objects.equals(sharing.getResidence().getUser().getId(), userId)) {
                if (sharing.getStartDateTime() == null) {
                    toReturn.add(sharing);
                }
            }
        }
        return toReturn;
    }

    public List<Sharing> findBookings(Long userId) {
        return this.sharingRepository.findBookings(userId);
    }

    public Sharing findById(Long id) {
        return this.sharingRepository.findById(id).orElseThrow();
    }

    public void save(Sharing sharing) {
        this.sharingRepository.save(sharing);
    }
}
