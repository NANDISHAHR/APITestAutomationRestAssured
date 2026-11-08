package com.testautoamtion.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.testautoamtion.apitesting.utils.FileNameConstants;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class PostApiRequestUsingPojos {

	@Test
	public void postApiRequest() {

		try {

			String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.JSON_SCHEMA), "UTF-8");

			BookingDates bookingDates = new BookingDates("2026-01-01", "2026-01-03");
			Booking booking = new Booking("Jim", "Brown", 1000, true, "Breakfast", bookingDates);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

			System.out.println(requestBody);

			// de-serialization
			Booking bookingDetails = objectMapper.readValue(requestBody, Booking.class);
			System.out.println(bookingDetails.getFirstname());
			System.out.println(bookingDetails.getTotalprice());

			System.out.println(bookingDetails.getBookingdates().getCheckin());
			System.out.println(bookingDetails.getBookingdates().getCheckout());

			Response response = RestAssured.given().contentType(ContentType.JSON).body(requestBody)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().post().then().statusCode(200)
					.extract().response();

			int bookingId = response.path("bookingid");

			// System.out.println(jsonSchema);

			RestAssured.given().contentType(ContentType.JSON).baseUri("https://restful-booker.herokuapp.com/booking")
					.when().get("/{bookingId}", bookingId).then().assertThat().statusCode(200)
					.body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));

		} catch (JacksonException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
