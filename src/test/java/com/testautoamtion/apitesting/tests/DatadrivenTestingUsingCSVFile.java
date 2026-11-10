package com.testautoamtion.apitesting.tests;

import org.testng.annotations.Test;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.testautoamtion.apitesting.utils.FileNameConstants;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomationapitesting.listners.RestAssuredListeners;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class DatadrivenTestingUsingCSVFile {
	@Test(dataProvider = "CSVTestData")
	public void dataDrivenTestingUsingCSV(Map<String, String> testData) {

		try {

			int totalPrice = Integer.parseInt(testData.get("totalprice"));
			BookingDates bookingDates = new BookingDates("2026-01-01", "2026-01-03");
			Booking booking = new Booking(testData.get("firstname"), testData.get("lastname"), totalPrice, true,
					"Breakfast", bookingDates);

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

	@DataProvider(name = "CSVTestData")
	public Object[][] getCSVTestData() {

		Object[][] objArray = null;
		Map<String, String> map = null;
		List<Map<String, String>> testDataList = null;

		try {
			CSVReader csvReader = new CSVReader(new FileReader(FileNameConstants.CSV_TEST_DATA));

			testDataList = new ArrayList<Map<String, String>>();
			String[] line = null;
			int count = 0;

			while ((line = csvReader.readNext()) != null) {

				if (count == 0) {
					count++;
					continue;
				}
				map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
				map.put("firstname", line[0]);
				map.put("lastname", line[1]);
				map.put("totalprice", line[2]);

				testDataList.add(map);
			}

			objArray = new Object[testDataList.size()][1];

			for (int i = 0; i < testDataList.size(); i++) {
				objArray[i][0] = testDataList.get(i);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return objArray;

	}
}
