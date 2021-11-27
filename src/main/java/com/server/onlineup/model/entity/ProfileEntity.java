package com.server.onlineup.model.entity;

import com.server.onlineup.common.utils.NumberUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Profile")
public class ProfileEntity {
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false)
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column
    private String idFacebook;

    @Column
    private String fcmToken;

    @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<RoomUserEntity> roomAsUser = new HashSet<RoomUserEntity>();

    public Set<RoomUserEntity> getRoomAsUser() {
        return roomAsUser;
    }

    public Set<RoomAdminEntity> getRoomAsAdmin() {
        return roomAsAdmin;
    }

    @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
    Set<RoomAdminEntity> roomAsAdmin = new HashSet<RoomAdminEntity>();

    public String getFb_id() {
        return idFacebook;
    }

    public void setFb_id(String fb_id) {
        this.idFacebook = fb_id;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcm_token) {
        this.fcmToken = fcm_token;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void addAdminRoom(RoomEntity room) {
        RoomAdminEntity roomAdminEntity = new RoomAdminEntity(this, room);
        roomAsAdmin.add(roomAdminEntity);
    }

    public void addUserRoom(RoomEntity room) {
        RoomUserEntity roomUserEntity = new RoomUserEntity(this, room);
        roomAsUser.add(roomUserEntity);
    }


    // Getter & Setter
    public ProfileEntity() {
    }

    public ProfileEntity(String email, String id) {
        this.email = email;
        this.idFacebook = id;
        this.password = NumberUtils.generateRandomString((int) (Math.floor(Math.random())));
        this.fullName = "Unknown";
        this.roles = "USER";
    }

    public ProfileEntity(String email, String fullName, String password) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.roles = "USER";
    }

    private String roles;

    public String getRoles() {
        return this.roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "users_roles",
//            joinColumns = {@JoinColumn(name = "user_id")},
//            inverseJoinColumns = {@JoinColumn(name = "role_id")})

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return this.fullName;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }
}