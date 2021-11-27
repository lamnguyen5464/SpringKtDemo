package com.server.onlineup.service.database;

import com.server.onlineup.model.entity.RoomEntity;
import com.server.onlineup.repository.RoomRepository;
import com.server.onlineup.service.implementation.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Iterable<RoomEntity> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<RoomEntity> findById(String id) {
        return roomRepository.findById(id);
    }

    @Override
    public Set<RoomEntity> findByInventoryIds(Set<String> inventoryIdList) {
        return roomRepository.findByInventoryIds(inventoryIdList);
    }

    @Override
    public RoomEntity save(RoomEntity room) {
        return roomRepository.save(room);
    }

    @Override
    public void remove(String id) {
        roomRepository.deleteById(id);
    }


}
