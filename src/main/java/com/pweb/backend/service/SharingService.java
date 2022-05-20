package com.pweb.backend.service;

import com.pweb.backend.repository.SharingRepository;
import org.springframework.stereotype.Service;

@Service
public class SharingService {
    private final SharingRepository sharingRepository;

    public SharingService(SharingRepository sharingRepository) {
        this.sharingRepository = sharingRepository;
    }
}
