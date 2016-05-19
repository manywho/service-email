package com.manywho.services.email.types;

import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;
import org.hibernate.validator.constraints.Email;

@TypeElement(name = Contact.NAME)
public class Contact {
    public static final String NAME = "Contact";

    @TypeProperty(name = "Name", contentType = ContentType.String, bound = false)
    private String name;

    @Email(message = "An invalid email address was given")
    @TypeProperty(name = "Email", contentType = ContentType.String, bound = false)
    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
