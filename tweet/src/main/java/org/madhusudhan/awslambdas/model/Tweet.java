package org.madhusudhan.awslambdas.model;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "tweet")
public class Tweet implements Serializable {
	
    private String id;
    private User user;
    private Entities entities;
    private boolean favorite;
    private boolean retweeted;
    private String text;

	@DynamoDBHashKey(attributeName="Id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName="user")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@DynamoDBAttribute(attributeName="entities")
	public Entities getEntities() {
		return entities;
	}
	public void setEntities(Entities entities) {
		this.entities = entities;
	}

	@DynamoDBAttribute(attributeName="favorite")
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@DynamoDBAttribute(attributeName="retweeted")
	public boolean isRetweeted() {
		return retweeted;
	}
	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	@DynamoDBAttribute(attributeName="text")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
