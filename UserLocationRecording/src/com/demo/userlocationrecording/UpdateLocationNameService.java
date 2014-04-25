package com.demo.userlocationrecording;

import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;
import com.demo.userlocationrecording.places.ReverseGeocoding;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class UpdateLocationNameService extends IntentService {

	private Cursor undefinedNameLocationCursor;
	public UpdateLocationNameService(){
		super("UpdateLocationNameService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Long id;
		String lat, lng, time, date, name;
		undefinedNameLocationCursor = getContentResolver().query(UserLocationProvider.URI_USER_LOCATION_NO_NAME, UserLocation.FIELDS, null, null, null);
		if(undefinedNameLocationCursor.moveToFirst()){
			while(!undefinedNameLocationCursor.isAfterLast()){
				id = undefinedNameLocationCursor.getLong(undefinedNameLocationCursor.getColumnIndex(UserLocation.COL_ID));
				lat = undefinedNameLocationCursor.getString(undefinedNameLocationCursor.getColumnIndex(UserLocation.COL_LAT));
				lng = undefinedNameLocationCursor.getString(undefinedNameLocationCursor.getColumnIndex(UserLocation.COL_LNG));
				date = undefinedNameLocationCursor.getString(undefinedNameLocationCursor.getColumnIndex(UserLocation.COL_DATE));
				time = undefinedNameLocationCursor.getString(undefinedNameLocationCursor.getColumnIndex(UserLocation.COL_TIME));
				name = ReverseGeocoding.getAddressFromCoordinates(Double.parseDouble(lat), Double.parseDouble(lng));
				UserLocation usrLocation = new UserLocation(date, time, lat, lng, name);
				getContentResolver().insert(UserLocationProvider.URI_USER_LOCATION, usrLocation.getContent());
				getContentResolver().delete(UserLocationProvider.URI_USER_LOCATION_NO_NAME, null, new String[]{id.toString()});
				undefinedNameLocationCursor.moveToNext();
			}
		}
	}
	
}
