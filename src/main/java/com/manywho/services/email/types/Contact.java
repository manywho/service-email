package com.manywho.services.email.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;
import org.hibernate.validator.constraints.Email;

@Type.Element(name = "Contact", summary = "The Contact object structure")
public class Contact implements Type {
    @Type.Property(name = "Name", contentType = ContentType.String, bound = false)
    private String name;

    @Email(message = "An invalid email address was given")
    @Type.Property(name = "Email", contentType = ContentType.String, bound = false)
    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
