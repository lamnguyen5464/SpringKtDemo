package com.server.onlineup.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.onlineup.model.entity.RoomAdminEntity;
import com.server.onlineup.model.entity.RoomEntity;
import com.server.onlineup.model.entity.RoomUserEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoomResponse {
    private long processUnit;
    private String address;
    private String theme;
    private String name;
    private int maxQueuer;
    private String hostName;
    private boolean status;
    private String id;
    Set<RoomUserEntity> userList = new HashSet<RoomUserEntity>();
    Set<RoomAdminEntity> adminList = new HashSet<RoomAdminEntity>();
    private long startDate;
    private long endDate;

    public long getProcessUnit() {
        return processUnit;
    }

    public void setProcessUnit(long processUnit) {
        this.processUnit = processUnit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxQueuer() {
        return maxQueuer;
    }

    public void setMaxQueuer(int maxQueuer) {
        this.maxQueuer = maxQueuer;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<RoomUserEntity> getUserList() {
        return userList;
    }

    public void setUserList(Set<RoomUserEntity> userList) {
        this.userList = userList;
    }

    public Set<RoomAdminEntity> getAdminList() {
        return adminList;
    }

    public void setAdminList(Set<RoomAdminEntity> adminList) {
        this.adminList = adminList;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public RoomResponse(RoomEntity room) {
        processUnit = room.getProcessUnit();
        address = room.getAddress();
        theme = room.getTheme();
        name = room.getName();
        maxQueuer = room.getMaxQueuer();
        hostName = room.getHostName();
        status= room.getStatus();
        id = room.getId();
        userList = room.getUserList();
        adminList = room.getAdminList();
        startDate = room.getStartDate();
        endDate = room.getEndDate();
    }
}
