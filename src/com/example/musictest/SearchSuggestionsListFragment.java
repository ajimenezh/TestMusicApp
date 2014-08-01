package com.example.musictest;


import java.util.ArrayList;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class SearchSuggestionsListFragment extends ListFragment {
	
	private Context mContext;
	private String artistQuery;
	
	private ArrayList<String> artistSuggestions = new ArrayList<String>();
	
	private OnHeadlineSelectedListener mCallback;
	
	private String query;
	
	public SearchSuggestionsListFragment(){
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View rootView;

		rootView = inflater.inflate(R.layout.search_suggestions_fragment, container, false);
		
		artistSuggestions = (ArrayList<String>) getArguments().get("suggestions");
		query = getArguments().getString("query");

		artistSuggestions.add(0, "Search " + query + "...");
		
        // Establecemos el Adapter a la Lista del Fragment
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, artistSuggestions));
         
        return rootView;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
 
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
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		
		getActivity().getWindow().setSoftInputMode(
  		      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
		if (position == 0) {
			mCallback.onSearchItemSelected(0, query);
		}
		else {
			mCallback.onSearchItemSelected(1, artistSuggestions.get(position));
		}
		
    }
	
}