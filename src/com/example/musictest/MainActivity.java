package com.example.musictest;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.radixtree.RadixTree;
import com.example.slidingpanel.SlidingUpPanelLayout;
import com.example.slidingpanel.SlidingUpPanelLayout.PanelSlideListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.animation.AnimatorProxy;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * 
 * TODO
 *  - Check internet connection.
 *  - Serialization of radix tree.
 * 	
 */
public class MainActivity extends ActionBarActivity implements
				SearchSuggestionsListFragment.OnHeadlineSelectedListener,
				ArtistSearchFragment.OnHeadlineSelectedListener,
				ActionBar.TabListener{
	
	private static Context mContext;
	private RadixTree searchTree = new RadixTree();;
	public static final String PREFS_NAME = "PrefsFile001";
    private boolean finishedLoadingArtists = false;
    private Handler mHandler = new Handler();
    
    private Utilities utils;
	public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	public static ArrayList<ArrayList<Integer>> Next = new ArrayList<ArrayList<Integer>>();
	public static HashMap<String,Integer> Hash = new HashMap<String,Integer>();
	public static ArrayList<Integer> AllArtists = new ArrayList<Integer>();
	public static ArrayList<Integer> AllAlbums = new ArrayList<Integer>();
	public static ArrayList<Integer> AllSongs = new ArrayList<Integer>();
	public static ArrayList<Integer> Parent = new ArrayList<Integer>();
	public static ArrayList<String> Name = new ArrayList<String>();
	public static SparseArray<String> SongPath = new SparseArray<String>();
	public static SparseIntArray Track = new SparseIntArray();
	public static boolean isInAllSongs = false;
	public static ArrayList<HashMap<String,String>> SongListArray;
	public static ArrayList<String> SongListArraySorted;
	public static HashMap<String,Integer> SongListMap = new HashMap<String,Integer>();
	public static ArrayList<Integer> SongHashListArray = new ArrayList<Integer>();
	public static int allSongsIndex;
	public static LinkedList<String> stack = new LinkedList<String>();
	
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    public static ActionBar actionBar;
    private SearchView searchView;
    // Tab titles
    private String[] tabs = { "Home", "News", "Twitter" };
    public static View mView;
    
	public static boolean finishedLoadingSongs = false;
	
	public static final String TAG = "WTF";
 
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
 
    private static MainActivity mInstance;
    private static Boolean isVideoPlaying = false;
	private static boolean isPlayingVideo = false;
	private static android.view.ViewGroup.LayoutParams originalParamsPlayer;
	private static android.view.ViewGroup.LayoutParams originalParamsVideo;
	
	private boolean isVideo = false;
	private boolean isMusic = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY); 
		setContentView(R.layout.activity_main);
		
		mContext = this;
		mInstance = this;
		mView = findViewById(android.R.id.content);
		actionBar = getSupportActionBar();
		
	    final ImageView playBtn = (ImageView) findViewById(R.id.barPlayButton);
		if (playBtn != null) {
			//ViewHelper.setTranslationX(playBtn, getResources().getDisplayMetrics().widthPixels - 68);
		}
    	final TextView mTextView = (TextView) MainActivity.mView.findViewById(R.id.textView2);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels-2*68, 68); 
    	mTextView.setLayoutParams(params);
		final CustomImageView mImageView = (CustomImageView) MainActivity.mView.findViewById(R.id.imageView2);
		final VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
    	
    	if (isVideo) {
    		playBtn.setVisibility(View.GONE);
    		mTextView.setVisibility(View.GONE);
    		mImageView.setVisibility(View.GONE);
    	}
    	if (isMusic) {
    		mVideoView.setVisibility(View.GONE);
    	}
		
		//SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
	    //int size = prefs.getInt("array" + "_size", 0);
		
        //SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		//Boolean val = settings.getBoolean("fileDownloaded", false);
		
		//if (!val) {
		//	(new DownloadArtistsFile()).execute();
		//}
			
		final SlidingUpPanelLayout mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
	        mLayout.setPanelSlideListener(new PanelSlideListener() {
	            @Override
	            public void onPanelSlide(View panel, float slideOffset) {
	                Log.w(TAG, "onPanelSlide, offset " + slideOffset);

	                if (isVideo) setVideoViewSize(slideOffset);
	                if (isMusic) setImageViewSize(slideOffset);
	                //setActionBarTranslation(slideOffset);
	                //actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb((int)(255*slideOffset), 0, 0, 0)));

	            }

	            @Override
	            public void onPanelExpanded(View panel) {
	                Log.w(TAG, "onPanelExpanded");
	                
	                if (isVideo) setVideoViewSize((float)1.0);
	                if (isMusic) setImageViewSize((float)1.0);
	            }

	            @Override
	            public void onPanelCollapsed(View panel) {
	                Log.w(TAG, "onPanelCollapsed");
	                
	                if (isVideo) setVideoViewSize((float)0.0);
	                if (isMusic) setImageViewSize((float)0.0);
	                
	                //VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
	                
	                //Log.w("hello", mVideoView.isFullWindow + " "  + mVideoView.isPlaying());
	                
	                //int actionBarHeight = getActionBarHeight();
	                //setActionBarTranslation(actionBarHeight);
	                //if (!actionBar.isShowing()) actionBar.show();

	            }

	            @Override
	            public void onPanelAnchored(View panel) {
	                Log.w(TAG, "onPanelAnchored");
	            }

	            @Override
	            public void onPanelHidden(View panel) {
	                Log.w(TAG, "onPanelHidden");
	            }
	        });
	        
	    	    
	    playBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).isPlaying()) {
					
					playBtn.setImageResource(R.drawable.ic_action_play);
					isVideoPlaying = false;
					VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
					mVideoView.pause();
					
				}
				else {
					
					playBtn.setImageResource(R.drawable.ic_action_pause);
					isVideoPlaying = true;
					VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
					mVideoView.start();
					
				}
				
			}
		});
	    


		(new AsyncMusicLoad()).execute();
		(new AsyncReadArtistsFile()).execute();
				
		Fragment fragment = null;
		fragment = new HomeFragment();

		
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.frame_container, fragment)
		.addToBackStack(null)
		.commit();
		

	}
	
	
	protected void setImageViewSize(float slideOffset) {
		final CustomImageView mImageView = (CustomImageView) MainActivity.mView.findViewById(R.id.imageView2);
		//mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        
        int width = (int) (1.5*68 + (getResources().getDisplayMetrics().widthPixels - 1.5*68)*slideOffset);
        int height = (int)((double)width*9.0/16.0);
        
        int partialWidth = width / 6;
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(4*partialWidth, 4*partialWidth + (int) (2*68*slideOffset)); 
    	//params.setMargins((int) (partialWidth*slideOffset), (int) (68*Math.sqrt(slideOffset)), (int) (partialWidth*slideOffset), 0);
    	//mImageView.setLayoutParams(params);
    	//mImageView.layout((int) (partialWidth*slideOffset), (int) (68*Math.sqrt(slideOffset)), 0, 0);
        
        Log.w("hello", 4*partialWidth + "  " +  partialWidth);
    	
    	ViewHelper.setTranslationX(mImageView, (int) (partialWidth*slideOffset));
    	//ViewHelper.setTranslationY(mImageView, (int) (68*Math.sqrt(slideOffset)));
    	ViewHelper.setTranslationY(mImageView, (int) (68*Math.pow(slideOffset,0.40)-68*slideOffset));
    	mImageView.setLayoutParams(params);
    	
    	final TextView mTextView = (TextView) MainActivity.mView.findViewById(R.id.textView2);

    	mTextView.setVisibility(View.VISIBLE);
    	mTextView.bringToFront();
    	mTextView.forceLayout();
    	params = new LinearLayout.LayoutParams((int) (1.5*getResources().getDisplayMetrics().widthPixels), 68); 
    	mTextView.setLayoutParams(params);
    	
    	
        Log.w("hello", mTextView.getLeft() + " " + mTextView.getRight());
    	ViewHelper.setTranslationX(mTextView, -(int) (mTextView.getLeft() + 68));
    	
    	ImageView playBtn = (ImageView) MainActivity.mView.findViewById(R.id.barPlayButton);
    	ViewHelper.setTranslationX(playBtn, - playBtn.getLeft() + getResources().getDisplayMetrics().widthPixels - 68);
    	ViewHelper.setAlpha(playBtn, 1-slideOffset);
//    	int sdk = android.os.Build.VERSION.SDK_INT;
//    	if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//        	playBtn.setBackgroundDrawable(new ColorDrawable(Color.argb((int)(255*slideOffset), 0, 0, 0)));
//
//    	} else {
//        	playBtn.setBackground(new ColorDrawable(Color.argb((int)(255*slideOffset), 0, 0, 0)));
//    	}
    	
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.mediaController);
    	Log.w("Posicion", rl.getLeft() + " " + rl.getTop());
    	rl.bringToFront();
    	ImageView t = (ImageView) findViewById(R.id.playerPlayButton);
    	Log.w("Posicion", t.getLeft() + " " + t.getTop());
	}


	protected void setVideoViewSize(float slideOffset) {
	

		final VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
		//mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        
        int width = (int) (128 + (getResources().getDisplayMetrics().widthPixels - 128)*slideOffset);
        int height = (int)((double)width*9.0/16.0);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height); 
    	params.setMargins(0, 0, 0, 0);
    	mVideoView.setLayoutParams(params);
		
	}
	
	private void setVideoViewSize(float f, int i) {

		if (i == 1) {
		    
			final VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
			//mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	        
	        int width = (int) (128 + (getResources().getDisplayMetrics().widthPixels - 128)*f);
	        int height = (int)((double)width*9.0/16.0);
	        
	        int leftOverheight = getResources().getDisplayMetrics().heightPixels - height;
	        Log.w("hello", height + " " + getResources().getDisplayMetrics().heightPixels);
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,getResources().getDisplayMetrics().heightPixels); 
	    	//params.setMargins(0, 0, 0, 0);
	    	mVideoView.setLayoutParams(params);
		}
		else setVideoViewSize(f);
		
	}


	private int getActionBarHeight(){
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

	public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = getActionBarHeight();
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup) findViewById(android.R.id.content).getParent());
        int children = content.getChildCount();
        for (int i = 0; i < children; i++) {
            View child = content.getChildAt(i);
            if (child.getId() != android.R.id.content) {
                if (y <= -actionBarHeight) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        child.setTranslationY(y);
                    } else {
                        AnimatorProxy.wrap(child).setTranslationY(y);
                    }
                }
            }
        }
    }
	
	private class AsyncMusicLoad extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			{
				utils = new Utilities();
				
				Cursor mCursor = null;
				
				Integer cnt = 0;
				
				//Log.w("hello", "hola");
				
				try {
			         mCursor = getContentResolver().query(
			             Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "_id");
			         
			         mCursor.moveToFirst();
			         
			         if (mCursor.getCount() != 0) {

			        	 do {
			        		 

			                 //long date = mCursor.getLong(mCursor
			                 //    .getColumnIndexOrThrow(Audio.Media.DATE_ADDED));

			                 //String Duration = mCursor.getString(mCursor
			                 //    .getColumnIndexOrThrow(Audio.Media.DURATION));
			                String DATA = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.DATA));
			                 
			                 //double TIME_STAMP = mCursor.getInt(mCursor
			                 //    .getColumnIndexOrThrow(Audio.Media.DATE_ADDED));
			                if(DATA.endsWith(".mp3") || DATA.endsWith(".MP3")) {
			                	String ALBUM = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.ALBUM));
			                	String ARTIST = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.ARTIST));
			                	//String DURATION = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.DURATION));
				                //String DISPLAY_NAME = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.DISPLAY_NAME));
				                String TRACK = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.TRACK)); 
				                String TITLE = mCursor.getString(mCursor.getColumnIndexOrThrow(Audio.Media.TITLE));				
								
				                ALBUM = utils.ParseName(ALBUM);
				                ARTIST = utils.ParseName(ARTIST);
				                TITLE = utils.ParseTitle(TITLE);
				                
								if (Hash.get(ARTIST)==null) {
									Hash.put(ARTIST, cnt);
									Name.add(ARTIST);
									AllArtists.add(cnt++);
									Parent.add(-1);
									ArrayList<Integer> v = new ArrayList<Integer>();
									Next.add(v);
								}
								
								if (Hash.get(ARTIST+"/"+ALBUM)==null) {
									AllAlbums.add(cnt);
									Hash.put(ARTIST+"/"+ALBUM, cnt++);
									Name.add(ALBUM);
									ArrayList<Integer> v = new ArrayList<Integer>();
									Next.add(v);
									Parent.add(Hash.get(ARTIST));
									Next.get(Hash.get(ARTIST)).add(Hash.get(ARTIST+"/"+ALBUM));
								}
								
								if (Hash.get(ALBUM+"/"+TITLE)==null) {
									AllSongs.add(cnt);
									Hash.put(ALBUM+"/"+TITLE, cnt++);
									Name.add(TITLE);
									ArrayList<Integer> v = new ArrayList<Integer>();
									Next.add(v);
									Parent.add(Hash.get(ARTIST+"/"+ALBUM));
									SongPath.put(cnt-1, DATA);
									Track.put(cnt-1, Integer.parseInt(TRACK));
									Next.get(Hash.get(ARTIST+"/"+ALBUM)).add(Hash.get(ALBUM+"/"+TITLE));
								}
								
			                }
			                 //Log.w("song","Name:" + DATA);
			                 //System.out.println("data " + DATA);
			                 //System.out.println("time " + TIME_STAMP);
			                 //System.out.println("time " + Duration);
			             } while (mCursor.moveToNext());
			        	 
			        	 finishedLoadingSongs = true;
			        	 
//			        	 SongListArray = new ArrayList<HashMap<String,String>>();
//			        	 SongListArraySorted = new ArrayList<String>();
//						
//			        	 for(Integer index : MainActivity.AllSongs) {
//			        		 SongListArraySorted.add(MainActivity.Name.get(index)+"/*/"+MainActivity.Name.get(MainActivity.Parent.get(index)));
//			        	 }
//			        	 Collections.sort(SongListArraySorted);

			         }
			     } catch (Exception e) {
			    	 Log.w("Error", "fallo");
			         e.printStackTrace();
			     } finally {
			         if (mCursor != null) {
			             mCursor.close();
			             mCursor = null;
			         }
			     }
			}
			
			return null;
		}
		
	}
	
	private class AsyncReadArtistsFile extends AsyncTask<String, Void, Void> {

		   //ProgressDialog dialog = new ProgressDialog(mContext);

		   @Override
		    protected void onPreExecute() {
		        //dialog.setMessage("Loading...");
		        //dialog.show();
		        
		        super.onPreExecute();
		    }

		   protected Void doInBackground(String... args) {
			   
			   readArtistsFile();
			   
			   finishedLoadingArtists = true;
			   
			   return null;
		   }

		   protected void onPostExecute(Void result) {
		     // do UI work here
		     //if(dialog != null && dialog.isShowing()){
		     //  dialog.dismiss();
		     //}

		  }
		}


	private void readArtistsFile() {
		
		long startTime = System.nanoTime();    
				
		AssetManager assetManager = getAssets(); 
		BufferedReader br;
		int k = 0;
		
		try {
			InputStream is = assetManager.open("artist_file.txt");
			
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				
				String artist = "";
				Integer last = 0;
				for (int i=0; i<line.length(); i++) {
					if (line.charAt(i)==',') {
						artist = line.substring(last, i);
						searchTree.insertWord(artist);
						last = i+1;
						k++;
					}
				}
				artist = line.substring(last, line.length());
				searchTree.insertWord(artist);
			}
			br.close();
			
		} catch (IOException e1) {
			Log.e("Error", "Could not load '" + e1.getMessage()+ "'!");
			System.out.println("IOException");
		}
		
		long estimatedTime = System.nanoTime() - startTime;
		
		Log.w("Words", "" + k);
		Log.w("Time", "" + estimatedTime/1000000);
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);

	        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	        MenuItem mSearchMenuItem = (MenuItem) menu.findItem(R.id.menu_search);
	        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
	        if (null != searchView )
	        {
	            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	            searchView.setIconifiedByDefault(false);   
	        }
	        else {
	        	Log.w("hello", "bad");
	        }
	        
	        if (mSearchMenuItem == null) {
	        	Log.w("hello", "Menu item also bad");
	        }
	        

	        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()  {

				public boolean onQueryTextChange(String newText) {
					
					actionBar.removeAllTabs();
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	            	
	            	newText = capitalizeString(newText);
	            	
	            	Log.w("hello", newText);
	            	
	            	ArrayList<String> list = new ArrayList<String>();
	            	if (finishedLoadingArtists && newText.length()>0) list = (ArrayList<String>) searchTree.getAllWordsWithPrefix(newText);
	            	
	            	Fragment fragment = new SearchSuggestionsListFragment();
	            	
	            	Bundle bundle = new Bundle();
	            	bundle.putSerializable("suggestions", list);
	            	bundle.putString("query", newText);
	            	fragment.setArguments(bundle);
	            	
	            	FragmentManager fragmentManager = getSupportFragmentManager();
	    			fragmentManager.beginTransaction()
	    			.replace(R.id.frame_container, fragment)
	    			.addToBackStack(null)
	    			.commit();
	            	
	                return true;
	            }

	            public boolean onQueryTextSubmit(String query) 
	            {
	                // this is your adapter that will be filtered
	            	
	            	getWindow().setSoftInputMode(
	            		      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	            	
	            	Fragment fragment = null;
	        		Bundle args = new Bundle();
	        		fragment = new ArtistSearchFragment();
	        		
	        		args.putString("query", query);
	    			fragment.setArguments(args);
	        		
	        		FragmentManager fragmentManager = getSupportFragmentManager();
	    			fragmentManager.beginTransaction()
	    			.replace(R.id.frame_container, fragment)
	    			.addToBackStack(null)
	    			.commit();
	    			
	                return true;
	            }
	        };
	        if (null != searchView) searchView.setOnQueryTextListener(queryTextListener);

	        return super.onCreateOptionsMenu(menu);
	    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	private class DownloadArtistsFile extends AsyncTask<String, Void, Void> {

		   //ProgressDialog dialog = new ProgressDialog(mContext);

		   @Override
		    protected void onPreExecute() {
		        //dialog.setMessage("Loading...");
		        //dialog.show();
		        
		        super.onPreExecute();
		    }

		   protected Void doInBackground(String... args) {
			   
			   	InputStream input = null;
		        OutputStream output = null;
		        HttpURLConnection connection = null;
		        try {
		            URL url = new URL("https://raw.githubusercontent.com/aaliang/ArtistPairs/master/Artist_lists_small.txt");
		            connection = (HttpURLConnection) url.openConnection();
		            connection.connect();

		            // expect HTTP 200 OK, so we don't mistakenly save error report
		            // instead of the file
		            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
		                Log.w("Error", "Server returned HTTP " + connection.getResponseCode()
		                        + " " + connection.getResponseMessage());
		            }

		            // download the file
		            input = connection.getInputStream();
		            output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/artists_file.txt");

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
		                output.write(data, 0, count);
		            }
		        } catch (Exception e) {
		            Log.w("Error", e.toString());
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
		        
		        Log.w("hello", "File downloaded");
		        
		        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
		        SharedPreferences.Editor editor = prefs.edit();  
		        editor.putBoolean("fileDownloaded", true);
		        editor.commit();
		      


			    //runOnUiThread(new Runnable() {
			   //     @Override
			   //     public void run() {
			   //     	if (adapter!=null) adapter.notifyDataSetChanged();
			   //    }
			   //});
			    
			   
		    return null;
		   }

		   protected void onPostExecute(Void result) {
		     // do UI work here
		     //if(dialog != null && dialog.isShowing()){
		     //  dialog.dismiss();
		     //}

		  }
		}
	
	String capitalizeString(String inputVal) {
	    if (inputVal.length() == 0) return "";

	    if (inputVal.length() == 1) return inputVal.toUpperCase();

	    inputVal =  inputVal.substring(0,1).toUpperCase()
	        + inputVal.substring(1);
	    
	    for (int i=1; i<inputVal.length(); i++) {
	    	if (inputVal.charAt(i-1)==' ') {
	    		if (i != inputVal.length()) {
	    			inputVal =  inputVal.substring(0,i) + inputVal.substring(i,i+1).toUpperCase()
	    				+ inputVal.substring(i+1, inputVal.length());
	    		}
	    		else {
	    			inputVal =  inputVal.substring(0,i) + inputVal.substring(i,i+1).toUpperCase();
	    		}
	    	}
	    }
	    
	    return inputVal;
	}

    
    @Override
	public void onSearchItemSelected(int TYPE, String str) {
		// TODO Auto-generated method stub
    	
    	getWindow().setSoftInputMode(
    		      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
    	
    	if (searchView != null) {
    		searchView.setQuery("", false);
    	}
    	
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
            fm.popBackStack();
        }
    	
		Fragment fragment = null;
		Bundle args = new Bundle();
		CharSequence title = "";
		switch (TYPE) {
		case 0:
			//fragment = new ArtistFragment();
			fragment = new ArtistSearchFragment();
			args.putString("query", str);
			fragment.setArguments(args);
			//stack.add(str);
			break;
		case 1:
//			Intent i=new Intent(mContext,ArtistHome.class);
//			i.putExtra("query", str);
//			mContext.startActivity(i);
			
			//fragment = new ArtistFragment();
			fragment = new ArtistHome();
			args.putString("query", str);
			fragment.setArguments(args);
//			//stack.add(str);
			break;

		default:
			break;
		}
		
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment)
			.addToBackStack(null)
			.commit();


		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
    
	@Override
	public void onBackPressed() {

		super.onBackPressed();

		
		FragmentManager fm = this.getSupportFragmentManager();
		fm.popBackStack();
		
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
	
	public static synchronized MainActivity getInstance() {
        return mInstance;
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
 
        return mRequestQueue;
    }
 
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
 
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    
    public Boolean getIsVideoPlaying() {
    	return isVideoPlaying;
    }
    
    public static void setIsVideoPlaying(Boolean flag) {
    	isVideoPlaying = flag;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            
            if (isVideoPlaying) {
            	
            	//Log.w("hello", "Landscape");
            	
    		    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            	
            	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            
            	
        		int width = getResources().getDisplayMetrics().widthPixels;
        		int height = getResources().getDisplayMetrics().heightPixels;

            	
            	//openFullPlayer(1, width, height);
        		
        		if (actionBar.isShowing()) actionBar.hide();
            	
	        	//((SlidingUpPanelLayout) MainActivity.mView.findViewById(R.id.sliding_layout)).expandPanel();
	        	((SlidingUpPanelLayout) MainActivity.mView.findViewById(R.id.sliding_layout)).expandPanel();
	        	((SlidingUpPanelLayout) findViewById(R.id.sliding_layout)).setSlidingEnabled(false);           	
            	setVideoViewSize((float)1.0, 1);
            	
            	((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).setFullWindow(true);
            	((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).setIcon();
            	
            }
            
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        	if (isVideoPlaying) {
        		
        		//Log.w("hello", "Portrait");
        		
        		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
        		
    		    //getWindow().setFlags(WindowManager.LayoutParams.FLAG, 
    		    //		WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            	
        		int width = getResources().getDisplayMetrics().widthPixels;
        		int height = getResources().getDisplayMetrics().heightPixels;

            	
        		//openFullPlayer(1, width, height);
        		
        		if (!actionBar.isShowing()) actionBar.show();
        		
	        	((SlidingUpPanelLayout) MainActivity.mView.findViewById(R.id.sliding_layout)).expandPanel();
	        	((SlidingUpPanelLayout) findViewById(R.id.sliding_layout)).setSlidingEnabled(true);
	        	
	        	setVideoViewSize((float)1.0, 0);
	        	
	        	((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).setFullWindow(false);
	        	
	        	setPlayIcon();
            }
        }
    }


	private void setPlayIcon() {
		
		final ImageView playBtn = (ImageView) findViewById(R.id.playerPlayButton);
		
		if (!((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).isPlaying()) {
			
			playBtn.setImageResource(R.drawable.ic_action_play);
			
		}
		else {
			
			playBtn.setImageResource(R.drawable.ic_action_pause);
			
		}
	}


	public static void closeFullScreenPlayer() {

		MainActivity.mView.findViewById(R.id.player).setLayoutParams(originalParamsPlayer);
		MainActivity.actionBar.show();
		MainActivity.mView.findViewById(R.id.videoView2).setLayoutParams(originalParamsVideo);
		ImageView cancelBtn = (ImageView) mView.findViewById(R.id.closePlayer);
        cancelBtn.setVisibility(View.GONE);
        int width = originalParamsVideo.width;
        int height = originalParamsVideo.height;
        ((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).setDimensions(height, width);
        ((VideoView) MainActivity.mView.findViewById(R.id.videoView2)).getHolder().setFixedSize(height, width);
	}

	public static void openFullPlayer(int type, int width, int height) {
		
		if (type == 1) {
			isVideoPlaying = true;
		}
		
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT); 
//		if (originalParamsPlayer == null) originalParamsPlayer = MainActivity.mView.findViewById(R.id.player).getLayoutParams();
//		MainActivity.mView.findViewById(R.id.player).setLayoutParams(params);
//		MainActivity.actionBar.hide();
		
		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			openFullPortraitPlayer(width, height);
		}
		else {
			openFullLandscapePlayer(width, height);
		}
	}
		
	private static void openFullLandscapePlayer(int width, int height) {
				
		if (isVideoPlaying ) {
						
			//ImageView cancelBtn = (ImageView) mView.findViewById(R.id.closePlayer);
            //cancelBtn.setVisibility(View.GONE);
			        	
        	mView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        	
        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT); 
        	if (originalParamsVideo == null) originalParamsVideo = MainActivity.mView.findViewById(R.id.player).getLayoutParams();
        	params.setMargins(0, 0, 0, 0);
        	//params.alignWithParent = false;
			//MainActivity.mView.findViewById(R.id.player).setLayoutParams(params);
        	
        	//Log.w("hola", height + " " + width);
			
        	LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width,height); 
			llp.setMargins(0, 0, 0, 0);

			//llp.addRule(RelativeLayout.CENTER_IN_PARENT);
			VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
			mVideoView.setLayoutParams(llp);
			
			mVideoView.setDimensions(height, width);
			mVideoView.getHolder().setFixedSize(height, width);
			
		}
		else {
			//TODO
		}
		
	}

	private static void openFullPortraitPlayer(int width, int height) {
		
		Log.w("hello", isPlayingVideo + " " + width + "x" + height);

		if (isVideoPlaying ) {
			
			Log.w("hello", isPlayingVideo + " " + width + "x" + height);
			
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width,width*9/16); 
			llp.setMargins(0, 0, 0, 0);
			//if (originalParamsVideo == null) originalParamsVideo = MainActivity.mView.findViewById(R.id.player).getLayoutParams();
			VideoView mVideoView = (VideoView) MainActivity.mView.findViewById(R.id.videoView2);
			mVideoView.setLayoutParams(llp);
			
			mVideoView.setDimensions(height, width);
			mVideoView.getHolder().setFixedSize(height, width);
			
			//ImageView cancelBtn = (ImageView) MainActivity.mView.findViewById(R.id.closePlayer);
		    //cancelBtn.setVisibility(View.VISIBLE);
		    //cancelBtn.bringToFront();
		    
//		    cancelBtn.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					MainActivity.closeFullScreenPlayer();
//					
//				}
//			});
		}
		else {
			//TODO
		}
		
	}
	
	public static Context getContext() {
		return mContext;
	}


}
