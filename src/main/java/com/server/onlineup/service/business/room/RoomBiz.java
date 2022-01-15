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
import com.server.onlineup.repository.ProfileRepository;
import com.server.onlineup.repository.RoomRepository;
import com.server.onlineup.service.database.ProfileService;
import com.server.onlineup.service.database.RoomAdminService;
import com.server.onlineup.service.database.RoomService;
import com.server.onlineup.service.database.RoomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
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
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private RoomRepository roomRepository;

    public ResponseEntity handleCreateRoom(UserDetails user, RoomEntity room) {
        // Load userDetail to profileUser
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        if (!currentUser.isPresent()) {
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

    public ResponseEntity handleJoinRoom(UserDetails admin, JoinRoomRequest joinRoomRequest) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(admin.getUsername());

        Optional<RoomEntity> roomEntity = roomService.findById(joinRoomRequest.getRoomId());
        if (!roomEntity.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if (!isAdminInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if (!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if (isUserInRoom(roomEntity.get(), user.get().getId())) {
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
        if (!isAdminInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if (!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if (isUserAsAdmin(roomEntity.get(), user.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_ALREADY_HOST));
        } else if (!isUserInRoom(roomEntity.get(), user.get().getId())) {
            roomEntity.get().addCoHost(user.get());
        } else {
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
        if (!isAdminInRoom(roomEntity.get(), currentUser.get().getId()) || !isHostInRoom(roomEntity.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        Optional<ProfileEntity> user = profileService.findByUsername(joinRoomRequest.getUserEmail());
        if (!user.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if (isUserAsAdmin(roomEntity.get(), user.get().getId())) {
            roomEntity.get().removeHost();
            roomEntity.get().removeCoHost(user.get().getId());
            roomEntity.get().addHost(user.get());
            roomEntity.get().setHostName(user.get().getFullName());

            // change old host to user
            roomEntity.get().addUser(currentUser.get());
        } else if (isUserInRoom(roomEntity.get(), user.get().getId())) {
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

    public ResponseEntity getListRoomsAsUser(UserDetails user) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        if (!currentUser.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        Set<String> roomUserId = currentUser.get().getRoomAsUser().stream().map(u -> u.getRoomId()).collect(Collectors.toSet());
        Set<RoomEntity> roomList = roomService.findByInventoryIds(roomUserId);
        Set<RoomResponse> roomListResponse = new HashSet<>();
        for (RoomEntity room : roomList) {
            roomListResponse.add(new RoomResponse(room));
        }
        return BaseResponse.Builder()
                .addData(roomListResponse)
                .addMessage(RoomEnum.GET_SUCCESS)
                .build();
    }

    public ResponseEntity getListRoomsAsAdmin(UserDetails user) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        if (!currentUser.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        Set<String> roomAdminId = currentUser.get().getRoomAsAdmin().stream().map(u -> u.getRoomId()).collect(Collectors.toSet());
        Set<RoomEntity> roomList = roomService.findByInventoryIds(roomAdminId);
        Set<RoomResponse> roomListResponse = new HashSet<>();
        for (RoomEntity room : roomList) {
            roomListResponse.add(new RoomResponse(room));
        }
        return BaseResponse.Builder()
                .addData(roomListResponse)
                .addMessage(RoomEnum.GET_SUCCESS)
                .build();
    }

    // Helper
    public boolean isUserInRoom(RoomEntity room, String userId) {
        for (RoomUserEntity user : room.getUserList()) {
            if (user.getProfileId().equals(userId)) {
                return true;
            }
        }
        for (RoomAdminEntity user : room.getAdminList()) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserAsAdmin(RoomEntity room, String userId) {
        for (RoomAdminEntity user : room.getAdminList()) {
            if (user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHostInRoom(RoomEntity room, String adminId) {
        for (RoomAdminEntity user : room.getAdminList()) {
            if (user.getId().equals(adminId) && (user.getRole() == RoleEnum.HOST)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdminInRoom(RoomEntity room, String adminId) {
        for (RoomAdminEntity user : room.getAdminList()) {
            if (user.getId().equals(adminId)) {
                return true;
            }
        }
        return false;
    }

    //------------------------------------------------------------------------
//    public ResponseEntity handleSearchHost(UserDetails host, String roomID, String emailHost) {
//        Optional<ProfileEntity> currentUser = profileService.findByUsername(host.getUsername());
//
//        Optional<RoomEntity> roomEntity = roomService.findById(roomID);
//        if (!roomEntity.isPresent()) {
//            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
//        }
//        if (!isAdminInRoom(roomEntity.get(), currentUser.get().getId())) {
//            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
//        }
//        List<ProfileEntity> listUser = profileRepository.findHostsOfRoom(roomID, emailHost);
//
//        return BaseResponse.Builder()
//                .addData(listUser)
//                .addMessage(RoomEnum.FOUND_HOSTS_OF_ROOM)
//                .build();
//    }

    public ResponseEntity handleSearchRoom(String roomName) {
        List<RoomEntity> a = roomRepository.searchRoom(roomName);
        return BaseResponse.Builder()
                .addData(a)
                .addMessage(RoomEnum.FOUND_ROOMS)
                .build();
    }

    public ResponseEntity handleUpdateStatusRoom(UserDetails user, RoomEntity roomEntity) {
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());
        Optional<RoomEntity> room = roomService.findById(roomEntity.getId());
        if (!room.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if (!isAdminInRoom(room.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.NOT_ROOM_ADMIN));
        }
        room.get().setAddress(roomEntity.getAddress());
        room.get().setStatus(roomEntity.getStatus());
        room.get().setEndDate(roomEntity.getEndDate());
        room.get().setMaxQueuer(roomEntity.getMaxQueuer());
        room.get().setName(roomEntity.getName());
        room.get().setProcessUnit(roomEntity.getProcessUnit());
        room.get().setStartDate(roomEntity.getStartDate());
        room.get().setTheme(roomEntity.getTheme());
        roomService.save(room.get());
        return BaseResponse.Builder()
                .addMessage(RoomEnum.UPDATE_STATUS)
                .addData(room.get())
                .build();
    }

    public ResponseEntity showRoomDetail(String roomID) {
        Optional<RoomEntity> room = roomService.findById(roomID);

        if (!room.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        RoomEntity a = room.get();
        return BaseResponse.Builder()
                .addData(a)
                .addMessage(RoomEnum.SHOW_DETAIL_SUCCESS)
                .build();
    }

    public ResponseEntity joinRoomAsQueuer(UserDetails user, String roomID) {
        Optional<RoomEntity> room = roomService.findById(roomID);
        Optional<ProfileEntity> currentUser = profileService.findByUsername(user.getUsername());

        if (!room.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.ROOM_NOT_EXIST));
        }
        if (!currentUser.isPresent()) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_NOT_EXIST));
        }
        if (isAdminInRoom(room.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_ALREADY_HOST));
        }
        if (isUserInRoom(room.get(), currentUser.get().getId())) {
            throw new APIException(BaseResponse.Builder().addMessage(RoomEnum.USER_ALREADY_IN_ROOM));
        }

        room.get().addUser(currentUser.get());
        // Save room
        roomService.save(room.get());
        return BaseResponse.Builder()
                .addMessage(RoomEnum.JOIN_SUCCESS)
                .build();
    }
}
