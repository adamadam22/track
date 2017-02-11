package com.track.backend.utilities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TextFeedback {
	
	private int statusCode;
	private String text;

	public static TextFeedback create(int statusCode, String text){
		TextFeedback feedback = new TextFeedback();
		feedback.setText(text);
		feedback.setStatusCode(statusCode);
		return feedback;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	@XmlElement
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getText() {
		return text;
	}

	@XmlElement
	public void setText(String text) {
		this.text = text;
	}

}
