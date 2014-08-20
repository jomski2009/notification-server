package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.EmailMessageResult;
import com.yookos.notifyserver.core.domain.EmailVerificationEntity;
import com.yookos.notifyserver.core.domain.UserExistsResult;
import com.yookos.notifyserver.core.domain.UserRegistrationObject;

import java.util.List;

/**
 * Created by jome on 2014/05/23.
 */
public interface EmailValidationService {
    void addBannedDomains(List<String> bannedDomains);

    void processBadEmailAccounts();

    EmailMessageResult sendVerificationEmail(UserRegistrationObject registrationEmailVerification);

    com.yookos.notifyserver.core.domain.EmailValidationResult validateRegistrationToken(EmailVerificationEntity verificationToken);

    UserExistsResult checkUserExists(String username);

    UserExistsResult checkUserEmailExists(String email);

    EmailMessageResult sendVerificationEmail(String email);
}
