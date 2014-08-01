package com.example.musictest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;

/**
 * Displays a video file.  The VideoView class
 * can load images from various sources (such as resources or content
 * providers), takes care of computing its measurement from the video so that
 * it can be used in any layout manager, and provides various display options
 * such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements MediaPlayerControl {
    // settable by the client
    private Uri         mUri;

    // All the stuff we need for playing and showing a video
    public SurfaceHolder mSurfaceHolder = null;
    public MediaPlayer mMediaPlayer = null;
    public boolean     mIsPrepared;
    public int         mVideoWidth;
    public int         mVideoHeight;
    public int         mSurfaceWidth;
    public int         mSurfaceHeight;
    public MediaController mMediaController;
    public OnCompletionListener mOnCompletionListener;
    public MediaPlayer.OnPreparedListener mOnPreparedListener;
    public int         mCurrentBufferPercentage;
    public OnErrorListener mOnErrorListener;
    public boolean     mStartWhenPrepared;
    public int         mSeekWhenPrepared;
    public Context 	mContext;
    private int mForceHeight = 0;
    private int mForceWidth = 0;
	public boolean isFullWindow;
	private boolean isPaused = false;

	private ImageView mPlayButton;

    public VideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }
    
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }
    
    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }
    
    public void setData(VideoView other) {
        this.mMediaPlayer = other.mMediaPlayer;
        this.mIsPrepared = other.mIsPrepared;
        this.mOnCompletionListener = other.mOnCompletionListener;
        this.mOnPreparedListener = other.mOnPreparedListener;
        this.mCurrentBufferPercentage = other.mCurrentBufferPercentage;
        this.mOnErrorListener = other.mOnErrorListener;
        this.mStartWhenPrepared = false;
        this.mSeekWhenPrepared = other.mSeekWhenPrepared;
    }
    
    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.w("@@@@", "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
//
//        if (mVideoWidth > 0 && mVideoHeight > 0) {
//            if ( mVideoWidth * height  > width * mVideoHeight ) {
//                //Log.i("@@@", "image too tall, correcting");
//                height = width * mVideoHeight / mVideoWidth;
//            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
//                //Log.i("@@@", "image too wide, correcting");
//            	//height = width * mVideoHeight / mVideoWidth;
//                width = height * mVideoWidth / mVideoHeight;
//            } else {
//                //Log.i("@@@", "aspect ratio is correct: " +
//                        //width+"/"+height+"="+
//                        //mVideoWidth+"/"+mVideoHeight);
//            }
//        }
        //Log.w("@@@@@@@@@@", "setting size: " + widthMeasureSpec + 'x' + heightMeasureSpec);
        //Log.w("@@@@@@@@@@", "setting size: " + width + 'x' + height);
        setMeasuredDimension(width, height);
    }
    
    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                 * than max size imposed on ourselves.
                 */
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
                /* Parent says we can be as big as we want, up to specSize. 
                 * Don't be larger than specSize, and don't be larger than 
                 * the max size imposed on ourselves.
                 */
                result = Math.min(desiredSize, specSize);
                break;
                
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
}
    
    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }
    
    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void openVideo() {
    	Log.w("###", "llamada a openVideo()");
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // Tell the music playback service to pause 
        // TODO: these constants need to be published somewhere in the framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);
        
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        try {
        	
        	Log.w("###", "Init player");
        	
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mIsPrepared = false;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            attachMediaController();
        } catch (IOException ex) {
            Log.w("VideoView", "Unable to open content: " + mUri, ex);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w("VideoView", "Unable to open content: " + mUri, ex);
            return;
        }
    }
    
    public void setPlayButton(ImageView playButton) {
    	if (isFullWindow) {
	        if (mPlayButton != null) {
	        	mPlayButton.setVisibility(View.GONE);
	        }
	        mPlayButton = playButton;
    	}
        
    }

    
    public void setMediaController(MediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View)this.getParent() : this;
            //mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(mIsPrepared);
        }
    }
    
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            // briefly show the mediacontroller
        	
        	Log.w("###", "Is prepared" + " " + mStartWhenPrepared);
        	
            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mSeekWhenPrepared != 0) {
                        mMediaPlayer.seekTo(mSeekWhenPrepared);
                    }
                    if (mStartWhenPrepared) {
                        mMediaPlayer.start();
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                   } else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
                       if (mMediaController != null) {
                           mMediaController.show(0);   // show the media controls when we're paused into a video and make 'em stick.
                       }
                   }
                }
            } else {
                Log.d("VideoView", "Couldn't get video size after prepare(): " +
                        mVideoWidth + "/" + mVideoHeight);
                // The file was probably truncated or corrupt. Start anyway, so
                // that we play whatever short snippet is there and then get
                // the "playback completed" event.
                if (mStartWhenPrepared) {
                    mMediaPlayer.start();
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener =
        new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener =
        new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int a, int b) {
            Log.d("VideoView", "Error: " + a + "," + b);
            if (mMediaController != null) {
                mMediaController.hide();
            }

            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, a, b)) {
                    return true;
                }
            }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
            if (getWindowToken() != null) {
//                Resources r = mContext.getResources();
//                new AlertDialog.Builder(mContext)
//                        .setTitle(com.android.internal.R.string.VideoView_error_title)
//                        .setMessage(com.android.internal.R.string.VideoView_error_text_unknown)
//                        .setPositiveButton(com.android.internal.R.string.VideoView_error_button,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        /* If we get here, there is no onError listener, so
//                                         * at least inform them that the video is over.
//                                         */
//                                        if (mOnCompletionListener != null) {
//                                            mOnCompletionListener.onCompletion(mMediaPlayer);
//                                        }
//                                    }
//                                })
//                        .setCancelable(false)
//                        .show();
            }
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
        new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
    {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l)
    {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l)
    {
        mOnErrorListener = l;
    }
    
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            //Log.w("layout",  top + " " + bottom);
        }
    }
    
    @Override
    public void onSizeChanged(int left, int top, int right, int bottom) {

           //Log.w("changed",  top + " " + bottom);

    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
    {
        public void surfaceChanged(SurfaceHolder holder, int format,
                                    int w, int h)
        {
        	//Log.w("###", "Surface onChanged");
        	
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            
            if (mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                }
                if (!isPaused) mMediaPlayer.start();
                if (mMediaController != null && isFullWindow) {
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
        	//Log.w("###", "Surface onCreated  " + holder.toString());
        	
        	if (mSurfaceHolder == null) {
	            mSurfaceHolder = holder;
	            openVideo();
        	}
        	
        	isPaused = false;
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // after we return from this we can't use the surface any more
        	Log.w("Destroyed", "It has been destroyed");
//            mSurfaceHolder = null;
//            if (mMediaController != null) mMediaController.hide();
//            if (mMediaPlayer != null) {
//                mMediaPlayer.reset();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//            }
        	
        	isPaused = false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	if (isFullWindow) {
	    	if (mIsPrepared && mMediaPlayer != null && mPlayButton != null) {
	    		togglePlayButtonVisiblity();
	        }
	        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
	            toggleMediaControlsVisiblity();
	        }
    	}
        return false;
    }
    
    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
    	if (isFullWindow) {
	    	if (mIsPrepared && mMediaPlayer != null && mPlayButton != null) {
	    		togglePlayButtonVisiblity();
	        }
	        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
	            toggleMediaControlsVisiblity();
	        }
    	}
        return false;
    }
    
    private void togglePlayButtonVisiblity() {
    	if (isFullWindow) {
	    	if (mPlayButton.getVisibility() == View.VISIBLE) { 
	    		//mPlayButton.setVisibility(View.GONE);
	        } else {
	        	mPlayButton.setVisibility(View.VISIBLE);
	        	timerDelayRemoveView(5000, mPlayButton);
	        }
    	}
    	return;
	}
    
    public void timerDelayRemoveView(float time, final ImageView v) {
        Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() {           
            public void run() {                
                v.setVisibility(View.GONE);      
            }
        }, (long) time); 
    }

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (mIsPrepared &&
                keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL &&
                mMediaPlayer != null &&
                mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
    	if (isFullWindow) {
	        if (mMediaController.isShowing()) { 
	            mMediaController.hide();
	        } else {
	            mMediaController.show();
	        }
    	}
    }
    
    public void start() {
    	
        if (mMediaPlayer != null && mIsPrepared) {
                mMediaPlayer.start();
                mStartWhenPrepared = false;
        } else {
            mStartWhenPrepared = true;
        }
        if (mMediaController != null && isFullWindow) {
        	mMediaController.setPause();
        }
    }
    
    public void pause() {
    	isPaused = true;
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        mStartWhenPrepared = false;
        if (mMediaController != null && isFullWindow) {
        	mMediaController.setPlay();
        }
    }
    
    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }
    
    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
        }
    }    
            
    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
    
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFullWindow(boolean b) {
		isFullWindow = b;
		if (mMediaController != null) {
			if (b) mMediaController.putControllersFront();
			else  mMediaController.putControllersBack();
		}
	}

	public void setIcon() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mMediaController != null) {
			mMediaController.setPause();
		}
		else {
			mMediaController.setPlay();
		}
	}
}