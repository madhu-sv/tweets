package org.madhusudhan.awslambdas.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@DynamoDBDocument
public class User {
	private String description;
	private Integer followersCount;
	private String userId;
	private String userName;
	private boolean verified;

	public User() {
	}

	public User(String description, Integer followersCount, String userId, String userName, boolean verified) {
		this.description = description;
		this.followersCount = followersCount;
		this.userId = userId;
		this.userName = userName;
		this.verified = verified;
	}

	public User(String json) {
		Gson gson = new Gson();
		User input = gson.fromJson(json, User.class);
		this.description = input.description;
		this.followersCount = input.followersCount;
		this.userId = input.userId;
		this.userName = input.userName;
		this.verified = input.verified;
	}
	
	public String toString() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
	
	public String getDescription() {
		return description;
	}
	public Integer getFollowersCount() {
		return followersCount;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

}
