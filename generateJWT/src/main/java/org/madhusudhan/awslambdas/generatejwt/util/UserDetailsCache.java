package org.madhusudhan.awslambdas.generatejwt.util;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

public class UserDetailsCache {
	private static final String SM_ENDPOINT_URL = System.getenv("SM_ENDPOINT_URL");
	private Map<String, String> userDetailsCache;
	
	
	private UserDetailsCache(){
		this.userDetailsCache = new HashMap<>();
	}
    
    private static class SingletonHelper{
        private static final UserDetailsCache INSTANCE = new UserDetailsCache();
    }
    
    public static UserDetailsCache getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
	public Map<String,String> getUserMap(String secretName) {
		Region region = Region.EU_WEST_2; // Change to desired region or make it a config property
		SecretsManagerClient secretsClient;
		try {
			secretsClient = SecretsManagerClient.builder()
			        .region(region)
			        .endpointOverride(new URI(SM_ENDPOINT_URL))
			        .build();
			return getSecretValue(secretsClient, secretName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, String> getSecretValue(SecretsManagerClient secretsClient, String secretName) {
		if(this.userDetailsCache.isEmpty() || !this.userDetailsCache.containsKey(secretName)) {
			try {
	            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
	                .secretId(secretName)
	                .build();

	            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
	            String secret = valueResponse.secretString();
	            Gson gson = new Gson();
	            Type type = new TypeToken<Map<String, String>>(){}.getType();
	            this.userDetailsCache = gson.fromJson(secret, type);
	           
	        } catch (SecretsManagerException e) {
	            System.err.println(e.awsErrorDetails().errorMessage());
	        }
		}
		
		return userDetailsCache;
    }
	

}
