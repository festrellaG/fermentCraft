package com.test.canonical.fermentcraft.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "shipping-service")
@Path("/api/v1/shipping")
public interface ShippingServiceClient {

    @GET
    @Path("/quote")
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout(2000)
    @Fallback(ShippingServiceFallback.class)
    ShippingRateResponseDTO calculateShippingRate(@QueryParam("address") String address);
}
