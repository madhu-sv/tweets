package org.madhusudhan.awslambdas.model;

import java.io.Serializable;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBDocument
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entities implements Serializable {
	private List<String> hashTags;
	private List<String> URLs;

	@DynamoDBIgnore
	public List<String> getHashTags() { return hashTags; }
	public void setHashTags(List<String> hashTags) { this.hashTags = hashTags; }
	
	@DynamoDBIgnore
	public List<String> getURLs() { return URLs; }
	public void setURLs(List<String> URLs) { this.URLs = URLs; }
}
