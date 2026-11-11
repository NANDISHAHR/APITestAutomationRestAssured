package com.testautoamtion.apitesting.tests;

import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.testautoamtion.apitesting.utils.FileNameConstants;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomationapitesting.listners.RestAssuredListeners;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class DatadrivenTestingUsingExcelFile {
	@Test(dataProvider = "Excel Test Data")
	public void dataDrivenTestingUsingExcelFile(Map<String, String> testData) {

		try {

			int totalPrice = Integer.parseInt(testData.get("TotalPrice"));
			BookingDates bookingDates = new BookingDates("2026-01-01", "2026-01-03");
			Booking booking = new Booking(testData.get("FirstName"), testData.get("LastName"), totalPrice, true,
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

	@DataProvider(name = "Excel Test Data")
	public Object[][] getTestData() {

		String query = "select * from sheet1 where Run='Yes'";

		Object[][] objArray = null;
		Map<String, String> testData = null;
		List<Map<String, String>> testDataList = null;

		Fillo fillo = new Fillo();
		Connection connection = null;
		Recordset recordset = null;

		try {
			connection = fillo.getConnection(FileNameConstants.EXCEL_TEST_DATA);
			recordset = connection.executeQuery(query);

			testDataList = new ArrayList<Map<String, String>>();
			while (recordset.next()) {
				testData = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

				for (String field : recordset.getFieldNames()) {
					testData.put(field, recordset.getField(field));
				}
				testDataList.add(testData);
			}

			objArray = new Object[testDataList.size()][1];
			for (int i = 0; i < testDataList.size(); i++) {
				objArray[i][0] = testDataList.get(i);
			}

		} catch (FilloException e) {
			e.printStackTrace();
		}

		return objArray;
	}

}
