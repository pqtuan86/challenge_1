package com.demo.userlocationrecording;

import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class MapActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	GoogleMap map;
	private static String date;
	private LoaderCallbacks<Cursor> loader;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_map);
		loader = this;
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		date = getIntent().getStringExtra("CHOSEN_DATE");
		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			
			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				getSupportLoaderManager().initLoader(0, null, loader);
			}
		});
	}

	private void initMap(Cursor lstLocation){
		int size = lstLocation.getCount();
		map.clear();
		lstLocation.moveToNext();
		double lat, lng;
		LatLngBounds.Builder markerBound = new LatLngBounds.Builder();
		while(!lstLocation.isAfterLast()){
			lat = Double.parseDouble(lstLocation.getString(lstLocation.getColumnIndex(UserLocation.COL_LAT)));
			lng = Double.parseDouble(lstLocation.getString(lstLocation.getColumnIndex(UserLocation.COL_LNG)));
			MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
			map.addMarker(marker);
			markerBound.include(new LatLng(lat, lng));
			lstLocation.moveToNext();
		}
		
//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(markerBound.build(), 0));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerBound.build().getCenter(), 10));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		String urlLocationDetail = UserLocationProvider.USER_LOCATION_DATE_SEARCH +"/"+ date;
		return new CursorLoader(this, Uri.parse(urlLocationDetail), new String[]{UserLocation.COL_NAME,  UserLocation.COL_TIME}, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		initMap(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
