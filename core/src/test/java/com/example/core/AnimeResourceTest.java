package com.example.core;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AnimeResourceTest {

    @Test
    public void testGetAllAnimes() {
        given()
          .when().get("/animes")
          .then()
             .statusCode(200);
    }
}
