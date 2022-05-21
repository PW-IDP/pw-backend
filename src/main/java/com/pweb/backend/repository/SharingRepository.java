package com.pweb.backend.repository;

import com.pweb.backend.model.Sharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharingRepository extends JpaRepository<Sharing, Long> {
    @Query(value = "SELECT * FROM sharings s WHERE s.residence_id = :residence", nativeQuery = true)
    List<Sharing> findSharingsByResidence(Long residence);

    @Query(value = "SELECT * FROM sharings s WHERE s.user_id = :guest AND s.start_datetime IS NOT NULL", nativeQuery = true)
    List<Sharing> findBookings(Long guest);

    @Modifying
    @Query(value = "DELETE FROM sharings s WHERE s.sharing_id = :sharing", nativeQuery = true)
    void deleteSharing(Long sharing);
}
