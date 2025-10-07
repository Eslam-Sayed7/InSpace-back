package com.InSpace.Api.services;

import com.InSpace.Api.services.dto.Auth.*;

public interface UserService {
    AuthServiceResult registerUserAndSyncRole(RegisterRequestModel registerRequestModel);

    // TODO
    AccountResponse changePassword(AccountRePasswordModel changePasswordRequestModel);

    AccountResponse changeMail(AccountReMailModel changeMailRequestModel);

    AccountResponse changeUsername(AccountReUsernameModel changeUserNameRequestModel);
}
