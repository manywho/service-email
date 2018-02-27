package com.manywho.services.email.controllers;

import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

public class DescribeControllerTest extends EmailServiceFunctionalTest {
    @Test
    public void testDescribe() throws Exception {
        MockHttpRequest request = MockHttpRequest.post("/metadata")
                .content(getFile("DescribeController/describe-request.json"))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        assertJsonSame(
                getJsonFormatFileContent("DescribeController/describe-response.json"),
                response.getContentAsString()
        );
    }
}
