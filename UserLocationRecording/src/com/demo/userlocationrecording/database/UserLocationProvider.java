package com.demo.userlocationrecording.database;

import com.demo.userlocationrecording.helper.GlobalConstants;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class UserLocationProvider extends ContentProvider {
	
	// All URIs share these parts
    public static final String AUTHORITY = "com.demo.userlocationrecording.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all user locations
    public static final String USER_LOCATION_PATH = "user_location";
    public static final String USER_LOCATION_NO_NAME_PATH = "user_location_no_name";
    public static final Uri URI_USER_LOCATION = Uri.parse(SCHEME + AUTHORITY + "/" + USER_LOCATION_PATH);
    public static final Uri URI_USER_LOCATION_NO_NAME = Uri.parse(SCHEME + AUTHORITY + "/" + USER_LOCATION_NO_NAME_PATH);
    // Used for a single person, just add the id to the end
    public static final String USER_LOCATION_BASE = SCHEME + AUTHORITY + "/" + USER_LOCATION_PATH + "/";
    
    public static final String USER_LOCATION_DATE_SEARCH = USER_LOCATION_BASE + "date";
    public static final String USER_LOCATION_NAME_SEARCH = USER_LOCATION_BASE + "namesearch";
    
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static{
    	sURIMatcher.addURI(AUTHORITY, USER_LOCATION_PATH, GlobalConstants.URI_USER_LOCATION);
    	sURIMatcher.addURI(AUTHORITY, USER_LOCATION_PATH + "/date/*", GlobalConstants.URI_LOCATION_DATE_SEARCH);
    	sURIMatcher.addURI(AUTHORITY, USER_LOCATION_PATH + "/namesearch", GlobalConstants.URI_LOCATION_NAME_SEARCH);
    	sURIMatcher.addURI(AUTHORITY, USER_LOCATION_NO_NAME_PATH, GlobalConstants.URI_USER_LOCATION_NO_NAME);
    }
	
	public UserLocationProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		switch (sURIMatcher.match(uri)) {
		case GlobalConstants.URI_USER_LOCATION:
			return DatabaseHandler.getInstance(getContext())
			.removeUserLocation(Long.parseLong(selectionArgs[0]), UserLocation.TABLE_USER_LOCATION);
		case GlobalConstants.URI_USER_LOCATION_NO_NAME:
			return DatabaseHandler.getInstance(getContext())
			.removeUserLocation(Long.parseLong(selectionArgs[0]), UserLocation.TABLE_USER_LOCATION_NO_NAME);
		default:
			return -1;
		}
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		UserLocation usrLocation = new UserLocation(values);
		switch (sURIMatcher.match(uri)) {
		case GlobalConstants.URI_USER_LOCATION:
			DatabaseHandler.getInstance(getContext())
			.putUserLocation(usrLocation, UserLocation.TABLE_USER_LOCATION);
			break;
		case GlobalConstants.URI_USER_LOCATION_NO_NAME:
			DatabaseHandler.getInstance(getContext())
			.putUserLocation(usrLocation, UserLocation.TABLE_USER_LOCATION_NO_NAME);
			break;
		default:
			break;
		}
		// right now, we don't care about the return Uri
		return uri;
	}

	@Override
	public boolean onCreate() {
		// TODO: Implement this to initialize your content provider on startup.
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO: Implement this to handle query requests from clients.
		Cursor result = null;
		String chosen_date;
		switch (sURIMatcher.match(uri)) {
			case GlobalConstants.URI_USER_LOCATION:
				// query group by date, choose distinct
	        	result = DatabaseHandler
	                    .getInstance(getContext())
	                    .getReadableDatabase()
	                    .query(true, UserLocation.TABLE_USER_LOCATION, UserLocation.FIELDS, null, null, UserLocation.COL_DATE,
	                            null, null, null);
	        	result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATION);
				break;
			
			case GlobalConstants.URI_LOCATION_DATE_SEARCH:
				chosen_date = uri.getLastPathSegment();
	            result = DatabaseHandler
	                    .getInstance(getContext())
	                    .getReadableDatabase()
	                    .query(UserLocation.TABLE_USER_LOCATION, UserLocation.FIELDS,
	                            UserLocation.COL_DATE + " IS ?",
	                            new String[] { chosen_date }, null, null,
	                            null, null);
	            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATION);
				break;
			
			case GlobalConstants.URI_LOCATION_NAME_SEARCH:
				chosen_date = selectionArgs[0];
	        	String searchText = selectionArgs[1];
	            result = DatabaseHandler
	                    .getInstance(getContext())
	                    .getReadableDatabase()
	                    .query(UserLocation.TABLE_USER_LOCATION, UserLocation.FIELDS,
	                            UserLocation.COL_DATE + " IS ? AND " + UserLocation.COL_NAME + " LIKE ?",
	                            new String[] { chosen_date, "%"+searchText+"%" }, null, null, null, null);
	            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATION);
				break;
				
			case GlobalConstants.URI_USER_LOCATION_NO_NAME:
				result = DatabaseHandler.getInstance(getContext())
										.getReadableDatabase()
										.query(UserLocation.TABLE_USER_LOCATION_NO_NAME, UserLocation.FIELDS, null, null, null, null, null);
				break;
			default:
				break;
		}
//        if (URI_USER_LOCATIONS.equals(uri)) {
//        	// query group by date, choose distinct
//        	result = DatabaseHandler
//                    .getInstance(getContext())
//                    .getReadableDatabase()
//                    .query(true, UserLocation.TABLE_NAME, UserLocation.FIELDS, null, null, UserLocation.COL_DATE,
//                            null, null, null);
//        	result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
//        }
//        else if (uri.toString().startsWith(USER_LOCATION_DATE_SEARCH)) {
//        	String chosen_date = uri.getLastPathSegment();
//            result = DatabaseHandler
//                    .getInstance(getContext())
//                    .getReadableDatabase()
//                    .query(UserLocation.TABLE_NAME, UserLocation.FIELDS,
//                            UserLocation.COL_DATE + " IS ?",
//                            new String[] { chosen_date }, null, null,
//                            null, null);
//            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
//        } else if (uri.toString().startsWith(USER_LOCATION_NAME_SEARCH)) {
//        	String chosen_date = selectionArgs[0];
//        	String searchText = selectionArgs[1];
//            result = DatabaseHandler
//                    .getInstance(getContext())
//                    .getReadableDatabase()
//                    .query(UserLocation.TABLE_NAME, UserLocation.FIELDS,
//                            UserLocation.COL_DATE + " IS ? AND " + UserLocation.COL_NAME + " LIKE ?",
//                            new String[] { chosen_date, "%"+searchText+"%" }, null, null, null, null);
//            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
//        }
//        else {
//        throw new UnsupportedOperationException("Not yet implemented");
//        }

        return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
//		UserLocation usrLocation = new UserLocation(values);
//		DatabaseHandler.getInstance(getContext())
//						.putUserLocation(usrLocation, uri.getLastPathSegment());
		// simply call the insert method
		insert(uri, values);
		return 1;
	}
	
	
}
