package org.madhusudhan.awslambdas;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

import org.madhusudhan.awslambdas.generatejwt.model.Request;
import org.madhusudhan.awslambdas.generatejwt.util.TokenGenerator;
import org.madhusudhan.awslambdas.generatejwt.util.UserDetailsCache;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.nimbusds.jose.JOSEException;

public class GenerateJWTFunctionHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final String SECRET_NAME = System.getenv("SECRET_NAME"); // Replace with the secret name or make it a
																			// config property
	private Map<String, String> userDetailsCache = UserDetailsCache.getInstance().getUserMap(SECRET_NAME);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		String body = input.getBody();
		Gson gson = new Gson();
		Request request = gson.fromJson(body, Request.class);
		String password = request.getPassword();
		String userName = request.getUsername();

		if (userDetailsCache.containsKey(userName) && (password.equals(userDetailsCache.get(userName)))) {
			try {
				return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(Collections.emptyMap())
						.withBody("{\"token\": " + getSuccessfulResponse(userName) + "\"}");
			} catch (NoSuchAlgorithmException | JOSEException e) {
				return getInvalidCredentialsResponse();
			}
		} else {
			context.getLogger().log(String.format("User credentials mismatch for user %s", userName));
			return getInvalidCredentialsResponse();
		}
	}

	private APIGatewayProxyResponseEvent getInvalidCredentialsResponse() {
		return new APIGatewayProxyResponseEvent().withStatusCode(403).withHeaders(Collections.emptyMap())
				.withBody("{\"error\": \"client_error\",\n \"message\": \"Unauthorized\"}");
	}

	private String getSuccessfulResponse(String userName) throws NoSuchAlgorithmException, JOSEException {
		return TokenGenerator.generateToken(userName);
	}

}
