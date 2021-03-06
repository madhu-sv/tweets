package org.madhusudhan.awslambdas.tokenauthorizer;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.madhusudhan.awslambdas.validatejwt.TokenValidator;

import com.amazonaws.lambda.io.AuthPolicy;
import com.amazonaws.lambda.io.TokenAuthorizerContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nimbusds.jose.JOSEException;

/**
 * Handles IO for a Java Lambda function as a custom authoriser for API Gateway
 *
 */
public class LambdaAuthorizer implements RequestHandler<TokenAuthorizerContext, AuthPolicy> {

	@Override
	public AuthPolicy handleRequest(TokenAuthorizerContext input, Context context) {

		String token = input.getAuthorizationToken();

		context.getLogger().log("Token sent : " + token);

		String principalId = "xxxx";

		// if the client token is not recognised or invalid
		// you can send a 401 Unauthorised response to the client by failing like so:
		// throw new RuntimeException("Unauthorised");

		// if the token is valid, a policy should be generated which will allow or deny
		// access to the client

		// if access is denied, the client will receive a 403 Access Denied response
		// if access is allowed, API Gateway will proceed with the back-end integration
		// configured on the method that was called

		String methodArn = input.getMethodArn();
		String[] arnPartials = methodArn.split(":");
		String region = arnPartials[3];
		String awsAccountId = arnPartials[4];
		String[] apiGatewayArnPartials = arnPartials[5].split("/");
		String restApiId = apiGatewayArnPartials[0];
		String stage = apiGatewayArnPartials[1];
		String httpMethod = apiGatewayArnPartials[2];
		String resource = "*";
		if (apiGatewayArnPartials.length == 4) {
			resource = apiGatewayArnPartials[3];
		}

		// this function must generate a policy that is associated with the recognised
		// principal user identifier.
		// depending on your use case, you might store policies in a DB, or generate
		// them on the fly

		// keep in mind, the policy is cached for 5 minutes by default (TTL is
		// configurable in the authoriser)
		// and will apply to subsequent calls to any method/resource in the RestApi
		// made with the same token

		// the example policy below denies access to all resources in the RestApi

		String isValidToken;
		try {
			isValidToken = TokenValidator.validateToken(token);
		} catch (NoSuchAlgorithmException | ParseException | JOSEException e) {
			System.out.println("Invalid Token 1");
			return new AuthPolicy(principalId,
					AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage));
		}

		if (TokenValidator.VALID_TOKEN.equals(isValidToken)) {
			System.out.println("Valid Token");
			return new AuthPolicy(principalId,
					AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage));
		} else {
			System.out.println("Invalid Token");
			return new AuthPolicy(principalId,
					AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage));
		}
	}

}
