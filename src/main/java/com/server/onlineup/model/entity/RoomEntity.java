package com.server.onlineup.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.server.onlineup.common.constant.RoleEnum;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Room")
public class RoomEntity {
    @Column(nullable = false)
    private long processUnit=5;

    @Column(nullable = false)
    private String address="OnLineUp Nguyen Van Cu";

    @Column(nullable = false)
    private String theme = "#5e9ac4";

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxQueuer = 100;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false)
    private boolean status=true;

    @Id
    private String id = UUID.randomUUID().toString();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
//    @JsonManagedReference
    Set<RoomUserEntity> userList = new HashSet<RoomUserEntity>();

    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL,
            orphanRemoval = true)
//    @JsonManagedReference
    Set<RoomAdminEntity> adminList = new HashSet<RoomAdminEntity>();

    @Column(nullable = false)
    private long startDate;

    @Column(nullable = false)
    private long endDate;


    public void setHostName(String s) {
        hostName = s;
        if(name ==null)
            name =  hostName + "'s meeting room";
    }

    public void addUser(ProfileEntity user) {
        RoomUserEntity roomUserEntity = new RoomUserEntity(user, this);
        userList.add(roomUserEntity);
    }

    public void addHost(ProfileEntity admin) {
        RoomAdminEntity roomAdminEntity = new RoomAdminEntity(admin, this);
        roomAdminEntity.changeRole(RoleEnum.HOST);
        adminList.add(roomAdminEntity);
    }

    public void addCoHost(ProfileEntity admin) {
        RoomAdminEntity roomAdminEntity = new RoomAdminEntity(admin, this);
        roomAdminEntity.changeRole(RoleEnum.HOST);
        adminList.add(roomAdminEntity);
    }
    public void removeUser(String userId) {
        for (RoomUserEntity user : userList) {
            if(userId.equals(user.getProfileId())) {
                userList.remove(user);
                return;
            }
        }
    }
    public void removeCoHost(String coHostId) {
        for (RoomAdminEntity admin : adminList) {
            if(coHostId.equals(admin.getProfileId())) {
                adminList.remove(admin);
                return;
            }
        }
    }
    public void removeHost() {
        for (RoomAdminEntity admin : adminList) {
            if(admin.getRole() == RoleEnum.HOST) {
                adminList.remove(admin);
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!(o instanceof RoomEntity))
            return false;
        RoomEntity post = (RoomEntity) o;
        return Objects.equals(id, post.getId()) && Objects.equals(userList, post.userList)
                && Objects.equals(adminList, post.adminList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getter & Setter
    public String getId() {
        return this.id;
    }

    public Set<RoomAdminEntity> getAdminList() {
        return this.adminList;
    }
    public Set<RoomUserEntity> getUserList() {
        return this.userList;
    }

    public long getProcessUnit() {
        return processUnit;
    }

    public String getAddress() {
        return address;
    }

    public String getTheme() {
        return theme;
    }

    public String getName() {
        return name;
    }

    public int getMaxQueuer() {
        return maxQueuer;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isStatus() {
        return status;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }
    public boolean getStatus() {
        return status;
    }
}
