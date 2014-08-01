package com.example.musictest;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;


public class Artist implements Parcelable, Serializable {
	
	private String echoNestId;
	private String name;
	private String spotifyId;
	private String spotifyImage;
	
	public Artist(){
		
	}
	
	public Artist(Parcel in)
	{
		echoNestId = in.readString();
		name = in.readString();
		spotifyId = in.readString();
		spotifyImage = in.readString();

	}
	
	public void setEchoNestId(String echoNestId) {
		this.echoNestId = echoNestId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}
	
	public void setSpotifyImage(String spotifyImage) {
		this.spotifyImage = spotifyImage;
	}
	
	public String getSpotifyImage() {
		return this.spotifyImage;
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(echoNestId);
		dest.writeValue(name);
		dest.writeValue(spotifyId);
		dest.writeValue(spotifyImage);

	}

	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {

	    public Artist createFromParcel(Parcel in)
	    {
	        return new Artist(in);
	    }

	    public Artist[] newArray(int size)
	    {
	        return new Artist[size];
	    }
	};

	public String getSpotifyId() {
		return this.spotifyId;
	}
	
}