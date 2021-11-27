package com.server.onlineup.model.response;

public class LoginResponse {
    public ProfileResponse userInfo;
    public JwtResponse jwtResponse;

    public LoginResponse(ProfileResponse userInfo, JwtResponse jwtResponse) {
        this.userInfo = userInfo;
        this.jwtResponse = jwtResponse;
    }
}
