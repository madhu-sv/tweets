package org.madhusudhan.awslambdas.model;

import java.io.Serializable;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBDocument
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entities implements Serializable {
	@DynamoDBAttribute
	private List<String> hashTags;
	
	@DynamoDBAttribute
	private List<String> URLs;
	
	public List<String> getHashTags() {
		return hashTags;
	}
	
	public void setHashTags(List<String> hashTags) {
		this.hashTags = hashTags;
	}
	
	public List<String> getURLs() {
		return URLs;
	}
	
	public void setURLs(List<String> uRLs) {
		URLs = uRLs;
	}
}
