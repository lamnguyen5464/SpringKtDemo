package com.server.onlineup.model.entity;

import com.server.onlineup.common.constant.RoleEnum;
import com.server.onlineup.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RoomAdmin")
public class RoomAdminEntity {
    // This line help the json return have id of admin
    public String getId() {return id.getProfile();}
    public String getProfileId() {
        return id.getProfile();
    }
    public String getRoomId() {
        return id.getRoom();
    }
    public RoleEnum getRole() {
        return role;
    }
    public long getJoinTime() {
        return joinTime;
    }

    @EmbeddedId
    RoomProfileKey id;

    @ManyToOne
    @MapsId("profile")
    @JoinColumn(name = "profile_id",insertable=false,updatable=false)
    ProfileEntity profileEntity;

    @ManyToOne
    @MapsId("room")
    @JoinColumn(name = "room_id", insertable=false,updatable=false)
    RoomEntity roomEntity;

    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.HOST;

    public void changeRole(RoleEnum role) {
        this.role = role;
    }

    public RoomAdminEntity() {}
    public RoomAdminEntity(ProfileEntity profileEntity, RoomEntity room) {
        this.roomEntity = room;
        this.profileEntity = profileEntity;
        this.id = new RoomProfileKey(roomEntity.getId(), profileEntity.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        RoomAdminEntity that = (RoomAdminEntity) o;
        return Objects.equals(profileEntity, that.profileEntity) &&
                Objects.equals(roomEntity, that.roomEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileEntity, roomEntity);
    }
    private long joinTime= TimeUtils.getCurrentTimestamp();

}

