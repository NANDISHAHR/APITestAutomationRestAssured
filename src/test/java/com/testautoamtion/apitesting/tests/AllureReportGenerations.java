package com.testautoamtion.apitesting.tests;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;

import com.jayway.jsonpath.JsonPath;
import com.testautoamtion.apitesting.utils.BaseTest;
import com.testautoamtion.apitesting.utils.FileNameConstants;
import com.testautomationapitesting.listners.RestAssuredListeners;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

@Epic("Epic - 01")
@Feature("Create Update Delete Booking")
public class AllureReportGenerations extends BaseTest {

	private static final Logger logger = LogManager.getLogger(AllureReportGenerations.class);

	
	@Story("Story 1")
	@Test(description = "End to end API testing")
	@Description("End to end testing")
	@Severity(SeverityLevel.CRITICAL)
	public void deleteApiRequest() {

		try {
			logger.info("Delete API Request started...");
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),
					"UTF-8");
			String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_REQUEST_BODY),
					"UTF-8");
			String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),
					"UTF-8");
			String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),
					"UTF-8");

			// POST API
			Response response = RestAssured.given()
					.filter(new AllureRestAssured())
					.filter(new RestAssuredListeners()).contentType(ContentType.JSON)
					.body(postAPIRequestBody).baseUri("https://restful-booker.herokuapp.com/booking").when().post()
					.then().assertThat().statusCode(200).extract().response();

			JSONArray jsonArrayFirstName = JsonPath.read(response.body().asString(), "$.booking..firstname");
			String firstName = (String) jsonArrayFirstName.get(0);
			AssertJUnit.assertEquals(firstName, "Jim");

			int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");

			// GET API call
			RestAssured.given().filter(new AllureRestAssured()).filter(new RestAssuredListeners()).contentType(ContentType.JSON)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().get("/{bookingId}", bookingId)
					.then().statusCode(200);

			// Token Generation
			Response tokenResponse = RestAssured.given().filter(new AllureRestAssured()).contentType(ContentType.JSON).body(tokenAPIRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/auth").when().post().then().assertThat()
					.statusCode(200).extract().response();

			String token = JsonPath.read(tokenResponse.body().asString(), "$.token");

			// Put API call
			RestAssured.given().filter(new AllureRestAssured()).contentType(ContentType.JSON).body(putAPIRequestBody).header("Cookie", "token=" + token)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().put("{bookingId}", bookingId).then()
					.assertThat().statusCode(200).body("firstname", Matchers.equalTo("James"))
					.body("additionalneeds", Matchers.equalTo("Super bowl"));

			// Patch API call
			RestAssured.given().filter(new AllureRestAssured()).contentType(ContentType.JSON).body(patchAPIRequestBody)
					.header("Cookie", "token=" + token).baseUri("https://restful-booker.herokuapp.com/booking").when()
					.patch("{bookingId}", bookingId).then().assertThat().statusCode(200)
					.body("firstname", Matchers.equalTo("John"));

			// Delete API call
			RestAssured.given().filter(new AllureRestAssured()).contentType(ContentType.JSON).header("Cookie", "token=" + token)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().delete("{bookingId}", bookingId)
					.then().assertThat().statusCode(201);

			logger.info("Delete API Request ended...");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
