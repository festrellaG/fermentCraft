package com.test.canonical.fermentcraft.resource;

import com.test.canonical.fermentcraft.dto.PageResultDTO;
import com.test.canonical.fermentcraft.dto.ProductBatchRequestDTO;
import com.test.canonical.fermentcraft.dto.ProductBatchResponseDTO;
import com.test.canonical.fermentcraft.service.ProductBatchService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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

@Path("/api/v1/product-batches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductBatchResource {

    private final ProductBatchService productBatchService;

    @Inject
    public ProductBatchResource(ProductBatchService productBatchService) {
        this.productBatchService = productBatchService;
    }

    @GET
    @Operation(summary = "List product batches", description = "Returns active batches with optional category filtering.")
    @APIResponse(responseCode = "200", description = "Batches returned.")
    public PageResultDTO<ProductBatchResponseDTO> listBatches(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("category") String category
    ) {
        return productBatchService.listBatches(page, size, category);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get product batch", description = "Returns an active product batch by ID.")
    @APIResponse(responseCode = "200", description = "Batch returned.")
    @APIResponse(responseCode = "404", description = "Batch not found.")
    public ProductBatchResponseDTO getById(@PathParam("id") Long id) {
        return productBatchService.getById(id);
    }

    @POST
    @Operation(summary = "Create product batch")
    @APIResponse(responseCode = "201", description = "Batch created.")
    @APIResponse(responseCode = "400", description = "Request validation failed.")
    public Response create(@Valid ProductBatchRequestDTO request, @Context UriInfo uriInfo) {
        ProductBatchResponseDTO created = productBatchService.create(request);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(created.id().toString())
                        .build())
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update product batch")
    @APIResponse(responseCode = "200", description = "Batch updated.")
    @APIResponse(responseCode = "400", description = "Request validation failed.")
    @APIResponse(responseCode = "404", description = "Batch not found.")
    public ProductBatchResponseDTO update(
            @PathParam("id") Long id,
            @Valid ProductBatchRequestDTO request
    ) {
        return productBatchService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deactivate product batch")
    @APIResponse(responseCode = "204", description = "Batch deactivated.")
    @APIResponse(responseCode = "404", description = "Batch not found.")
    public Response delete(@PathParam("id") Long id) {
        productBatchService.delete(id);
        return Response.noContent().build();
    }
}
