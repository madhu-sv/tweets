package org.madhusudhan.awslambdas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.madhusudhan.awslambdas.model.Tweet;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class TweetsHandler implements RequestStreamHandler {
	private JSONParser parser = new JSONParser();
	private static final String DYNAMO_DB_TABLE_NAME = System.getenv("TABLE_NAME");
	private static final String DYNAMO_DB_ENDPOINT = System.getenv("DYNAMO_DB_ENDPOINT");
	private static final String REGION = System.getenv("REGION");

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDb = new DynamoDB(client);
		try {
			JSONObject event = (JSONObject) parser.parse(reader);

			if (event.get("body") != null) {
				Gson gson = new Gson();
				Tweet tweet = gson.fromJson((String) event.get("body"), Tweet.class);
				Item item = new Item().withPrimaryKey("id", tweet.getId())
						.withString("user", tweet.getUser())
						.withBoolean("liked", tweet.isLiked())
						.withBoolean("retweeted", tweet.isRetweeted())
						.withString("text", tweet.getText());

				dynamoDb.getTable(DYNAMO_DB_TABLE_NAME)
						.putItem(new PutItemSpec().withItem(item));
			}

			JSONObject responseBody = new JSONObject();
			responseBody.put("message", "New item created");

			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");

			responseJson.put("statusCode", 200);
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());

		} catch (ParseException pex) {
			responseJson.put("statusCode", 400);
			responseJson.put("exception", pex);
		}

		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public void handleGetTweetByParam(InputStream inputStream, OutputStream outputStream, Context context)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder
				.standard()
				.withEndpointConfiguration(new EndpointConfiguration(DYNAMO_DB_ENDPOINT, REGION))
				.build();
		DynamoDB dynamoDb = new DynamoDB(client);

		Item result = null;
		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			JSONObject responseBody = new JSONObject();

			if (event.get("pathParameters") != null) {

				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("id") != null) {

					int id = Integer.parseInt((String) pps.get("id"));
					result = dynamoDb.getTable(DYNAMO_DB_TABLE_NAME).getItem("id", id);
				}

			} else if (event.get("queryStringParameters") != null) {

				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("id") != null) {

					int id = Integer.parseInt((String) qps.get("id"));
					result = dynamoDb.getTable(DYNAMO_DB_TABLE_NAME).getItem("id", id);
				}
			}
			if (result != null) {
				Gson gson = new Gson();
				Tweet tweet = gson.fromJson(result.toJSON(), Tweet.class);

				responseBody.put("Tweet", tweet);
				responseJson.put("statusCode", 200);
			} else {

				responseBody.put("message", "No item found");
				responseJson.put("statusCode", 404);
			}

			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");

			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());

		} catch (ParseException pex) {
			responseJson.put("statusCode", 400);
			responseJson.put("exception", pex);
		}

		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();
	}
	
	@SuppressWarnings("unchecked")
	public void handleGetTweetsForUserByParam(InputStream inputStream, OutputStream outputStream, Context context)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder
				.standard()
				.withEndpointConfiguration(new EndpointConfiguration(DYNAMO_DB_ENDPOINT, REGION))
				.build();
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		List<Tweet> tweets = null;
		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			JSONObject responseBody = new JSONObject();
			if (event.get("pathParameters") != null) {

				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("user") != null) {
					String user = pps.get("user").toString();
					eav.put(":val1", new AttributeValue().withS(user));
					DynamoDBQueryExpression<Tweet> queryExpression = new DynamoDBQueryExpression<Tweet>()
				            .withKeyConditionExpression("User = :val1").withExpressionAttributeValues(eav);
					tweets = mapper.query(Tweet.class, queryExpression);

				}

			} else if (event.get("queryStringParameters") != null) {

				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("id") != null) {

					String user = qps.get("user").toString();
					eav.put(":val1", new AttributeValue().withS(user));
					DynamoDBQueryExpression<Tweet> queryExpression = new DynamoDBQueryExpression<Tweet>()
				            .withKeyConditionExpression("User = :val1").withExpressionAttributeValues(eav);
					tweets = mapper.query(Tweet.class, queryExpression);
				}
			}
			
			if(tweets != null) {
				responseBody.put("Tweets", tweets);
				responseJson.put("statusCode", 200);
			} else {
				responseBody.put("message", "No tweets found");
				responseJson.put("statusCode", 404);
			}
			
			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");

			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());
			
			
		} catch (ParseException pex) {
			responseJson.put("statusCode", 400);
			responseJson.put("exception", pex);
		}

		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toString());
		writer.close();
	}


}
