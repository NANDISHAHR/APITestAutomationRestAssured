package com.testautoamtion.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautoamtion.apitesting.utils.BaseTest;
import com.testautoamtion.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PostApiRequestUsingFile extends BaseTest {

	@Test
	public void postApiRequest() {

		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),
					"UTF-8");
			// System.out.println(postAPIRequestBody);

			Response response = RestAssured.given().contentType(ContentType.JSON).body(postAPIRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().post().then().assertThat()
					.statusCode(200).extract().response();

			JSONArray jsonArrayFirstName = JsonPath.read(response.body().asString(), "$.booking..firstname");
			String firstName = (String) jsonArrayFirstName.get(0);
			Assert.assertEquals(firstName, "Jim");

			JSONArray jsonArrayLastName = JsonPath.read(response.body().asString(), "$.booking..lastname");
			String lastName = (String) jsonArrayLastName.get(0);
			Assert.assertEquals(lastName, "Brown");

			JSONArray jsonArraybookingdates = JsonPath.read(response.body().asString(),
					"$.booking.bookingdates..checkin");
			String checkin = (String) jsonArraybookingdates.get(0);
			Assert.assertEquals(checkin, "2018-01-01");

			JSONArray jsonArraybookingCheckOutdates = JsonPath.read(response.body().asString(),
					"$.booking.bookingdates..checkout");
			String checkout = (String) jsonArraybookingCheckOutdates.get(0);
			Assert.assertEquals(checkout, "2019-01-01");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
