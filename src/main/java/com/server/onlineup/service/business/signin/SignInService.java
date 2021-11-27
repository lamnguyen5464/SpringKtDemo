package com.server.onlineup.service.business.signin;

import com.server.onlineup.common.constant.AuthenticationEnum;
import com.server.onlineup.common.exception.APIException;
import com.server.onlineup.common.response.BaseResponse;
import com.server.onlineup.model.entity.ProfileEntity;
import com.server.onlineup.model.response.EmailVerificationReponse;
import com.server.onlineup.model.response.JwtResponse;
import com.server.onlineup.model.response.LoginResponse;
import com.server.onlineup.model.response.ProfileResponse;
import com.server.onlineup.service.database.ProfileService;
import com.server.onlineup.service.implementation.IUserService;
import com.server.onlineup.service.provider.jwt.JwtService;
import com.server.onlineup.service.provider.rest.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SignInService {
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RestService restService;
    @Autowired
    private ProfileService profileService;

    public ResponseEntity handleSignIn(ProfileEntity user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ProfileEntity currentUser = userService.findByUsername(user.getEmail()).get();
        JwtResponse jwtResponse = new JwtResponse(jwt);
        return BaseResponse.Builder()
                .addMessage(AuthenticationEnum.LOGIN_SUCCESS)
                //Edit from jwtResponse to get more information of user.
                .addData(new LoginResponse(new ProfileResponse(currentUser), jwtResponse))
                .build();
    }

    public ResponseEntity handleSignInWithFacebook(String facebookToken) {
        EmailVerificationReponse res = restService.requestProfileFromFbToken(facebookToken);
        if (!profileService.isUseFb(res.email, res.id)) {
            throw new APIException(BaseResponse.Builder()
                    .addErrorStatus(HttpStatus.BAD_REQUEST)
                    .addMessage(AuthenticationEnum.FAIL_FACEBOOK_SIGN_IN)
            );
        }
        String jwt = jwtService.generateTokenFromEmail(res.email);
        JwtResponse jwtResponse = new JwtResponse(jwt);
        // Add to get user information
        ProfileEntity user = profileService.findByUsername(res.email).get();
        return BaseResponse.Builder()
                .addMessage(AuthenticationEnum.LOGIN_SUCCESS)
                .addData(new LoginResponse(new ProfileResponse(user), jwtResponse))
                .build();
    }

}
