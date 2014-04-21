package com.demo.userlocationrecording;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
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

public class MainActivity extends ActionBarActivity {

	private Context context;
	private PendingIntent pendingIntent;
	
	private ListView lstRecordedDates;
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
	
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1*60*1000, pendingIntent);
		
		lstRecordedDates = (ListView) findViewById(R.id.id_lst_date);
		lstRecordedDates.setAdapter(new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, 
										new String[]{UserLocation.COL_DATE}, new int[] {android.R.id.text1}, 0));
		
		getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				return new CursorLoader(context, UserLocationProvider.URI_USER_LOCATIONS, new String[] {UserLocation.COL_DATE}, null, null, null);
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
				Intent intent = new Intent(context, ListLocationActivity.class);
				String selectedItemStr = ((Cursor)lstRecordedDates.getAdapter().getItem(position)).getString(1);
				intent.putExtra("CHOSEN_DATE", selectedItemStr);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setVisible(false);
//		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//		searchView.setVisibility(View.GONE);
		
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
