package com.lab4.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@OpenAPIDefinition(
        info = @Info(
                title = "Restful in medicine",
                version = "1.0",
                description = "OpenAPiDocs",
                contact = @Contact(name = "Mail", email = "mail@mail.com")
        )
)
public class OpenApiConfig {
}