package com.server.onlineup.service.implementation;


import com.server.onlineup.model.entity.RoomEntity;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface IRoomService extends IGeneralService<RoomEntity> {
    Optional<RoomEntity> findById(String id);
    Set<RoomEntity> findByInventoryIds(Set<String> inventoryIdList);

}
