package com.server.onlineup.controller;

import com.server.onlineup.model.entity.RoomEntity;
import com.server.onlineup.model.request.JoinRoomRequest;
import com.server.onlineup.model.request.SearchHostRequest;
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

    @GetMapping("/room-user")
    public ResponseEntity getListRoomsAsUser() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.getListRoomsAsUser(user);
    }

    @GetMapping("/room-admin")
    public ResponseEntity getListRoomsAsAdmin() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.getListRoomsAsAdmin(user);
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

    //Công Tài code :))) cần kiểm tra lại kĩ :P
    @PostMapping("/search-host")
    public ResponseEntity searchHost(@RequestBody SearchHostRequest searchHostRequest) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleSearchHost(user, searchHostRequest.roomID, searchHostRequest.emailHost);
    }

    @PostMapping("/search-room")
    public ResponseEntity searchRoom(@RequestBody String roomName) {
        return roomBizService.handleSearchRoom(roomName);
    }

    @PostMapping("/update-status")
    public ResponseEntity updateRoomStatus(@RequestBody RoomEntity roomEntity) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return roomBizService.handleUpdateStatusRoom(user, roomEntity);
    }

    @PostMapping("/detail")
    public ResponseEntity showRoomDetail(@RequestBody String roomID) {
        return roomBizService.showRoomDetail(roomID);
    }
}
