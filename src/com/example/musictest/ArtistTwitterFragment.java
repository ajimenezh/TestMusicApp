package com.example.musictest;


import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;



public class ArtistTwitterFragment extends Fragment{
	
	private Context mContext;
	private String artistQuery;
	private Artist artist = new Artist();

	public ArtistTwitterFragment(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.twitter_layout, container, false);
		
		mContext = getActivity();
         
		artistQuery = getArguments().getString("query");
		
        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		WebView myWebView = (WebView) getView().findViewById(R.id.webView);
		
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		  
		String data = "<a class=\"twitter-timeline\" href=\"https://twitter.com/alex_j05\" data-widget-id=\"493434458557538305\">Tweets por @alex_j05</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>";
        myWebView.loadData(data, "text/html; charset=UTF-8", null);
	}
	

}