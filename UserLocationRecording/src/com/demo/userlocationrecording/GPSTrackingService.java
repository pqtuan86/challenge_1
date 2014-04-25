package com.demo.userlocationrecording;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.demo.userlocationrecording.database.DatabaseHandler;
import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;
import com.demo.userlocationrecording.helper.NetworkUtil;
import com.demo.userlocationrecording.places.ReverseGeocoding;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class GPSTrackingService extends Service implements LocationListener {

	private final String LOCATION_NAME_UNDEFINED = "undefined";
	private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for getting location
    boolean canGetLocation = false;

    // some vars
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 50 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTrackingService(){
    	this.mContext = this;
    }
    
    public GPSTrackingService(Context context) {
        this.mContext = context;
        //getLocation();
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
    	getLocation();
		return Service.START_STICKY;
	}

	protected void getLocation() {
        try {
        	location = null;
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no available GPS provider
            	sendBroadcast();
            	stopSelf();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //Constant.mLocation = location;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                
                
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.i("Location in get====", "lat == " + location.getLatitude() + " lng == " + location.getLongitude());
                            //Constant.mLocation = location;
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                if(NetworkUtil.haveInternetConnection(this)){
                	GetAddressTask getAddNameTask = new GetAddressTask(mContext);
                    getAddNameTask.execute(latitude, longitude);
                } else {
                	// save to database with no place's name
                	addUserLocationToDatabase(UserLocationProvider.URI_USER_LOCATION_NO_NAME, LOCATION_NAME_UNDEFINED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // stop the service
        stopSelf();
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTrackingService.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * 
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		// get the new location here, get the place name, save to database
		Log.i("Location====", "lat == " + location.getLatitude() + " lng == " + location.getLongitude());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void sendBroadcast(){
		Intent intent = new Intent(MainActivity.LOCATION_ACCESS_BROADCAST);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private class GetAddressTask extends AsyncTask<Double, Void, String>{

		private Context mCtx;
		
		public GetAddressTask(Context context){
			
			this.mCtx = context;
		}
		
		@Override
		protected String doInBackground(Double... params) {
			// TODO Auto-generated method stub
			
			Double lat, lng;
			lat = params[0];
			lng = params[1];
			String nameOfPlace = ReverseGeocoding.getAddressFromCoordinates(lat, lng);
//			Log.i("received address============", nameOfPlace);
			return nameOfPlace;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			if(result != null){
				addUserLocationToDatabase(UserLocationProvider.URI_USER_LOCATION, result);
			}
		}
		
	}
	
	private void addUserLocationToDatabase(Uri uri, String name){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
		String currentDateTime = sdf.format(new Date());
		String currentDate = currentDateTime.split("_")[0];
		String currentTime = currentDateTime.split("_")[1];
		Log.i("currentDate=======", currentDate);
		UserLocation usrLocation = new UserLocation(currentDate, currentTime, String.valueOf(latitude), String.valueOf(longitude), name);
		mContext.getContentResolver().insert(uri, usrLocation.getContent());
//		DatabaseHandler.getInstance(mContext).putUserLocation(usrLocation);
		
	}

}
