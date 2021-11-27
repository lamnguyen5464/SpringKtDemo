package com.server.onlineup.model.response;

import com.server.onlineup.model.entity.ProfileEntity;

public class ProfileResponse {
    public String id;
    public String email;
    public String fullName;
    public String fcmToken;
    public String idFacebook;

    public ProfileResponse(ProfileEntity profileEntity) {
        this.id = profileEntity.getId();
        this.email = profileEntity.getEmail();
        this.fullName = profileEntity.getFullName();
        this.fcmToken = profileEntity.getFcmToken();
        this.idFacebook = profileEntity.getFb_id();
    }
}
