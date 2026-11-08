package com.testautoamtion.apitesting.tests;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.testautoamtion.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class PostApiRequest extends BaseTest {

	@Test
	public void createBooking() {

		// prepare - request body
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

		RestAssured.given().contentType(ContentType.JSON).body(booking.toString())
				.baseUri("https://restful-booker.herokuapp.com/booking")
				// .log().all()
				.when().post().then().assertThat()
				// .log().ifValidationFails()
				.statusCode(200).body("booking.firstname", Matchers.equalTo("Jim"))
				.body("booking.totalprice", Matchers.equalTo(1000))
				.body("booking.bookingdates.checkin", Matchers.equalTo("2026-01-01"));
	}
}
