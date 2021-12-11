package com.server.onlineup.repository;

import com.server.onlineup.model.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmail(String email);

    void deleteById(String id);

    Optional<ProfileEntity> findById(String id);

    @Query(value = "Select * from profile p " +
            "JOIN room_ad ra ON p.id = ra.profile_id " +
            "where ra.room_id = ?1 AND p.email LIKE %?2%", nativeQuery = true)
    List<ProfileEntity> findHostsOfRoom(String roomID, String emailHost);
}