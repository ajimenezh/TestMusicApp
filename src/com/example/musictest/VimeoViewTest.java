package com.example.musictest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class VimeoViewTest extends Fragment {
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.vimeo_view, container, false);
		
        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 
		
	    WebView myWebView = (WebView) getView().findViewById(R.id.webView);
	    myWebView.loadUrl("http://player.vimeo.com/video/23611770?autoplay=1");
		
	}
	
}