package com.testautoamtion.apitesting.tests;

import org.testng.annotations.Test;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautoamtion.apitesting.utils.FileNameConstants;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomationapitesting.listners.RestAssuredListeners;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class DataDrivenTestingUsingJsonFile {
	@Test(dataProvider = "getTestData")
	public void datadrivenTestingUsingJson(LinkedHashMap<String, String> testData) {

		try {

			BookingDates bookingDates = new BookingDates("2026-01-01", "2026-01-03");
			Booking booking = new Booking(testData.get("firstname"), testData.get("lastname"), 1000, true, "Breakfast",
					bookingDates);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

			System.out.println(requestBody); 

			// de-serialization
			Booking bookingDetails = objectMapper.readValue(requestBody, Booking.class);
			System.out.println(bookingDetails.getFirstname());
			System.out.println(bookingDetails.getTotalprice());

			System.out.println(bookingDetails.getBookingdates().getCheckin());
			System.out.println(bookingDetails.getBookingdates().getCheckout());

			Response response = RestAssured.given().filter(new RestAssuredListeners()).contentType(ContentType.JSON)
					.body(requestBody).baseUri("https://restful-booker.herokuapp.com/booking").when().post().then()
					.statusCode(200).extract().response();

		} catch (JacksonException e) {
			e.printStackTrace();
		}

	}

	@DataProvider(name = "getTestData")
	public Object[] getTestDataUsingJson() {

		Object[] obj = null;
		try {
			String jsonTestData = FileUtils.readFileToString(new File(FileNameConstants.JSON_TEST_DATA), "UTF-8");
			JSONArray jsonArray = JsonPath.read(jsonTestData, "$");
			obj = new Object[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				obj[i] = jsonArray.get(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return obj;
	}
}
