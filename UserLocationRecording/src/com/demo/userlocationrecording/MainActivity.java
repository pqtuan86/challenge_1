package com.demo.userlocationrecording;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;
import com.google.android.gms.drive.query.internal.NotFilter;

public class MainActivity extends ActionBarActivity {

	static final String LOCATION_ACCESS_BROADCAST = "create_dialog";
	final int CONTEXT_MENU_LOCATION_LIST 	= 1;
	final int CONTEXT_MENU_LOCATION_MAP		= 2;
	final int NOTIFICATION_ID = 1;
	private Context context;
	private PendingIntent pendingIntent;
	
	private ListView lstRecordedDates;
	private String selectedItemStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		Intent intent = new Intent(context, GPSTrackingService.class);
//		context.startService(intent);
		

		pendingIntent = PendingIntent.getService(this, 0, intent, 0);



		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);



		Calendar calendar = Calendar.getInstance();
	
		calendar.setTimeInMillis(System.currentTimeMillis());
	
		calendar.add(Calendar.SECOND, 2*60);
	
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 2*60*1000, pendingIntent);
		
		lstRecordedDates = (ListView) findViewById(R.id.id_lst_date);
		lstRecordedDates.setAdapter(new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, 
										new String[]{UserLocation.COL_DATE}, new int[] {android.R.id.text1}, 0));
		
		getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				return new CursorLoader(context, UserLocationProvider.URI_USER_LOCATION, new String[] {UserLocation.COL_DATE}, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
				// TODO Auto-generated method stub
				((SimpleCursorAdapter)lstRecordedDates.getAdapter()).swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				// TODO Auto-generated method stub
				((SimpleCursorAdapter)lstRecordedDates.getAdapter()).swapCursor(null);
			}
		});
		
		lstRecordedDates.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context, MapActivity.class);
				Cursor selectedItemCursor = (Cursor)lstRecordedDates.getAdapter().getItem(position);
				selectedItemStr = selectedItemCursor.getString(selectedItemCursor.getColumnIndex(UserLocation.COL_DATE));
//				intent.putExtra("CHOSEN_DATE", selectedItemStr);
//				startActivity(intent);
				registerForContextMenu(lstRecordedDates);
				openContextMenu(lstRecordedDates);
			}
		});
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(LOCATION_ACCESS_BROADCAST.equals(intent.getAction())){
				showSettingsAlert();
			}
		}
		
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i("Onpauseeeeeeeeee", "is called");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(LOCATION_ACCESS_BROADCAST));
		createAnOngoingNotification();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("OnDestroyyyyyyyyyyyyyyyyyy=", "is called");
		NotificationManager mNotificationManager =
	    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    	// mId allows you to update the notification later on.
	    	mNotificationManager.cancel(NOTIFICATION_ID);
		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle(getResources().getString(R.string.context_menu_title));
		menu.addSubMenu(Menu.NONE, CONTEXT_MENU_LOCATION_LIST, Menu.NONE, "Open list location");
		menu.addSubMenu(Menu.NONE, CONTEXT_MENU_LOCATION_MAP, Menu.NONE, "Open map location");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (item.getItemId()) {
		case CONTEXT_MENU_LOCATION_LIST:
			intent = new Intent(context, ListLocationActivity.class);
			intent.putExtra("CHOSEN_DATE", selectedItemStr);
			startActivity(intent);
			break;
		case CONTEXT_MENU_LOCATION_MAP:
			intent = new Intent(context, MapActivity.class);
			intent.putExtra("CHOSEN_DATE", selectedItemStr);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setVisible(false);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("Location access is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }


    private void createAnOngoingNotification(){
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.ic_ongoing)
    	        .setContentTitle("User location")
    	        .setContentText("Recording...");
    	mBuilder.build().flags = Notification.FLAG_ONGOING_EVENT;
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, MainActivity.class);

    	// The stack builder object will contain an artificial back stack for the
    	// started Activity.
    	// This ensures that navigating backward from the Activity leads out of
    	// your application to the Home screen.
//    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//    	// Adds the back stack for the Intent (but not the Intent itself)
//    	stackBuilder.addParentStack(MainActivity.class);
//    	// Adds the Intent that starts the Activity to the top of the stack
//    	stackBuilder.addNextIntent(resultIntent);
//    	PendingIntent resultPendingIntent =
//    	        stackBuilder.getPendingIntent(
//    	            0,
//    	            PendingIntent.FLAG_UPDATE_CURRENT
//    	        );
    	PendingIntent resultPendingIntent =
    		    PendingIntent.getActivity(
    		    this,
    		    0,
    		    resultIntent,
    		    PendingIntent.FLAG_UPDATE_CURRENT
    		);
    	mBuilder.setContentIntent(resultPendingIntent);
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
