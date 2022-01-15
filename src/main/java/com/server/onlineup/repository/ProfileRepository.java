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

//    @Query(value = "SELECT email, full_name " +
//            "FROM profile p " +
//            "LEFT JOIN e.room_admin ra " +
//            "WHERE room_id = ?1 AND email LIKE %?2%", nativeQuery = true)
////    @Query(value = "SELECT p.id FROM profile p", nativeQuery = true)
//    List<ProfileEntity> findHostsOfRoom(String roomID, String emailHost);
}