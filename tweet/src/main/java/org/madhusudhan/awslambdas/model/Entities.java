package org.madhusudhan.awslambdas.model;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.google.gson.Gson;

@DynamoDBDocument
public class Entities {
	private List<String> hashTags;
	private List<String> URLs;

	public Entities() {}

	public Entities(String json) {
		Gson g = new Gson();
		Entities entities = g.fromJson(json, Entities.class);
		if(entities != null && entities.URLs != null && entities.hashTags != null) {
			this.hashTags = entities.hashTags;
			this.URLs = entities.URLs;
		} 
		else {
			this.URLs = new ArrayList<>();
			this.hashTags = new ArrayList<>();
		}
	}

	public Entities(List<String> hashTags, List<String> urls) {
		this.URLs = urls;
		this.hashTags = hashTags;
	}

	@DynamoDBIgnore
	public List<String> getHashTags() { return hashTags; }
	public void setHashTags(List<String> hashTags) { this.hashTags = hashTags; }
	
	@DynamoDBIgnore
	public List<String> getURLs() { return URLs; }
	public void setURLs(List<String> URLs) { this.URLs = URLs; }
}
