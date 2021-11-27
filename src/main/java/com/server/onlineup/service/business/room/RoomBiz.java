package com.server.onlineup.service.business.room;

import com.server.onlineup.common.constant.AuthenticationEnum;
import com.server.onlineup.common.constant.RoleEnum;
import com.server.onlineup.common.constant.RoomEnum;
import com.server.onlineup.common.exception.APIException;
import com.server.onlineup.common.response.BaseResponse;
import com.server.onlineup.model.entity.ProfileEntity;
import com.server.onlineup.model.entity.RoomAdminEntity;
import com.server.onlineup.model.entity.RoomEntity;
import com.server.onlineup.model.entity.RoomUserEntity;
import com.server.onlineup.model.request.JoinRoomRequest;
import com.server.onlineup.model.response.RoomResponse;
import com.server.onlineup.service.database.ProfileService;
import com.server.onlineup.service.database.RoomAdminService;
import com.server.onlineup.service.database.RoomService;
import com.server.onlineup.service.database.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomBiz {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomAdminService roomAdminService;
    @Autowired
    private RoomUserService roomUserService;
    @Autowired
    private ProfileService profileService;

    public ResponseEntity handleCreateRoom(UserDetails user, RoomEntity room) {
        // Load userDetail to profileUser
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        if(!currentUser.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(AuthenticationEnum.INVALID_EMAIL));
        }

        // Add current user to admin
        room.addHost(currentUser.get());
        // Set room host name
        room.setHostName(currentUser.get().getFullName());
        // Create room entity
        roomService.save(room);
        return BaseResponse.Builder()
                .addMessage(RoomEnum.CREATE_SUCCESS)
                .build();
    }
    public boolean isUserInRoom(RoomEntity room, String userId){
        for(RoomUserEntity user : room.getUserList()){
            if (user.getProfileId().equals(userId)){
                return true;
            }
        }
        for(RoomAdminEntity user : room.getAdminList()){
            if (user.getProfileId().equals(userId)){
                return true;
            }
        }
        return false;
    }
    public boolean isUserAsAdmin(RoomEntity room, String userId){
        for(RoomAdminEntity user : room.getAdminList()){
            if (user.getProfileId().equals(userId)){
                return true;
            }
        }
        return false;
    }
    public boolean isHostInRoom(RoomEntity room, String adminId){
        for(RoomAdminEntity user : room.getAdminList()){
            if (user.getProfileId().equals(adminId) && (user.getRole()== RoleEnum.HOST)){
                return true;
            }
        }
        return false;
    }
    public boolean isAdminInRoom(RoomEntity room, String adminId){
        for(RoomAdminEntity user : room.getAdminList()){
            if (user.getProfileId().equals(adminId)){
                return true;
            }
        }
        return false;
    }

    public ResponseEntity handleJoinRoom(UserDetails admin, JoinRoomRequest joinRoomRequest) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(admin.getUsername());

        Optional<RoomEntity> roomEntity = roomService.findById(joinRoomRequest.getRoomId());
        if (!roomEntity.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if(!isAdminInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if(!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if(isUserInRoom(roomEntity.get(), user.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_ALREADY_IN_ROOM));
        }

        roomEntity.get().addUser(user.get());
        // Save room
        roomService.save(roomEntity.get());
        return BaseResponse.Builder()
                .addMessage(RoomEnum.JOIN_SUCCESS)
                .build();
    }

    public ResponseEntity handleAddCoHost(UserDetails host, JoinRoomRequest joinRoomRequest) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(host.getUsername());

        Optional<RoomEntity> roomEntity = roomService.findById(joinRoomRequest.getRoomId());
        if (!roomEntity.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if(!isAdminInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if(!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if(isUserAsAdmin(roomEntity.get(), user.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_ALREADY_CO_HOST));
        } else if(!isUserInRoom(roomEntity.get(), user.get().getId())){
            roomEntity.get().addCoHost(user.get());
        }else {
            roomEntity.get().removeUser(user.get().getId());
            roomEntity.get().addCoHost(user.get());
        }
        // Save room
        roomService.save(roomEntity.get());
        return BaseResponse.Builder()
                .addMessage(RoomEnum.UPDATE_CO_HOST_SUCCESS)
                .build();
    }
    public ResponseEntity handleAssignHost(UserDetails host, JoinRoomRequest joinRoomRequest) {
        // Get host profile
        Optional<ProfileEntity> currentUser = profileService.findByUsername(host.getUsername());

        Optional<RoomEntity> roomEntity = roomService.findById(joinRoomRequest.getRoomId());
        if (!roomEntity.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if(!isAdminInRoom(roomEntity.get(), currentUser.get().getId()) || !isHostInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if(!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if(isUserAsAdmin(roomEntity.get(), user.get().getId())) {
            roomEntity.get().removeHost();
            roomEntity.get().removeCoHost(user.get().getId());
            roomEntity.get().addHost(user.get());
            roomEntity.get().setHostName(user.get().getFullName());

            // change old host to user
            roomEntity.get().addUser(currentUser.get());
        }else if(isUserInRoom(roomEntity.get(), user.get().getId())) {
            roomEntity.get().removeHost();
            roomEntity.get().removeUser(user.get().getId());
            roomEntity.get().addHost(user.get());
            roomEntity.get().setHostName(user.get().getFullName());

            // change old host to user
            roomEntity.get().addUser(currentUser.get());
        }
        // Save room
        roomService.save(roomEntity.get());

        return BaseResponse.Builder()
                .addMessage(RoomEnum.UPDATE_HOST_SUCCESS)
                .build();
    }
    public ResponseEntity getListRooms(UserDetails user) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        if(!currentUser.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        Set<String> roomUserId = currentUser.get().getRoomAsUser().stream().map(u -> u.getRoomId()).collect(Collectors.toSet());
        Set<String> roomAdminId = currentUser.get().getRoomAsAdmin().stream().map(u -> u.getRoomId()).collect(Collectors.toSet());
        roomUserId.addAll(roomAdminId);
        Set<RoomEntity> roomList = roomService.findByInventoryIds(roomUserId);
        Set<RoomResponse> roomListResponse = new HashSet<>();
        for (RoomEntity room : roomList) {
            roomListResponse.add(new RoomResponse(room));
        }
        return BaseResponse.Builder()
                .addData(roomListResponse)
                .addMessage(RoomEnum.JOIN_SUCCESS)
                .build();
    }
}
