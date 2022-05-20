package com.pweb.backend.repository;

import com.pweb.backend.model.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidenceRepository extends JpaRepository<Residence, Long> {
    @Query(value = "SELECT * FROM residences r WHERE r.name = :name AND r.user_id = :user", nativeQuery = true)
    Optional<Residence> findByName(String name, Long user);
}
