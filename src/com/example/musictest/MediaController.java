package com.example.musictest;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class MediaController{
	
	private	Integer		PLAY_VIEW_ID;
	private	Integer		PAUSE_VIEW_ID;
	private ImageView 	mPlayButton;
	private boolean 	isEnabled = false;
	private boolean 	isPlaying = false;
	private VideoView 	mView;
	
	
	public MediaController(){
		
	}
	
	public MediaController(ImageView btn) {
		mPlayButton = btn;
		mPlayButton.setOnClickListener(mOnClickListener);
	}
	
	public MediaController(ImageView btn, Integer play) {
		mPlayButton = btn;
		mPlayButton.setOnClickListener(mOnClickListener);
		PLAY_VIEW_ID = play;
		PAUSE_VIEW_ID = play;
	}
	
	public MediaController(ImageView btn, Integer play, Integer pause) {
		mPlayButton = btn;
		mPlayButton.setOnClickListener(mOnClickListener);
		PLAY_VIEW_ID = play;
		PAUSE_VIEW_ID = pause;
	}
	
	public void hide() {
		if (mPlayButton != null && mPlayButton.getVisibility()==View.VISIBLE) {
			mPlayButton.setVisibility(View.GONE);
		}
	}


	public void setEnabled(boolean b) {
		isEnabled = b;
		return;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}


	public void show(int vanishing) {
		if (vanishing == 0) {
			if (mPlayButton != null && mPlayButton.getVisibility()==View.GONE) {
				setCorrectIcon();
				mPlayButton.setVisibility(View.VISIBLE);
			}
		}
		else {
			show();
		}
	}
	
	public void show() {	
		if (mPlayButton != null && mPlayButton.getVisibility()==View.GONE) {
			setCorrectIcon();
			mPlayButton.setVisibility(View.VISIBLE);
		}
		if (isPlaying) timerDelayRemoveView(2000, mPlayButton);
	}
	
	public void setCorrectIcon() {
		if (!isPlaying) {
			setPlay();
		}
		else {
			setPause();
		}
	}


	public void timerDelayRemoveView(float time, final ImageView v) {
        Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() {           
            public void run() {                
                v.setVisibility(View.GONE);      
            }
        }, (long) time); 
    }


	public boolean isShowing() {
		if (mPlayButton != null) {
			return mPlayButton.getVisibility()==View.VISIBLE;
		}
		else {
			return false;
		}
	}


	public void setPause() {
		if (mPlayButton != null && PAUSE_VIEW_ID != null) {
			if (!isPlaying) {
				isPlaying = true;
				mPlayButton.setImageResource(PAUSE_VIEW_ID);
			}
		}
		
	}


	public void setPlay() {
		if (isPlaying && mPlayButton != null && PLAY_VIEW_ID != null) {
			isPlaying = false;
			mPlayButton.setImageResource(PLAY_VIEW_ID);
		}
	}
	

    private View.OnClickListener mOnClickListener =
            new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Log.w("Click", "hola");
					
					if (mView != null) {
						
						Log.w("mView", "No es nulo");
						
						if (isPlaying) {
							
							mView.pause();
							
							setPlay();
							
						}
						else {
							
							mView.start();
							
							setPause();
						}
						
					}
					
				}
    };


	public void setMediaPlayer(VideoView videoView) {
		mView = videoView;
	}

	public void putControllersFront() {
		if (mPlayButton != null) {
			Log.w("hello", "What happens?");
			mPlayButton.setVisibility(View.VISIBLE);
			mPlayButton.bringToFront();
		}
	}

	public void putControllersBack() {
		if (mPlayButton != null) {
			mPlayButton.setVisibility(View.GONE);
		}
	}
	
}