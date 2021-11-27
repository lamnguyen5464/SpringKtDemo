package com.server.onlineup.repository;

import com.server.onlineup.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    Optional<RoomEntity> findById(String id);

    void deleteById(String id);

    @Query( "select o from RoomEntity o where id in :ids" )
    Set<RoomEntity> findByInventoryIds(@Param("ids") Set<String> inventoryIdList);
}
