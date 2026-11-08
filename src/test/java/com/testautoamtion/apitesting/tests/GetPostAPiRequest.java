package com.testautoamtion.apitesting.tests;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.testautoamtion.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

public class GetPostAPiRequest extends BaseTest {

	@Test
	public void getCreatedBookingDetails() {

		JSONObject booking = new JSONObject();
		JSONObject bookingDates = new JSONObject();

		booking.put("firstname", "Jim");
		booking.put("lastname", "Brown");
		booking.put("totalprice", 1000);
		booking.put("depositpaid", true);
		booking.put("additionalneeds", "Breakfast");
		booking.put("bookingdates", bookingDates);

		bookingDates.put("checkin", "2026-01-01");
		bookingDates.put("checkout", "2027-01-01");

		Response response = RestAssured.given().contentType(ContentType.JSON).body(booking.toString())
				.baseUri("https://restful-booker.herokuapp.com/booking")
				.when().post().then().assertThat()
				.statusCode(200).body("booking.firstname", Matchers.equalTo("Jim"))
				.body("booking.totalprice", Matchers.equalTo(1000))
				.body("booking.bookingdates.checkin", Matchers.equalTo("2026-01-01"))
				.extract().response();
		
		
		int bookingId = response.path("bookingid");
		
		
		RestAssured.given().contentType(ContentType.JSON)
		             .pathParams("bookingID", bookingId)
		             .baseUri("https://restful-booker.herokuapp.com/booking")
		             .when()
		                .get("{bookingID}")
		                .then()
		                .assertThat()
		                .statusCode(200)
		                .body("firstname", Matchers.equalTo("Jim"))
		                .body("lastname", Matchers.equalTo("Brown"))
		                .body("additionalneeds", Matchers.equalTo("Breakfast"));
		
		
		
	}
}
