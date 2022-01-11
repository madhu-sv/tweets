package org.madhusudhan.awslambdas.model;


import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class User implements Serializable {
	private String description;
	private Integer followersCount;
	private String userId;
	private String userName;
	private boolean verified;

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
