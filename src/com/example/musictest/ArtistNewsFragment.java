package com.example.musictest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class ArtistNewsFragment extends Fragment{
	
	private Context mContext;
	private String artistQuery;
	private Artist artist = new Artist();
	private HashMap<String, ArrayList<String>> albums = new HashMap<String, ArrayList<String>>();
	
    private int infoLoaded = 0;
    
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    
    private ArrayList<News> artistNews = new ArrayList<News>();
	
	public ArtistNewsFragment(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.artist_news_fragment, container, false);
		
		mContext = getActivity();
         
		artistQuery = getArguments().getString("query");
		
		//(new getNewsReddit()).execute(artistQuery);
		(new getGoogleNews()).execute(artistQuery);
		
        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	private class getNewsReddit extends AsyncTask<String, Void, Void> {


		@Override
		   protected void onPreExecute() {

		        super.onPreExecute();
		   }

		   protected Void doInBackground(String... args) {

				String query = args[0];
				
				for (int i=0; i<query.length(); i++) {
					if (query.charAt(i)==' ') {
						query = query.substring(0,i) + query.substring(i+1, query.length());
					}
				}
				
				Log.w("query", query);
				
				if (query != null) {
				
					for (int i=0; i<query.length(); i++) {
						if (query.charAt(i)==' ') {
							query = query.substring(0, i) + "%20" + query.substring(i+1, query.length()); 
						}
					}
		
					String url = "http://www.reddit.com/r/" + query + ".json";
					
					JSONObject response = getJSONfromURL(url);
					
					if (response != null) {
		
						try {
							if (response.has("data") && ((JSONObject) response.get("data")).has("children")) {
								
								try {
									JSONArray array = (JSONArray) ((JSONObject) response.get("data")).get("children");
									
									for (int i=0; i<array.length(); i++) {
										
										//Log.w("hello", "reddit  " + i);
										
										JSONObject obj = (JSONObject) ((JSONObject)array.get(i)).get("data");
										
										String domain = obj.getString("domain");
										
										Log.w("hello", domain);
										
										if (domain.equals("imgur.com") || domain.contains("imgur") || (domain.length()>4 && domain.substring(0,5).equals("self."))) continue;
										
										if (domain.equals("youtube.com")) {
											
										}
										else {
											
											Log.w("hello", "Pasa");
											
											String thumbnail = obj.getString("thumbnail");
											String link = obj.getString("url");
											String title = obj.getString("title");
											
											News news = new News();
											news.setImage(thumbnail);
											news.setLink(link);
											news.setTitle(title);
											news.setType("reddit");
											
											artistNews.add(news);
										}
										
									}
									
									
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				
//				 runOnUiThread(new Runnable() {
//							 
//					 @Override
//					 public void run() {
//						 
//							ProgressBar pg = (ProgressBar) findViewById(R.id.progressBar1);
//							pg.setVisibility(View.GONE);
//							
//							RelativeLayout rl = (RelativeLayout) findViewById(R.id.homeArtistLayout);
//							rl.setVisibility(View.VISIBLE);
//							
//							//TextView tv1 = new TextView(mContext);
//							//LinearLayout layout = new LinearLayout(mContext);
//							//getActivity().setContentView(layout);
//							//layout.setOrientation(LinearLayout.VERTICAL);
//							//tv1.setText("Hola mundo");
//						    //layout.addView(tv1);
//						    
//					 }
//				 });

				return null;
		   }


		   
		protected void onPostExecute(Void result) {
				Log.w("hello", "message sent");
			   Message message = new Message();
			   message.obj = "-";
			   ArtistNewsFragment.this.AsynkTaskHandler.sendMessage(message);
		   }
	}
	
	private class getGoogleNews extends AsyncTask<String, Void, Void> {


		@Override
		   protected void onPreExecute() {

		        super.onPreExecute();
		   }

		   protected Void doInBackground(String... args) {

				String query = args[0];
				
				for (int i=0; i<query.length(); i++) {
					if (query.charAt(i)==' ') {
						query = query.substring(0,i) + "+" + query.substring(i+1, query.length());
					}
				}
				
				Log.w("query", query);
				
				if (query != null) {
				
					for (int i=0; i<query.length(); i++) {
						if (query.charAt(i)==' ') {
							query = query.substring(0, i) + "%20" + query.substring(i+1, query.length()); 
						}
					}
					
					String url = "https://news.google.com/news/feeds?q=" + query + "&output=rss";
					
//					XMLReader xr;
//					try {
//						xr = new XMLReader();
//						xr.read(url);
//					} catch (SAXException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (ParserConfigurationException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			        DocumentBuilder db;
					try {
						db = dbf.newDocumentBuilder();
				        Document document = db.parse(new InputSource(new URL(url).openStream()));
				        NodeList nodeList = document.getElementsByTagName("item");
				        for(int x=0,size= nodeList.getLength(); x<size; x++) {
				        	Node item = nodeList.item(x);
				        	item.getFirstChild().getTextContent();
				        	Log.w("hello", item.getFirstChild().getNodeName() + " " + item.getFirstChild().getNodeValue() + " " + item.getFirstChild().getTextContent());
				        }
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

				return null;
		   }


		   
		protected void onPostExecute(Void result) {
				Log.w("hello", "message sent");
			   Message message = new Message();
			   message.obj = "-";
			   ArtistNewsFragment.this.AsynkTaskHandler.sendMessage(message);
		   }
	}
	
	private class getImageArtist extends AsyncTask<String, Void, Void> {

		   @Override
		   protected void onPreExecute() {

		        super.onPreExecute();
		   }

		   protected Void doInBackground(String... args) {

				String query = args[0];
				
				if (query != null) {
				
					for (int i=0; i<query.length(); i++) {
						if (query.charAt(i)==' ') {
							query = query.substring(0, i) + "%20" + query.substring(i+1, query.length()); 
						}
					}
					
					String url = "https://api.spotify.com/v1/search?q=" + query + "&type=artist&limit=1";
					
					JSONObject response = getJSONfromURL(url);
					
					if (response != null) {
		
						try {
							if (response.has("artists") && ((JSONObject) response.get("artists")).has("items")) {
								
								try {
									JSONArray array = (JSONArray) ((JSONObject) response.get("artists")).get("items");
									
									if (array.length() > 0) {
										String spotifyId = (String) ((JSONObject)array.get(0)).get("id");
										JSONArray images = (JSONArray) ((JSONObject)array.get(0)).get("images");
										String spotifyImage = (String) ((JSONObject)images.get(0)).get("url");
										
									}
									
									
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				
//				 getActivity().runOnUiThread(new Runnable() {
//							 
//					 @Override
//					 public void run() {
//						 
//						 	try {
//								ProgressBar pg = (ProgressBar) getView().findViewById(R.id.progressBar1);
//								pg.setVisibility(View.GONE);
//								
//								RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.homeArtistLayout);
//								rl.setVisibility(View.VISIBLE);
//								
//								//TextView tv1 = new TextView(mContext);
//								//LinearLayout layout = new LinearLayout(mContext);
//								//getActivity().setContentView(layout);
//								//layout.setOrientation(LinearLayout.VERTICAL);
//								//tv1.setText("Hola mundo");
//							    //layout.addView(tv1);
//						 	}
//						 	finally{
//						 		
//						 	}
//						    
//					 }
//				 });

				return null;
		   }

		   protected void onPostExecute(Void result) {
			   Message message = new Message();
			   message.obj = "-";
			   ArtistNewsFragment.this.AsynkTaskHandler.sendMessage(message);
		   }
	}
	
	public static JSONObject getJSONfromURL(String url){
	     InputStream is = null;
	     String result = "";
	     JSONObject json = null;
	      try{
	         HttpClient httpclient = new DefaultHttpClient();
	         HttpGet httpget = new HttpGet(url);
	         HttpResponse response = httpclient.execute(httpget);
	         HttpEntity entity = response.getEntity();
	         is = entity.getContent();
	         
		      try{
		         BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		         StringBuilder sb = new StringBuilder();
		         String line = null;
		         while ((line = reader.readLine()) != null) {
		             sb.append(line + "\n");
		         }
		         is.close();
		         result=sb.toString();
		         
			     try{
			         json = new JSONObject(result);

			     }catch(JSONException e){
			    	 
			     }
		     } catch(Exception e){
		    	 
		     }
	     }catch(Exception e){
	    	 
	     }



	      return json;
	 }
	


	// Given a string representation of a URL, sets up a connection and gets
	// an input stream.
	private InputStream downloadUrl(String urlString) throws IOException {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    // Starts the query
	    conn.connect();
	    return conn.getInputStream();
	}
	
	Handler AsynkTaskHandler = new Handler() {

	public void handleMessage(Message msg) {
     	infoLoaded++;
     	
     	drawNews();
     	
     	if (infoLoaded==1) {
     		
     		//Log.w("hello", artist.getName());
     		
			 getActivity().runOnUiThread(new Runnable() {
			 
				 @Override
				 public void run() {
					 ProgressBar pg = (ProgressBar) getView().findViewById(R.id.progressBar1);
						pg.setVisibility(View.GONE);
						
						RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.homeArtistLayout);
						rl.setVisibility(View.VISIBLE);
											    
				 }
			});
     	}
     }
 };

 	private void drawNews() {
 		getActivity().runOnUiThread(new Runnable() {
		 
 			@Override
 			public void run() {
 				
 				for (int i=0; i<artistNews.size(); i++) {
 					
 					final News news = artistNews.get(i);
 				
					Log.w("hello", news.getTitle());
					
					LinearLayout lv = (LinearLayout) getView().findViewById(R.id.artist_news);
//					TextView tv = new TextView(mContext);
//					tv.setId(50); 
//					tv.setText(news.getTitle());
//					tv.setTextSize(20);
//					LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//					llp.setMargins(40, 20, 0, 10); // llp.setMargins(left, top, right, bottom);
//					tv.setLayoutParams(llp);
//					lv.addView(tv);
					
					LayoutInflater inflater =
						    (LayoutInflater)getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
					
					View view = inflater.inflate( R.layout.reddit_news_item, null );
					TextView tv = (TextView) view.findViewById(R.id.news_title);
					tv.setText(news.getTitle());
					
					view.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {

							Intent internetIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(news.getLink()));
							internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
							internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							getActivity().startActivity(internetIntent);
							
						}
					});
					
					lv.addView(view);
 				}
 				
 			}
		});
		
 	}

}