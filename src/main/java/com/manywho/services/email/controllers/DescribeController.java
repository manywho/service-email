package com.manywho.services.email.controllers;

import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static com.manywho.services.email.entities.Configuration.*;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class DescribeController {
    @Path("/metadata")
    @POST
    public DescribeServiceResponse describe(ServiceRequest serviceRequest) throws  Exception {
        return new DescribeServiceBuilder()
                .setProvidesLogic(true)
                .setProvidesFiles(true)
                .setCulture(new Culture("EN", "US"))
                .addConfigurationValue(new DescribeValue(HOST, ContentType.String, true))
                .addConfigurationValue(new DescribeValue(PORT, ContentType.Number, true))
                .addConfigurationValue(new DescribeValue(USERNAME, ContentType.String, true))
                .addConfigurationValue(new DescribeValue(PASSWORD, ContentType.Password, true))
                .addConfigurationValue(new DescribeValue(TRANSPORT, ContentType.String, true))
                .addConfigurationValue(new DescribeValue(BOX_APP_USER_ID, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(BOX_ENTERPRISE_ID, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(BOX_PUBLIC_KEY_ID, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(USE_BOX_FOR_ATTACHMENT, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(S3ACCESSKEYID, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(S3ACCESSSECRET, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(S3BUCKETNAME, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(S3REGION, ContentType.String, false))
                .addConfigurationValue(new DescribeValue(RETAIN_FILE, ContentType.String, false))
                .createDescribeService()
                .createResponse();
    }
}
