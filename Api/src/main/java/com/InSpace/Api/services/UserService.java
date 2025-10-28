package com.InSpace.Api.services;

import com.InSpace.Api.services.dto.Auth.AccountReMailModel;
import com.InSpace.Api.services.dto.Auth.AccountRePasswordModel;
import com.InSpace.Api.services.dto.Auth.AccountReUsernameModel;
import com.InSpace.Api.services.dto.Auth.AccountResponse;
import com.InSpace.Api.services.dto.Auth.AuthServiceResult;
import com.InSpace.Api.services.dto.Auth.LoginRequestModel;
import com.InSpace.Api.services.dto.Auth.LoginResponse;
import com.InSpace.Api.services.dto.Auth.RegisterRequestModel;

public interface UserService {
    AuthServiceResult registerUserAndSyncRole(RegisterRequestModel registerRequestModel);

    LoginResponse loginUser(LoginRequestModel loginrequest);
    // TODO
    AccountResponse changePassword(AccountRePasswordModel changePasswordRequestModel);

    AccountResponse changeMail(AccountReMailModel changeMailRequestModel);

    AccountResponse changeUsername(AccountReUsernameModel changeUserNameRequestModel);

    Long getUserIdFromUsername(String username);

}
