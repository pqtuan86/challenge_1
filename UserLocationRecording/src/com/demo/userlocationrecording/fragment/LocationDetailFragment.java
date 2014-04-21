package com.demo.userlocationrecording.fragment;

import java.net.URI;

import com.demo.userlocationrecording.R;
import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LocationDetailFragment extends Fragment implements LoaderCallbacks<Cursor>{

	ListView lstDetail;
	public LocationDetailFragment(){
		
	}
	
	public static LocationDetailFragment newInstance(String date){
		LocationDetailFragment instance = new LocationDetailFragment();
		
		Bundle args = new Bundle();
		args.putString("CHOSEN_DATE", date);
		instance.setArguments(args);
		
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_list_of_locations, null, false);
		lstDetail = (ListView) view.findViewById(R.id.id_lst_location);
		
		lstDetail.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.location_lst_item, null, 
				new String[]{UserLocation.COL_NAME, UserLocation.COL_TIME}, new int[] {R.id.id_tv_item_name, R.id.id_tv_item_time}, 0));
		return view;
	}

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		getLoaderManager().initLoader(0, null, this);
		super.onAttach(activity);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		String chosen_date = getArguments().getString("CHOSEN_DATE");
		Log.i("chosen date in loader ======", chosen_date);
		String urlLocationDetail = UserLocationProvider.USER_LOCATION_BASE + chosen_date;
		return new CursorLoader(getActivity(), Uri.parse(urlLocationDetail), new String[]{UserLocation.COL_NAME,  UserLocation.COL_TIME}, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		((SimpleCursorAdapter)lstDetail.getAdapter()).swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		((SimpleCursorAdapter)lstDetail.getAdapter()).swapCursor(null);
	}
	
}
