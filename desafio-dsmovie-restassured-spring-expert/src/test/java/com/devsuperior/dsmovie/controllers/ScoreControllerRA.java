package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.given;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ScoreControllerRA {

	private String adminToken;

	@BeforeEach
	public void setUp() throws JSONException {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;

		adminToken = TokenUtil.obtainAccessToken("maria@gmail.com", "12345678");
	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		JSONObject body = new JSONObject();
		body.put("movieId", 9999);
		body.put("score", 4.0);

		given()
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.put("/scores")
		.then()
			.statusCode(404);
	}

	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		JSONObject body = new JSONObject();
		body.put("score", 4.0);

		given()
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.put("/scores")
		.then()
			.statusCode(422);
	}

	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		JSONObject body = new JSONObject();
		body.put("movieId", 1);
		body.put("score", -1.0);

		given()
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.body(body.toString())
			.put("/scores")
		.then()
			.statusCode(422);
	}
}
