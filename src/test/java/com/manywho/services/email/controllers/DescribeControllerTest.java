package com.manywho.services.email.controllers;

import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.junit.Test;
import javax.ws.rs.core.Response;

public class DescribeControllerTest extends EmailServiceFunctionalTest {
    @Test
    public void testDescribeServiceResponse() throws Exception {
        Response responseMsg = target("/metadata").request()
                .post(getServerRequestFromFile("DescribeController/metadata1-request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("DescribeController/metadata1-response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
