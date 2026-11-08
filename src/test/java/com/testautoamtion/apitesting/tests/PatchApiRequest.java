package com.testautoamtion.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautoamtion.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PatchApiRequest {

	@Test
	public void patchApiRequest() {

		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),
					"UTF-8");
			String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_REQUEST_BODY),
					"UTF-8");
			String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),
					"UTF-8");
			String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),
					"UTF-8");

			// POST API
			Response response = RestAssured.given().contentType(ContentType.JSON).body(postAPIRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().post().then().assertThat()
					.statusCode(200).extract().response();

			JSONArray jsonArrayFirstName = JsonPath.read(response.body().asString(), "$.booking..firstname");
			String firstName = (String) jsonArrayFirstName.get(0);
			Assert.assertEquals(firstName, "Jim");

			int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");

			// GET API call
			RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking")
					.when().get("/{bookingId}", bookingId).then().statusCode(200);

			// Token Generation
			Response tokenResponse = RestAssured.given().contentType(ContentType.JSON).body(tokenAPIRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/auth").when().post().then().assertThat()
					.statusCode(200).extract().response();
			
			String token = JsonPath.read(tokenResponse.body().asString(), "$.token");
			
			
			//Put API call
			RestAssured
			        .given()
			              .contentType(ContentType.JSON)
			              .body(putAPIRequestBody)
			              .header("Cookie", "token="+token)
			              .baseUri("https://restful-booker.herokuapp.com/booking")
			         .when()
			              .put("{bookingId}", bookingId)
			         .then()
			              .assertThat()
			              .statusCode(200)
			              .body("firstname", Matchers.equalTo("James"))
			              .body("additionalneeds", Matchers.equalTo("Super bowl"));
			
			// Patch API call
			RestAssured
			          .given()
			                .contentType(ContentType.JSON)
			                .body(patchAPIRequestBody)
			                .header("Cookie", "token="+token)
			                .baseUri("https://restful-booker.herokuapp.com/booking")
			           .when()
			                .patch("{bookingId}", bookingId)
			           .then()
				             .assertThat()
				             .statusCode(200)
				             .body("firstname", Matchers.equalTo("John"));
			


		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
