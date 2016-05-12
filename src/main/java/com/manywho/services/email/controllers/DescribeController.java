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
                .addConfigurationValue(new DescribeValue("Host", ContentType.String, true))
                .addConfigurationValue(new DescribeValue("Port", ContentType.Number, true))
                .addConfigurationValue(new DescribeValue("Username", ContentType.String, true))
                .addConfigurationValue(new DescribeValue("Password", ContentType.Password, true))
                .addConfigurationValue(new DescribeValue("Transport", ContentType.String, true))
                .createDescribeService()
                .createResponse();
    }
}
