package com.example.musictest;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class ArtistHomeFragment extends Fragment implements ActionBar.TabListener{
	
	private static final String PREFS_NAME = "PrefsFile";
	private Context mContext;
	private String artistQuery;
	private Artist artist = new Artist();
	private HashMap<String, ArrayList<Integer>> albums = new HashMap<String, ArrayList<Integer>>();
	
    private int infoLoaded = 0;
    
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Top Rated", "Games", "Movies" };
	
	public ArtistHomeFragment(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.artist_home_fragment, container, false);
		
		mContext = getActivity();
         
		artistQuery = getArguments().getString("query");
		
		Log.w("en homefragment", artistQuery );
		
        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0); 
		String obj = prefs.getString(artistQuery, "");
		
		if (obj.length()==0) {
			Log.w("en homefragment", "Load everything" );
			(new getInfoArtist()).execute(artistQuery);
			(new getImageArtist()).execute(artistQuery);
		}
		else {
			
			try {
				artist = (Artist) fromString2(obj);
				
				ProgressBar pg = (ProgressBar) getView().findViewById(R.id.progressBar1);
				pg.setVisibility(View.GONE);
				
				RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.homeArtistLayout);
				rl.setVisibility(View.VISIBLE);
				
				TextView tv = (TextView) getView().findViewById(R.id.artist_name);
				
				tv.setText(artist.getName());
				
				String path = Environment.getExternalStorageDirectory() + "/" + artist.getSpotifyId() + ".jpg";
				File file = new File(path);
				if(!file.exists()) {  
					(new DownloadTask()).execute(artist.getSpotifyImage(), artist.getSpotifyId());
				}
				else {
					Bitmap myBitmap = BitmapFactory.decodeFile(path);

					try {
						ImageView imageview = (ImageView) getView().findViewById(R.id.artist_icon);
						if (imageview != null) imageview.setImageBitmap(myBitmap);
					}
					finally{}
				}
				
				(new LoadSongs()).execute(artist.getName());
				
			} catch (ClassNotFoundException e) {
				Log.e("Error", "ClassNotFoundException in loading artist from SharedPreferences");
				
				(new getInfoArtist()).execute(artistQuery);
				(new getImageArtist()).execute(artistQuery);
			} catch (IOException e) {
				Log.e("Error", "IOException in loading artist from SharedPreferences");
				
				(new getInfoArtist()).execute(artistQuery);
				(new getImageArtist()).execute(artistQuery);
			}
			
		}
		
	}
	
	private class getInfoArtist extends AsyncTask<String, Void, Void> {

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
		
					String url = "http://developer.echonest.com/api/v4/artist/search?api_key=DGQLABIJ9H2JL1ZP4&format=json&name=" + query + "&results=1";
					
					JSONObject response = getJSONfromURL(url);
					
					if (response != null) {
		
						try {
							if (response.has("response") && ((JSONObject) response.get("response")).has("artists")) {
								
								try {
									JSONArray array = (JSONArray) ((JSONObject) response.get("response")).get("artists");
									
									if (array.length() > 0) {
										String echoNestId = (String) ((JSONObject)array.get(0)).get("id");
										String name = (String) ((JSONObject)array.get(0)).get("name");
										
										artist.setEchoNestId(echoNestId);
										artist.setName(name);
										
										Log.w("Echo nest", name);
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
			   Message message = new Message();
			   message.obj = "-";
			   ArtistHomeFragment.this.AsynkTaskHandler.sendMessage(message);
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
										
										artist.setSpotifyId(spotifyId);
										artist.setSpotifyImage(spotifyImage);
										
										Log.w("Spotify", spotifyImage);
										
										String path = Environment.getExternalStorageDirectory() + "/" + spotifyId + ".jpg";
										File file = new File(path);
										if(!file.exists()) {  
											(new DownloadTask()).execute(spotifyImage, spotifyId);
										}
										else {
											Bitmap myBitmap = BitmapFactory.decodeFile(path);

											try {
												ImageView imageview = (ImageView) getView().findViewById(R.id.artist_icon);
												if (imageview != null) imageview.setImageBitmap(myBitmap);
											}
											finally{}
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
			   ArtistHomeFragment.this.AsynkTaskHandler.sendMessage(message);
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
	
	Handler AsynkTaskHandler = new Handler() {

	public void handleMessage(Message msg) {
     	infoLoaded++;
     	
     	if (infoLoaded==2) {
     		
     		//Log.w("hello", artist.getName());
     		
			 getActivity().runOnUiThread(new Runnable() {
			 
				 @Override
				 public void run() {
					 

					 	SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  

					 	SharedPreferences.Editor editor = prefs.edit();  
					 	try {
							editor.putString(artistQuery, toString2(artist));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						editor.commit(); 
					
						
						ProgressBar pg = (ProgressBar) getView().findViewById(R.id.progressBar1);
						pg.setVisibility(View.GONE);
						
						RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.homeArtistLayout);
						rl.setVisibility(View.VISIBLE);
						
						TextView tv = (TextView) getView().findViewById(R.id.artist_name);
						
						tv.setText(artist.getName());
						
						//new DownloadImageTask((ImageView) getView().findViewById(R.id.artist_icon))
				        //  .execute(artist.getSpotifyImage());
						
						(new LoadSongs()).execute(artist.getName());
					    
				 }
			});
     	}
     }
 };
 
 	/** Read the object from Base64 string. */
 	private static Object fromString2( String s ) throws IOException ,
                                                     ClassNotFoundException {
 		byte [] data = Base64.decode( s, Base64.DEFAULT );
 		ObjectInputStream ois = new ObjectInputStream( 
                                      new ByteArrayInputStream(  data ) );
 		Object o  = ois.readObject();
 		ois.close();
 		return o;
 	}

  	/** Write the object to a Base64 string. */
 	private static String toString2( Serializable o ) throws IOException {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		ObjectOutputStream oos = new ObjectOutputStream( baos );
 		oos.writeObject( o );
 		oos.close();
 		return new String( Base64.encode( baos.toByteArray(), Base64.DEFAULT ) );
  	}

 private class LoadSongs extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		
		String artistName = params[0];
		
		Utilities utils = new Utilities();
		
		artistName = utils.ParseName(artistName);
		
		while (!MainActivity.finishedLoadingSongs) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		if (MainActivity.Hash.containsKey(artistName)) {
			Integer ArtistHash = MainActivity.Hash.get(artistName);
			if (MainActivity.Next.get(ArtistHash) != null) {
				for(Integer Album: MainActivity.Next.get(ArtistHash)) {
					String album = MainActivity.Name.get(Album);
					albums.put(album, new ArrayList<Integer>());
					if (MainActivity.Next.get(Album) != null) {
						for(Integer Song: MainActivity.Next.get(Album)) {
							albums.get(album).add(Song);
						}
					}
				}
			}
		}
		
   	 	getActivity().runOnUiThread(new Runnable() {
		 
			 @Override
			 public void run() {
				 
   	 
	        	 Iterator it = albums.entrySet().iterator();
	        	 while (it.hasNext()) {
	        		 Map.Entry pairs = (Map.Entry)it.next();
	        		 
	        		 String album = (String) pairs.getKey();
	        		 
	        		 try{
	        		 
		        		 LinearLayout lv = (LinearLayout) getView().findViewById(R.id.artist_songs);
		        		 TextView tv = new TextView(mContext);
		        		 tv.setId(50); 
		        		 tv.setText(album);
		        		 tv.setTextSize(20);
		        		 LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		        		 llp.setMargins(40, 20, 0, 10); // llp.setMargins(left, top, right, bottom);
		        		 tv.setLayoutParams(llp);
		        		 lv.addView(tv);
	        		 
	        		 } finally {}
	        		 
	        		 final ArrayList<Integer> songs = (ArrayList<Integer>) pairs.getValue();
	        		 
	        		 for (int i=0; i<songs.size(); i++) {
	        			 try {
	        				 String title = MainActivity.Name.get(songs.get(i));
	        				 final String path = MainActivity.SongPath.get(songs.get(i));
	        				 final Integer hash = songs.get(i);
		        			 LinearLayout lv2 = (LinearLayout) getView().findViewById(R.id.artist_songs);
			        		 TextView tv2 = new TextView(mContext);
			        		 tv2.setId(50); 
			        		 tv2.setText(title);
			        		 tv2.setTextSize(16);
			        		 LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			        		 llp2.setMargins(60, 20, 0, 10); // llp.setMargins(left, top, right, bottom);
			        		 tv2.setLayoutParams(llp2);
			        		 lv2.addView(tv2);
			        		 tv2.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									MainActivity.playSong(hash, path);
									
								}
							});			        		 
	        			 } finally{}
	        		 }
	        	 }
			 }
		});
		
		return null;
	}
	 
 }


	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;
		
		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }
		
		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Log.w("hello", urldisplay);
		      Bitmap mIcon11 = null;
		      try {
		          InputStream in = new java.net.URL(urldisplay).openStream();
		          mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }
		
		  protected void onPostExecute(Bitmap result) {
		      bmImage.setImageBitmap(result);
		  }
	}
	
	// usually, subclasses of AsyncTask are declared inside the activity class.
	// that way, you can easily modify the UI thread from here
	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private String path;

	    @Override
	    protected String doInBackground(String... args) {
	        InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        try {
	            URL url = new URL(args[0]);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            path = Environment.getExternalStorageDirectory() + "/" + args[1] + ".jpg";
	            output = new FileOutputStream(path);

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }

	            if (connection != null)
	                connection.disconnect();
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	Log.w("Image downloaded", path);
	    	
	    	Bitmap myBitmap = BitmapFactory.decodeFile(path);

	    	ImageView imageview = (ImageView) getView().findViewById(R.id.artist_icon);
	    	imageview.setImageBitmap(myBitmap);
	    }
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());	
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
}