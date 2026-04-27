package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class MovieControllerRA {

	private String adminToken, clientToken, invalidToken;
	private Long existingId, nonExistingId;

	@BeforeEach
	public void setUp() throws JSONException {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;

		adminToken = TokenUtil.obtainAccessToken("maria@gmail.com", "12345678");
		clientToken = TokenUtil.obtainAccessToken("alex@gmail.com", "12345678");
		invalidToken = "invalid_token";

		existingId = 1L;
		nonExistingId = 9999L;
	}

	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
			.get("/movies")
		.then()
			.statusCode(200);
	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given()
			.queryParam("title", "Witcher")
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.title", hasItems("The Witcher"));
	}

	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given()
			.get("/movies/{id}", existingId)
		.then()
			.statusCode(200)
			.body("id", is(existingId.intValue()))
			.body("title", equalTo("The Witcher"));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
			.get("/movies/{id}", nonExistingId)
		.then()
			.statusCode(404);
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		JSONObject body = new JSONObject();
		body.put("title", "");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/image.jpg");

		given()
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.post("/movies")
		.then()
			.statusCode(422);
	}

	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject body = new JSONObject();
		body.put("title", "Test Movie Title");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/image.jpg");

		given()
			.header("Authorization", "Bearer " + clientToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.post("/movies")
		.then()
			.statusCode(403);
	}

	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject body = new JSONObject();
		body.put("title", "Test Movie Title");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/image.jpg");

		given()
			.header("Authorization", "Bearer " + invalidToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
