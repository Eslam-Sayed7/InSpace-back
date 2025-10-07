package com.InSpace.Api.services;

import com.InSpace.Api.services.dto.Email.EmailFormateDto;

public interface EmailService {
    void sendEmail(EmailFormateDto emailDto);
}
