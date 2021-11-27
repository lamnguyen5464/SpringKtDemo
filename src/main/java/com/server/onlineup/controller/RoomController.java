package com.server.onlineup.controller;

import com.server.onlineup.model.entity.RoomEntity;
import com.server.onlineup.model.request.JoinRoomRequest;
import com.server.onlineup.service.business.room.RoomBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomBiz roomBizService;

    @PostMapping("/create-room")
    public ResponseEntity createRoom(@RequestBody RoomEntity room) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleCreateRoom(user, room);
    }
    @GetMapping("/list")
    public ResponseEntity getListRooms() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.getListRooms(user);
    }
    @PostMapping("/add-user")
    public ResponseEntity addUser(@RequestBody JoinRoomRequest joinRoomRequest) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleJoinRoom(user, joinRoomRequest);
    }
    @PostMapping("/add-co-host")
    public ResponseEntity addCoHost(@RequestBody JoinRoomRequest joinRoomRequest) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleAddCoHost(user, joinRoomRequest);
    }
    @PostMapping("/change-host")
    public ResponseEntity changeHost(@RequestBody JoinRoomRequest joinRoomRequest) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleAssignHost(user, joinRoomRequest);
    }

}
