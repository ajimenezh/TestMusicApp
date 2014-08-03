package com.example.musictest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.slidingpanel.SlidingUpPanelLayout;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class YoutubeVideosFragment extends Fragment{
	
	private String artistQuery;
	protected int infoLoaded;
	
	private static YouTube youtube;
	
	private GoogleAccountCredential credential;
	
	private static Context mContext;
	
	private VideoView videoView;
	
	private Boolean isPlaying = false;
	private String url;
	
	private ArrayList<Video> artistVideos = new ArrayList<Video>();

	public YoutubeVideosFragment(){
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.youtube_fragment, container, false);
		
		artistQuery = getArguments().getString("query");
		
		mContext = getActivity();
				
		(new getYoutubeVideos()).execute(artistQuery);

        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
//		VideoView videoView = (VideoView) getView().findViewById(R.id.videoView);
//        MediaController mediaController = new MediaController(getActivity());
//        mediaController.setAnchorView(videoView);
//        // Set video link (mp4 format )
//        Uri video = Uri.parse("rtsp://v8.cache1.c.youtube.com/CiILENy73wIaGQnxa4t5p6BVTxMYESARFEgGUgZ2aWRlb3MM/0/0/0/video.3gp");
//        //videoView.setMediaController(mediaController);
//        videoView.setVideoURI(video);
//        videoView.start();
//        videoView.setVisibility(View.INVISIBLE);
        
		//WebView myWebView = (WebView) getView().findViewById(R.id.webView);
        //myWebView.loadData("<iframe width=\"420\" height=\"315\" src=\"http://www.youtube.com/embed/4nckjoBALgM?autoplay=1\" frameborder=\"0\" allowfullscreen></iframe>", "text/html; charset=UTF-8", null);
		
//        try{
//        	ArrayList<String> result = new ArrayList<String>();
//        	String url = "https://www.youtube.com/watch?v=iuLqQv5nJ9Y";
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            Document doc = db.parse((new URL(url)).openStream());
//            doc.getDocumentElement ().normalize ();
//            NodeList content = doc.getElementsByTagName("media:content");
//            for(int i=0; i<content.getLength(); i++){
//                Element rsp = (Element)content.item(i);
//                result.add(rsp.getAttribute("url"));
//            }
//
//        }catch(Exception e){
//            Log.e("log_tag", "Error in http connection "+e.toString());
//        }
        
//		String lYouTubeFmtQuality = "17";
//        try {
//			String lUriStr = calculateYouTubeUrl(lYouTubeFmtQuality, true, "iuLqQv5nJ9Y");
//			Log.w("Youtube URI", lUriStr);
			
//			SurfaceView mPreview = (SurfaceView) getView().findViewById(R.id.surface);
//			SurfaceHolder mSurfaceHolder = mPreview.getHolder();
//			
//			MediaPlayer mediaPlayer = new MediaPlayer();
//			
//			mediaPlayer.setDataSource(lUriStr);
//			mediaPlayer.setDisplay(mSurfaceHolder);
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mediaPlayer.setScreenOnWhilePlaying(true);
//	        mediaPlayer.prepare();
//	        mediaPlayer.start();
			
//			Log.w("###", "Init view");
//			videoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
//
//			MediaController mediaController = new MediaController(getActivity());
//	        mediaController.setAnchorView(videoView);
//	        // Set video link (mp4 format )
//	        url = lUriStr;
//	        Uri video = Uri.parse(lUriStr);
//	        
//	        Log.w("###", "Set controller");
//	        videoView.setMediaController(mediaController);
//	        Log.w("###", "Videourl");
//	        videoView.setVideoURI(video);
//	        Log.w("###", "Start");
//	        //videoView.start();
//	        isPlaying = true;
	        
	        
//	        Button btn = (Button) getView().findViewById(R.id.button1);
//	        
//	        btn.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {

//					if (isPlaying) {
//						
//						Log.w("###", "Button clicked");
//						
//						videoView.pause();
//						videoView.mStartWhenPrepared = false; 
//						isPlaying = false;
//						
//						VideoView videoView2 = (VideoView) getView().findViewById(R.id.videoView2);
//
//						MediaController mediaController = new MediaController(getActivity());
//				        mediaController.setAnchorView(videoView2);
//				        // Set video link (mp4 format )
//				        Uri video = Uri.parse(url);
//				        
//				        videoView2.setMediaController(mediaController);
//
//				        videoView2.setVideoURI(video);
//				        
//				        videoView2.setData(videoView);
//				        
//				        //videoView2.start();
//
//				        //videoView.start();
//				        isPlaying = true;
//					}
//					else {
//						videoView.start();
//						isPlaying = true;
//					}
					//videoView = (VideoView) getView().findViewById(R.id.videoView2);;
					//videoView.start();
					
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //The WRAP_CONTENT parameters can be replaced by an absolute width and height or the FILL_PARENT option)
//					params.leftMargin = 50; //Your X coordinate
//					params.topMargin = 60; //Your Y coordinate
//					videoView.setLayoutParams(params);
//					
//				}
//			});
//	        
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
	}
	
	private class getYoutubeVideos extends AsyncTask<String, Void, Void> {


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
						
						String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=relevance&q=" + query + "+Concert&type=video&videoEmbeddable=true&videoSyndicated=true&key=AIzaSyBk-WBRYclW0x1dClhs5l1igpfGAbFCjOc";
						
						JSONObject response = getJSONfromURL(url);
						
						if (response != null) {
			
							if (response.has("items") ) {
								
								try {
									JSONArray array = (JSONArray) response.get("items");
									
									for (int i=0; i<array.length(); i++) {
										JSONObject obj = array.getJSONObject(i);
										String videoId = ((JSONObject) obj.get("id")).getString("videoId");
										String title = ((JSONObject) obj.get("snippet")).getString("title");
										
										JSONObject thumbnailObj = (JSONObject) ((JSONObject) obj.get("snippet")).get("thumbnails");
										
										String thumbnail = ((JSONObject) thumbnailObj.get("default")).getString("url");
										
										Video vi = new Video();
										vi.setTitle(title);
										vi.setId(videoId);
										vi.setImage(thumbnail);
										
										artistVideos.add(vi);
										
									}
									
									
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								
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
			   YoutubeVideosFragment.this.AsynkTaskHandler.sendMessage(message);
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
     	
     	drawVideos();
     	
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
 
	private class findVideoUrl extends AsyncTask<String, Void, Void> {

		   ProgressDialog dialog = new ProgressDialog(MainActivity.getContext());
		   private String lUriStr = "";
		   private String videoId;

		   @Override
		    protected void onPreExecute() {
		        dialog.setMessage("Loading...");
		        dialog.show();
		        
		        super.onPreExecute();
		    }

		   protected Void doInBackground(String... args) {

		        
			   	String lYouTubeFmtQuality = "17";
			   	videoId = args[0];
				try {
					lUriStr = calculateYouTubeUrl(lYouTubeFmtQuality, true, args[0]);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			    
			   
		    return null;
		   }

		   protected void onPostExecute(Void result) {
			   
			   MainActivity.playVideo();
			   
		     // do UI work here
		     if(dialog != null && dialog.isShowing()){
		       dialog.dismiss();
		     }
		     
		        if (lUriStr == null || lUriStr.length()==0) {
		        	Toast toast1 =
		                    Toast.makeText(MainActivity.getContext(),
		                            "This video cannot be viewed outside of youtube.com", Toast.LENGTH_LONG);
		         
		                toast1.show();
		                
		                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId)));
		        }
		        else {
		        	
		        	//int width = getResources().getDisplayMetrics().widthPixels;
	        		//int height = getResources().getDisplayMetrics().heightPixels;

	        		//Log.w("dimensions", width + "  " + height);
	            	
	        		//MainActivity.openFullPlayer(1, width, height);
		        	
		        	((SlidingUpPanelLayout) MainActivity.mView.findViewById(R.id.sliding_layout)).expandPanel();

		        	ImageView playBtn2 = (ImageView) MainActivity.mView.findViewById(R.id.playerPlayButton);
		        	playBtn2.setImageResource(R.drawable.ic_action_play);
		        	
					
					VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);

					ImageView playBtn = (ImageView) MainActivity.mView.findViewById(R.id.videoPlayButton);
					//playBtn.bringToFront();
					playBtn.setVisibility(View.GONE);
					MediaController mediaController = new MediaController(playBtn, R.drawable.ic_action_play_over_video, R.drawable.ic_action_pause_over_video);
				    //mediaController.setAnchorView(mVideoView);
				    // Set video link (mp4 format )
				    url = lUriStr;
				    Uri video = Uri.parse(lUriStr);
				    
				    mVideoView.setMediaController(mediaController);
				    mVideoView.setVideoURI(video);
				    mVideoView.start();
				    mVideoView.setFullWindow(false);
				    
				    MainActivity.setIsVideoPlaying(true);
				    
		        	playBtn2.setImageResource(R.drawable.ic_action_pause);
		        	((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).setFullWindow(false);
					
		        }

		  }
		}

 	private void drawVideos() {
 		getActivity().runOnUiThread(new Runnable() {
		 
 			@Override
 			public void run() {
 				
 				for (int i=0; i<artistVideos.size(); i++) {
 					
 					final Video vi = artistVideos.get(i);
					
					LinearLayout lv = (LinearLayout) getView().findViewById(R.id.artistVideos);
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
					
					View view = inflater.inflate( R.layout.youtube_item, null );
					
					TextView tv = (TextView) view.findViewById(R.id.title);
					tv.setText(vi.getTitle());
					
					ImageLoader imageLoader = MainActivity.getInstance().getImageLoader();
					 
					// If you are using NetworkImageView
					NetworkImageView imgNetWorkView = (NetworkImageView) view.findViewById(R.id.thumbnail);
					imgNetWorkView.setImageUrl(vi.getImage(), imageLoader);
					
					view.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							(new findVideoUrl()).execute(vi.getId());
							

						}
					});
					
					lv.addView(view);
 				}
 				
 			}
		});
 		
 		
 	}
 	
 	/**
     * Calculate the YouTube URL to load the video.  Includes retrieving a token that YouTube
     * requires to play the video.
     * 
     * @param pYouTubeFmtQuality quality of the video.  17=low, 18=high
     * @param bFallback whether to fallback to lower quality in case the supplied quality is not available
     * @param pYouTubeVideoId the id of the video
     * @return the url string that will retrieve the video
     * @throws IOException
     * @throws ClientProtocolException
     * @throws UnsupportedEncodingException
     */
    public static String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
                    String pYouTubeVideoId) throws IOException,
                    ClientProtocolException, UnsupportedEncodingException {

    		String lUriStr = null;
    		try{
	            HttpClient lClient = new DefaultHttpClient();
	            
	            HttpGet lGetMethod = new HttpGet("http://www.youtube.com/get_video_info?&video_id=" + 
	                                                                             pYouTubeVideoId);
	            
	            HttpResponse lResp = null;
	
	            lResp = lClient.execute(lGetMethod);
	                    
	            ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
	            String lInfoStr = null;
	                    
	            lResp.getEntity().writeTo(lBOS);
	            lInfoStr = new String(lBOS.toString("UTF-8"));
	            
	            String[] lArgs=lInfoStr.split("&");
	            Map<String,String> lArgMap = new HashMap<String, String>();
	            for(int i=0; i<lArgs.length; i++){
	                    String[] lArgValStrArr = lArgs[i].split("=");
	                    if(lArgValStrArr != null){
	                            if(lArgValStrArr.length >= 2){
	                                    lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
	                            }
	                    }
	            }
	            
	            //Find out the URI string from the parameters
	            //Populate the list of formats for the video
	            String lFmtList = null;
	            ArrayList<Format> lFormats = new ArrayList<Format>();
	            if (lArgMap == null || lArgMap.get("fmt_list") == null) {
	            	Log.w("hello", pYouTubeVideoId);
	            	return null;
	            }
	            else {
	            	lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
		            if(null != lFmtList){
		                    String lFormatStrs[] = lFmtList.split(",");
		                    
		                    for(String lFormatStr : lFormatStrs){
		                         Format lFormat = new Format(lFormatStr);
		                          lFormats.add(lFormat);
		                    }
		            }
	            }
	            
	            //Populate the list of streams for the video
	            String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
	            if(null != lStreamList){
	                    String lStreamStrs[] = lStreamList.split(",");
	                    ArrayList<VideoStream> lStreams = new ArrayList<VideoStream>();
	                    for(String lStreamStr : lStreamStrs){
	                            VideoStream lStream = new VideoStream(lStreamStr);
	                            lStreams.add(lStream);
	                    }       
	                    
	                    //Search for the given format in the list of video formats
	                    // if it is there, select the corresponding stream
	                    // otherwise if fallback is requested, check for next lower format
	                    int lFormatId = Integer.parseInt(pYouTubeFmtQuality);
	                    
	                    Format lSearchFormat = new Format(lFormatId);
	                    while(!lFormats.contains(lSearchFormat) && pFallback ){
	                            int lOldId = lSearchFormat.getId();
	                            int lNewId = getSupportedFallbackId(lOldId);
	                            
	                            if(lOldId == lNewId){
	                                    break;
	                            }
	                            lSearchFormat = new Format(lNewId);
	                    }
	                    
	                    int lIndex = lFormats.indexOf(lSearchFormat);
	                    if(lIndex >= 0){
	                            VideoStream lSearchStream = lStreams.get(lIndex);
	                            lUriStr = lSearchStream.getUrl();
	                    }
	                    
	            }               
	            //Return the URI string. It may be null if the format (or a fallback format if enabled)
	            // is not found in the list of formats for the video
    		} catch (NullPointerException e) {
                Log.w("Error", "Caught the NullPointerException");
            }finally {
    		}
    		
            return lUriStr;
    }
    
    public static int getSupportedFallbackId(int pOldId){
        final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality 
                                                                          17,  //3GPP (MPEG-4 encoded) Medium quality 
                                                                          18,  //MP4  (H.264 encoded) Normal quality
                                                                          22,  //MP4  (H.264 encoded) High quality
                                                                          37   //MP4  (H.264 encoded) High quality
                                                                          };
        int lFallbackId = pOldId;
        for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
                if(pOldId == lSupportedFormatIds[i] && i > 0){
                        lFallbackId = lSupportedFormatIds[i-1];
                }                       
        }
        return lFallbackId;
}
    
}

