package org.madhusudhan.awslambdas.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;




public class Tweet {
	
	private String text;
	private String user;
	private boolean liked;
	private boolean retweeted;
	private BigDecimal id;
	
	public Tweet(@NotBlank String text, @NotBlank String user, boolean liked, boolean retweeted, @NotNull BigDecimal id) {
		super();
		this.text = text;
		this.user = user;
		this.id = id;
		this.liked = liked;
		this.retweeted = retweeted;
	}
	
	public Tweet() {
		// TODO Auto-generated constructor stub
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public boolean isRetweeted() {
		return this.retweeted;
	}

	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	
}
