package com.manywho.services.email.controllers;

import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineInvokeRequest;
import com.manywho.sdk.api.run.EngineInvokeResponse;
import com.manywho.sdk.api.run.elements.map.MapElementInvokeResponse;
import com.manywho.services.email.test.EmailServiceFunctionalTest;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceiveDecisionTest extends EmailServiceFunctionalTest {
    @Test
    //todo this test is a bit complicated and not easy to follow
    public void testDecisionCallbackResponse() throws Exception {

        String token = "67204d5c-6022-474d-8f80-0d576b43d02d";

        MockHttpRequest request = MockHttpRequest.get("/callback/response/" + token + "/go")
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpResponse response = new MockHttpResponse();

        EngineInvokeResponse invokeResponse = mock(EngineInvokeResponse.class);

        // mocking client join
        Call<EngineInvokeResponse> callJoin1 = mock(Call.class);
        Response<EngineInvokeResponse> callResponseJoin1 = Response.success(invokeResponse);

        when(runClient.join(UUID.fromString("67204d5c-6022-474d-8f80-0d576b43d02d"), UUID.fromString("340279d0-350c-4886-9af8-86ebeda0bd71")))
                .thenReturn(callJoin1);

        when(callJoin1.execute()).thenReturn(callResponseJoin1);

        //mocking client callback
        Call<InvokeType> callCallback = mock(Call.class);
        when(callCallback.execute()).thenReturn(Response.success(InvokeType.Forward));

        when(runClient.callback(any(), any(), any())).thenReturn(callCallback);

        // mocking client sync
        when(invokeResponse.getStateId()).thenReturn(UUID.fromString("340279d0-350c-4886-9af8-86ebeda0bd71"));
        when(invokeResponse.getStateToken()).thenReturn(UUID.fromString("340279d0-350c-4886-9af8-86ebeda0bd71"));
        List<MapElementInvokeResponse> mapElements = new ArrayList<>();
        mapElements.add(new MapElementInvokeResponse());
        when(invokeResponse.getMapElementInvokeResponses()).thenReturn(mapElements);

        EngineInvokeResponse engineInvokeResponse = mock(EngineInvokeResponse.class);
        Call<EngineInvokeResponse> callSync = mock(Call.class);
        Response<EngineInvokeResponse> responseEngineInvokeResponse = Response.success(engineInvokeResponse);
        when(callSync.execute()).thenReturn(responseEngineInvokeResponse);

        // join
        when(engineInvokeResponse.getStateId()).thenReturn(UUID.fromString("340279d0-350c-4886-9af8-86ebeda0bd71"));
        Call<EngineInvokeResponse> callJoin2 = mock(Call.class);
        when(runClient.join("67204d5c-6022-474d-8f80-0d576b43d02d", "340279d0-350c-4886-9af8-86ebeda0bd71")).thenReturn(callJoin2);

        EngineInvokeResponse engineInvokeResponseJoin2 = mock(EngineInvokeResponse.class);
        when(engineInvokeResponseJoin2.getJoinFlowUri()).thenReturn("http://urljoin");
        Response<EngineInvokeResponse> responseWrapper2 = Response.success(engineInvokeResponseJoin2);
        when(callJoin2.execute()).thenReturn(responseWrapper2);

        when(runClient.execute(any(UUID.class),any(UUID.class),any(EngineInvokeRequest.class))).thenReturn(callSync);

        //mock redis status
        mockJedisPool.getResource()
                .set("service:email:requests:" + token,
                        getJsonFormatFileContent("ReceiveDecision/persistence.json"));

        dispatcher.invoke(request, response);
        assertEquals(307, response.getStatus());
        assertEquals("http://urljoin", response.getOutputHeaders().get("Location").get(0).toString());
    }
}
