package com.server.onlineup.model.entity;

import com.server.onlineup.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RoomUser")
public class RoomUserEntity {
    // This line help the json return have id of user
    public String getId() {return id.getProfile();}
    public String getProfileId() {
        return id.getProfile();
    }
    public String getRoomId() {
        return id.getRoom();
    }
    public long getJoinTime() {
        return joinTime;
    }

    @EmbeddedId
    private RoomProfileKey id;

    @ManyToOne
    @MapsId("profile")
    @JoinColumn(name = "profile_id")
    private ProfileEntity profileEntity;

    @ManyToOne
    @MapsId("room")
    @JoinColumn(name = "room_id")
    private RoomEntity roomEntity;

    private long joinTime= TimeUtils.getCurrentTimestamp();

    public RoomUserEntity(){}

    public RoomUserEntity(ProfileEntity profileEntity, RoomEntity roomEntity) {
        this.roomEntity = roomEntity;
        this.profileEntity = profileEntity;
        this.id = new RoomProfileKey(roomEntity.getId(), profileEntity.getId());
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        RoomUserEntity that = (RoomUserEntity) o;
        return Objects.equals(profileEntity, that.profileEntity) &&
                Objects.equals(roomEntity, that.roomEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileEntity, roomEntity);
    }
}
