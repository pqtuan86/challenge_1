package com.demo.userlocationrecording.database;

import android.content.ContentValues;
import android.database.Cursor;

public class UserLocation {
	
	// SQL convention says Table name should be "singular", so not Persons
    public static final String TABLE_NAME = "UserLocation";
    // Naming the id column with an underscore is good to be consistent
    // with other Android things. This is ALWAYS needed
    public static final String COL_ID = "_id";
    // These fields can be anything you want.
    public static final String COL_DATE = "date";
    public static final String COL_LAT = "lat";
    public static final String COL_LNG = "lng";
    public static final String COL_NAME = "name";

    // For database projection so order is consistent
    public static final String[] FIELDS = { COL_ID, COL_DATE, COL_LAT,
    	COL_LNG, COL_NAME };

    /*
     * The SQL code that creates a Table for storing User Locations in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
            + COL_ID + " INTEGER PRIMARY KEY,"
            + COL_DATE + " TEXT NOT NULL DEFAULT '',"
            + COL_LAT + " TEXT NOT NULL DEFAULT '',"
            + COL_LNG + " TEXT NOT NULL DEFAULT '',"
            + COL_NAME + " TEXT NOT NULL DEFAULT ''"
            + ")";

    // Fields corresponding to database columns
    public long id = -1;
    public String date = "";
    public String lat = "";
    public String lng = "";
    public String name = "";

    /**
     * No need to do anything, fields are already set to default values above
     */
    public UserLocation() {
    }

    /**
     * Convert information from the database into a UserLocation object.
     */
    public UserLocation(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.id = cursor.getLong(0);
        this.date = cursor.getString(1);
        this.lat = cursor.getString(2);
        this.lng = cursor.getString(3);
        this.name = cursor.getString(4);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContent() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_DATE, date);
        values.put(COL_LAT, lat);
        values.put(COL_LNG, lng);
        values.put(COL_NAME, name);

        return values;
    }

}
