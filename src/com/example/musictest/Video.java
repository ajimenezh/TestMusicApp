package com.example.musictest;


public class Video {
	
	private String type;
	private String title;
	private String image;
	private String id;
	
	public Video(){
		
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
	
	public void setId(String id) {
		this.id = id;
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
	
	public String getId() {
		return this.id;
	}
	
}