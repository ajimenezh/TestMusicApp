package com.example.musictest;
 
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
 
public class TabsPagerAdapter extends FragmentStatePagerAdapter {
 
    private String artistQuery;

	public TabsPagerAdapter(FragmentManager fm, String artistQuery) {
        super(fm);
        this.artistQuery = artistQuery;
    }
 
    @Override
    public Fragment getItem(int index) {
    	
    	Fragment fragment = null;
		Bundle args = new Bundle();
 
        switch (index) {
        case 0:

    		fragment = new ArtistHomeFragment();
    		//args.putParcelable("query", ArtistHome.getArtist());
    		args.putString("query", artistQuery);
    		fragment.setArguments(args);
            return fragment;
        case 1:

    		fragment = new ArtistNewsFragment();
    		//args.putParcelable("query", ArtistHome.getArtist());
    		args.putString("query", artistQuery);
    		fragment.setArguments(args);
            return fragment;
        case 2:

        	fragment = new YoutubeVideosFragment();
    		//args.putParcelable("query", ArtistHome.getArtist());
    		args.putString("query", artistQuery);
    		fragment.setArguments(args);
            return fragment;
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}