package com.manywho.services.email.controllers;

import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

public class FileControllerTest  extends EmailServiceFunctionalTest {
    @Test
    public void testReturnEmptyListOfFiles() throws Exception {
        MockHttpRequest request = MockHttpRequest.post("/file")
                .content(getFile("FileController/request-list-files.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", injector.getInstance(AuthorizationEncoder.class).encode(AuthenticatedWho.createPublicUser(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"))));

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        assertJsonSame(
                getJsonFormatFileContent("FileController/response-list-files.json"),
                response.getContentAsString()
        );
    }
}
