package com.demo.userlocationrecording.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static DatabaseHandler singleton;

    public static DatabaseHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new DatabaseHandler(context);
        }
        return singleton;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userlocation";

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Good idea to use process context here
        this.context = context.getApplicationContext();
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(UserLocation.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public synchronized UserLocation getUserLocation(final long id){
		
		final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(UserLocation.TABLE_NAME,
        		UserLocation.FIELDS, UserLocation.COL_ID + " IS ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor == null || cursor.isAfterLast()) {
            return null;
        }

        UserLocation item = null;
        if (cursor.moveToFirst()) {
            item = new UserLocation(cursor);
        }
        cursor.close();

        return item;
	}

	public synchronized boolean putUserLocation(final UserLocation userLocation){
		
		boolean success = false;
        int result = 0;
        final SQLiteDatabase db = this.getWritableDatabase();

        if (userLocation.id > -1) {
            result += db.update(UserLocation.TABLE_NAME, userLocation.getContent(),
                    UserLocation.COL_ID + " IS ?",
                    new String[] { String.valueOf(userLocation.id) });
        }

        if (result > 0) {
            success = true;
        } else {
            // Update failed or wasn't possible, insert instead
            final long id = db.insert(UserLocation.TABLE_NAME, null,
                    userLocation.getContent());

            if (id > -1) {
                userLocation.id = id;
                success = true;
            }
        }

        return success;
	}
	
	public synchronized int removeUserLocation(final UserLocation userLocation){
		
		final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(UserLocation.TABLE_NAME,
                UserLocation.COL_ID + " IS ?",
                new String[] { Long.toString(userLocation.id) });

        return result;
	}
	
	
}
