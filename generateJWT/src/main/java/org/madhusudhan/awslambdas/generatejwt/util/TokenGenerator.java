package org.madhusudhan.awslambdas.generatejwt.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class TokenGenerator {
	private static String ISSUER = "madhusudhan";
	private static String AUDIENCE = "test audience";
	private static String EXPIRES_IN_SECONDS = "120";

	// Must be atleast 128 bits long
	private static String SECRET_KEY = "secretKeysecretKeysecretKeysecretKey";

	public static String generateToken(String userName) throws JOSEException, NoSuchAlgorithmException {

		int tokenExpiry = Integer.parseInt(EXPIRES_IN_SECONDS);

		byte[] secret = SECRET_KEY.getBytes();

		JWSSigner signer = new MACSigner(secret);

		JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();

		String audienceArray[] = AUDIENCE.split(",");
		claimsSetBuilder.audience(Arrays.asList(audienceArray));
		// substitute with what ever claims you want.
		String claims = String.format("name:%s", userName);

		String claimArray[] = claims.split(",");

		for (int i = 0; i < claimArray.length; i++) {
			String[] currentClaimArray = claimArray[i].split(":");
			String claimHeader = currentClaimArray[0].trim();
			String claimBody = currentClaimArray[1].trim();
			claimsSetBuilder.claim(claimHeader, claimBody);
		}

		claimsSetBuilder.issuer(ISSUER);

		Date now = new Date();
		Date expiresAt = new Date((now.getTime() + (tokenExpiry * 5000)));
		claimsSetBuilder.expirationTime(expiresAt);
		JWTClaimsSet claimSet = claimsSetBuilder.build();

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);

		signedJWT.sign(signer);

		String token = signedJWT.serialize();
		return token;
	}

}