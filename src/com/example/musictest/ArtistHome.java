package com.example.musictest;


import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;



public class ArtistHome extends Fragment implements ActionBar.TabListener{
	
	private Context mContext;
	public static String artistQuery;
	private Artist artist = new Artist();
	private HashMap<String, ArrayList<String>> albums = new HashMap<String, ArrayList<String>>();
	
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Home", "News", "Movies" };
	
	public ArtistHome(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.artist_home, container, false);
		
		mContext = getActivity();
         
		artistQuery = getArguments().getString("query");
		
		Log.w("entra en el Fragment", artistQuery);

		
        return rootView;
    }
	
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 
		
		Log.w("entra en el Fragment Created", artistQuery);
		
		 // Initilization
        viewPager = (ViewPager) getView().findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        actionBar = MainActivity.actionBar;

        mAdapter = new TabsPagerAdapter(getActivity().getSupportFragmentManager(), artistQuery);
 
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        actionBar.removeAllTabs();
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
		
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