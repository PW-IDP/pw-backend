package com.pweb.backend.repository;

import com.pweb.backend.model.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharingRepository extends JpaRepository<Residence, Long> {
}
