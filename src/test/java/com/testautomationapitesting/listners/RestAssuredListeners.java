package com.testautomationapitesting.listners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RestAssuredListeners implements Filter {

	private static final Logger logger = LogManager.getLogger(RestAssuredListeners.class);

	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext ctx) {

		Response response = ctx.next(requestSpec, responseSpec);
		//if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
			logger.info("\n Method => " + requestSpec.getMethod() + "\n URI => " + requestSpec.getURI()
					+ "\n Response Body => " + requestSpec.getBody()
					+ "\n Response Body => " + response.getBody().prettyPrint());
		//}
		return response;
	}

}
