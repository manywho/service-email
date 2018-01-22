package com.manywho.services.email.types;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Decision Choice", summary = "One possible response to the decision request")
public class DecisionChoice implements Type {

    @Type.Property(name = "Label", contentType = ContentType.String, bound = false)
    private String label;

    @Type.Property(name = "Redirect", contentType = ContentType.String, bound = false)
    private String redirect;

    public String getLabel() {
        return label;
    }

    public String getRedirect() {
        return redirect;
    }

}
