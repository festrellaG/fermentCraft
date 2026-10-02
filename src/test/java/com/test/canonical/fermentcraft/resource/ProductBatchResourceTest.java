package com.test.canonical.fermentcraft.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class ProductBatchResourceTest {

    @Test
    void testListBatchesEndpoint() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/v1/product-batches")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", is(not(empty())))
                .body("pageIndex", equalTo(0))
                .body("pageSize", equalTo(10));
    }

    @Test
    void testCreateBatchValidation() {
        String invalidRequest = """
                {
                  "productName": "Kombucha de prueba",
                  "category": "KOMBUCHA",
                  "availableQuantity": 10,
                  "unitPrice": -1.00,
                  "expirationDate": "2027-12-31"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/v1/product-batches")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details", is(not(empty())));
    }
}
