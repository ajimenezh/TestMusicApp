package com.example.musictest;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.musictest.SearchSuggestionsListFragment.OnHeadlineSelectedListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class ArtistSearchFragment extends Fragment {
	
	private Context mContext;
	private String artistQuery;
	private int infoLoaded = 0;
	private List<String> artistsListName = new ArrayList<String>();
	private List<String> artistsListId = new ArrayList<String>();
	private Artist artist = new Artist();
	
	private OnHeadlineSelectedListener mCallback;
	
	public ArtistSearchFragment(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		artistQuery = getArguments().getString("query");
		(new getListArtists()).execute(artistQuery);
		if (artistQuery == null) {
			artistQuery = "";
		}
		else {
		}
		
		View rootView;

		rootView = inflater.inflate(R.layout.artist_search_fragment, container, false);
		
		mContext = getActivity();

         
        return rootView;
    }
	
	private class getListArtists extends AsyncTask<String, Void, Void> {

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
					
					String url = "https://api.spotify.com/v1/search?q=" + query + "&type=artist&limit=10";
					
					JSONObject response = getJSONfromURL(url);
					
					if (response != null) {
		
						try {
							if (response.has("artists") && ((JSONObject) response.get("artists")).has("items")) {
								
								try {
									JSONArray array = (JSONArray) ((JSONObject) response.get("artists")).get("items");
									
									for (int i=0; i<array.length(); i++ ) {
										String name = (String) ((JSONObject)array.get(i)).get("name");
										
										artistsListName.add(name);
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
				
				 MainActivity.getInstance().runOnUiThread(new Runnable() {
							 
					 @Override
					 public void run() {
						 
						 	try {
								ProgressBar pg = (ProgressBar) getView().findViewById(R.id.progressBar1);
								pg.setVisibility(View.GONE);
								
								RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.homeArtistLayout);
								rl.setVisibility(View.VISIBLE);
								
								ListView lv = (ListView) getView().findViewById(R.id.search_list);
								
								ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						                 getActivity(), 
						                 android.R.layout.simple_list_item_1,
						                 artistsListName );
	
						        lv.setAdapter(arrayAdapter); 
						        
						        lv.setOnItemClickListener(new OnItemClickListener() {
						        	
								    @Override 
								    public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
								    	mCallback.onSearchItemSelected(1, artistsListName.get(position));
								    }
						        });
						 	}
						 	finally {
						 		
						 	}
					        
						    
					 }
				 });

				return null;
		   }

		   protected void onPostExecute(Void result) {
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
	
	// Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSearchItemSelected(int position, String str);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	
}