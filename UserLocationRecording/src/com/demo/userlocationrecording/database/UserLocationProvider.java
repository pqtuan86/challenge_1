package com.demo.userlocationrecording.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class UserLocationProvider extends ContentProvider {
	
	// All URIs share these parts
    public static final String AUTHORITY = "com.demo.userlocationrecording.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all user locations
    public static final String USER_LOCATIONS = SCHEME + AUTHORITY + "/userlocation";
    public static final Uri URI_USER_LOCATIONS = Uri.parse(USER_LOCATIONS);
    // Used for a single person, just add the id to the end
    public static final String USER_LOCATION_BASE = USER_LOCATIONS + "/";
    
    public static final String USER_LOCATION_DATE_SEARCH = USER_LOCATION_BASE + "date";
    public static final String USER_LOCATION_NAME_SEARCH = USER_LOCATION_BASE + "namesearch";
    

	
	public UserLocationProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
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
        if (URI_USER_LOCATIONS.equals(uri)) {
        	// query group by date, choose distinct
        	result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(true, UserLocation.TABLE_NAME, UserLocation.FIELDS, null, null, UserLocation.COL_DATE,
                            null, null, null);
        	result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
        }
        else if (uri.toString().startsWith(USER_LOCATION_DATE_SEARCH)) {
        	String chosen_date = uri.getLastPathSegment();
            result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(UserLocation.TABLE_NAME, UserLocation.FIELDS,
                            UserLocation.COL_DATE + " IS ?",
                            new String[] { chosen_date }, null, null,
                            null, null);
            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
        } else if (uri.toString().startsWith(USER_LOCATION_NAME_SEARCH)) {
        	String chosen_date = selectionArgs[0];
        	String searchText = selectionArgs[1];
            result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
//                    .query(UserLocation.TABLE_NAME, UserLocation.FIELDS,
//                            UserLocation.COL_DATE + " IS ?",
//                            new String[] { chosen_date }, null, null,
//                            null, null);
                    .query(UserLocation.TABLE_NAME, UserLocation.FIELDS,
                            UserLocation.COL_DATE + " IS ? AND " + UserLocation.COL_NAME + " LIKE ?",
                            new String[] { chosen_date, "%"+searchText+"%" }, null, null, null, null);
            result.setNotificationUri(getContext().getContentResolver(), URI_USER_LOCATIONS);
        }
        else {
        throw new UnsupportedOperationException("Not yet implemented");
        }

        return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
}
