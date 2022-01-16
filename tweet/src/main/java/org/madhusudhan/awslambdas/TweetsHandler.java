package org.madhusudhan.awslambdas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.madhusudhan.awslambdas.exception.TweetSizeExceededException;
import org.madhusudhan.awslambdas.model.Tweet;
import org.madhusudhan.awslambdas.model.User;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TweetsHandler implements RequestStreamHandler {
	private JSONParser parser = new JSONParser();
	private static final String DYNAMODB_TABLE_NAME = System.getenv("DYNAMODB_TABLE_NAME");
	private static final int MAX_TWEET_TEXT_SIZE = 160; // Can me it part of config by using something like - Integer.parseInt(System.getenv("MAX_TWEET_TEXT_SIZE"));

	private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	private DynamoDBMapper mapper = new DynamoDBMapper(client);
	DynamoDB dynamoDb = new DynamoDB(client);
	private Gson gson = new Gson();


	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		JSONObject responseJson = new JSONObject();

		try {
			JSONObject event = (JSONObject) parser.parse(reader);

			if (event.get("body") != null) {
				String body  = event.get("body").toString();
				Tweet tweet = new Tweet(body);
				context.getLogger().log(String.format("Event body: %s", body));
				context.getLogger().log(String.format("Id from input : %s", tweet.getId()));
				if (tweet.getText().length() <= MAX_TWEET_TEXT_SIZE) {
					dynamoDb.getTable(DYNAMODB_TABLE_NAME)
                    .putItem(Item.fromJSON(body));
				} else {
					throw new TweetSizeExceededException("Tweet text exceeded the size limit");
				}	
			}

			JSONObject responseBody = new JSONObject();
			responseBody.put("message", "New tweet created");

			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");

			responseJson.put("statusCode", 200);
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());

		} catch (ParseException pex) {
			responseJson.put("statusCode", 400);
			responseJson.put("exception", pex);
		} catch (TweetSizeExceededException e) {
			responseJson.put("statusCode", 400);
			responseJson.put("exception", e);
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

		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			JSONObject responseBody = new JSONObject();
			Tweet result = null;
			if (event.get("pathParameters") != null) {

				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("id") != null) {

					String id  = (String)pps.get("id");
					result = mapper.load(Tweet.class, id);
					context.getLogger().log(String.format("Tweet with id %s retrieved successfully", id));
				}

			} else if (event.get("queryStringParameters") != null) {

				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("id") != null) {
					String id  = (String)qps.get("id");
					result = mapper.load(Tweet.class, id);
					context.getLogger().log(String.format("Tweet with id %s retrieved successfully", id));
				}
			}
			if (result != null) {
				responseBody.put("Tweet", result);
				responseJson.put("statusCode", 200);
			} else {

				responseBody.put("message", "No tweet found");
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
		
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		List<Tweet> tweets = null;
		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			JSONObject responseBody = new JSONObject();
			if (event.get("pathParameters") != null) {

				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("user") != null) {
					Type userMapType = new TypeToken<Map<String, User>>() {}.getType();
					Map userMap = gson.fromJson((String)pps.get("user"), userMapType);

					eav.put(":val1", new AttributeValue().withM(userMap));
					DynamoDBQueryExpression<Tweet> queryExpression = new DynamoDBQueryExpression<Tweet>()
				            .withKeyConditionExpression("User = :val1").withExpressionAttributeValues(eav);
					tweets = mapper.query(Tweet.class, queryExpression);

				}

			} else if (event.get("queryStringParameters") != null) {

				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("user") != null) {
					
					Type userMapType = new TypeToken<Map<String, User>>() {}.getType();
					Map userMap = gson.fromJson((String)qps.get("user"), userMapType);

					eav.put(":val1", new AttributeValue().withM(userMap));
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
