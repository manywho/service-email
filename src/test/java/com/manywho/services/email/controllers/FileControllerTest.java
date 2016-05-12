package com.manywho.services.email.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.junit.Test;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class FileControllerTest  extends EmailServiceFunctionalTest {
    @Test
    public void testReturnEmptyListOfFiles() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/file").request()
                .headers(headers)
                .post(getServerRequestFromFile("FileController/request-list-files.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("FileController/response-list-files.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}
