package com.example.musictest;


public class News {
	
	private String type;
	private String title;
	private String image;
	private String link;
	private String data;
	
	public News(){
		
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getImage() {
		return this.image;
	}
	
	public String getLink() {
		return this.link;
	}
	
}