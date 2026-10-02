package com.test.canonical.fermentcraft.resource;

import com.test.canonical.fermentcraft.dto.CreateBoxRequestDTO;
import com.test.canonical.fermentcraft.dto.PageResultDTO;
import com.test.canonical.fermentcraft.dto.SubscriptionBoxResponseDTO;
import com.test.canonical.fermentcraft.entity.BoxStatus;
import com.test.canonical.fermentcraft.service.SubscriptionBoxService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/subscription-boxes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionBoxResource {

    private final SubscriptionBoxService subscriptionBoxService;

    @Inject
    public SubscriptionBoxResource(SubscriptionBoxService subscriptionBoxService) {
        this.subscriptionBoxService = subscriptionBoxService;
    }

    @POST
    @Operation(summary = "Create subscription box", description = "Creates a confirmed box and reserves its inventory.")
    @APIResponse(responseCode = "201", description = "Subscription box created.")
    @APIResponse(responseCode = "400", description = "Request validation failed.")
    @APIResponse(responseCode = "404", description = "A requested product batch was not found.")
    @APIResponse(responseCode = "422", description = "Insufficient product inventory.")
    public Response createBox(@Valid CreateBoxRequestDTO request, @Context UriInfo uriInfo) {
        SubscriptionBoxResponseDTO created = subscriptionBoxService.createBox(request);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(created.id().toString())
                        .build())
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get subscription box", description = "Returns a subscription box by ID.")
    @APIResponse(responseCode = "200", description = "Subscription box returned.")
    @APIResponse(responseCode = "404", description = "Subscription box not found.")
    public SubscriptionBoxResponseDTO getById(@PathParam("id") Long id) {
        return subscriptionBoxService.getById(id);
    }

    @GET
    @Path("/by-order/{orderNumber}")
    @Operation(summary = "Get subscription box by order number")
    @APIResponse(responseCode = "200", description = "Subscription box returned.")
    @APIResponse(responseCode = "404", description = "Subscription box not found.")
    public SubscriptionBoxResponseDTO getByOrderNumber(@PathParam("orderNumber") String orderNumber) {
        return subscriptionBoxService.getByOrderNumber(orderNumber);
    }

    @GET
    @Operation(summary = "List subscription boxes")
    @APIResponse(responseCode = "200", description = "Subscription boxes returned.")
    public PageResultDTO<SubscriptionBoxResponseDTO> listBoxes(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("status") BoxStatus status
    ) {
        return subscriptionBoxService.listBoxes(page, size, status);
    }

    @PATCH
    @Path("/{id}/status")
    @Operation(summary = "Update subscription box status")
    @APIResponse(responseCode = "200", description = "Subscription box status updated.")
    @APIResponse(responseCode = "404", description = "Subscription box not found.")
    public SubscriptionBoxResponseDTO updateStatus(
            @PathParam("id") Long id,
            @QueryParam("newStatus") BoxStatus newStatus
    ) {
        return subscriptionBoxService.updateStatus(id, newStatus);
    }
}
