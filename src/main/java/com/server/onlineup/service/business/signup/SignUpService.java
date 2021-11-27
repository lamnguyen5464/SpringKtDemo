package com.server.onlineup.service.business.signup;

import com.server.onlineup.common.constant.AuthenticationEnum;
import com.server.onlineup.common.exception.APIException;
import com.server.onlineup.common.response.BaseResponse;
import com.server.onlineup.model.entity.ProfileEntity;
import com.server.onlineup.model.response.EmailVerificationReponse;
import com.server.onlineup.model.response.JwtResponse;
import com.server.onlineup.model.response.LoginResponse;
import com.server.onlineup.model.response.ProfileResponse;
import com.server.onlineup.service.database.ProfileService;
import com.server.onlineup.service.provider.jwt.JwtService;
import com.server.onlineup.service.provider.rest.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    @Autowired
    private OTPService otpService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RestService restService;

    @Autowired
    private JwtService jwtService;

//    public boolean handleInputEmail(String email) {
//        if (Validator.IsEmail(email)) {
//            otpService.createForMail(email);
//            return true;
//        }
//        return false;
//    }

    //ham handle input email
    public ResponseEntity handleInputEmailForOTP(String email) {

        if (profileService.existingEmail(email)) {
            throw new APIException(BaseResponse.Builder()
                    .addErrorStatus(HttpStatus.BAD_REQUEST)
                    .addMessage(AuthenticationEnum.EXISTING_EMAIL)
            );
        } else {
            otpService.createForMail(email);
        }


        return BaseResponse.Builder()
                .addMessage(AuthenticationEnum.SEND_OTP_SUCCESS).build();
    }

    public ResponseEntity handleVerifyOTP(String email, String OTP) {
        otpService.verifyOtpForEmail(email, OTP);
        String token = jwtService.generateTokenFromEmail(email);
        return BaseResponse.Builder()
                .addData(new EmailVerificationReponse(email, token))
                .addMessage(AuthenticationEnum.OTP_SUCCESS)
                .build();
    }

    public ResponseEntity handleCreateAccount(String email, String fullname, String password) {
        if (!profileService.existingEmail(email)) {
            profileService.createAccount(email, fullname, password);
        } else profileService.updateAccount(email, fullname, password);
        //Adding: response jwt + profile info to navigate to home screen.
        String jwt = jwtService.generateTokenFromEmail(email);
        JwtResponse jwtResponse = new JwtResponse(jwt);
        ProfileEntity user = profileService.findByUsername(email).get();

        LoginResponse loginResponse = new LoginResponse(new ProfileResponse(user), jwtResponse);
        return BaseResponse.Builder()
                .addMessage(AuthenticationEnum.CREATE_ACCOUNT_SUCCESS)
                .addData(loginResponse)
                .build();
    }

    public ResponseEntity handleFacebookToken(String token) {
        EmailVerificationReponse res = restService.requestProfileFromFbToken(token);

        if (res == null || res.email == null || res.email.equals("")) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.FACEBOOK_FAIL)
                            .addErrorStatus(HttpStatus.BAD_REQUEST)
            );
        }

        if (profileService.existingEmail(res.email)) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.EXISTING_EMAIL)
                            .addErrorStatus(HttpStatus.BAD_REQUEST)
            );
        }

        res.accessToken = jwtService.generateTokenFromEmail(res.email);
        profileService.createAccountFb(res.email, res.id);
        return BaseResponse.<EmailVerificationReponse>Builder()
                .addData(res)
                .addMessage(AuthenticationEnum.FACEBOOK_SUCCESS)
                .build();
    }

}
