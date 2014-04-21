package com.demo.userlocationrecording;


import com.demo.userlocationrecording.database.UserLocation;
import com.demo.userlocationrecording.database.UserLocationProvider;
import com.demo.userlocationrecording.fragment.LocationDetailFragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ListLocationActivity extends ActionBarActivity implements LoaderCallbacks<Cursor>{

	static final int GET_LOCATION_LOADER = 0;
	static final int LOCATION_SEARCH_LOADER = 1;
	private static String date;
	private ListView lstLocation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list_of_locations);

		date = getIntent().getStringExtra("CHOSEN_DATE");
		Log.i("Chosen date =====", date);
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.detail_container, LocationDetailFragment.newInstance(date)).commit();
//		}
		// make onCreateOptionsMenu called again
		supportInvalidateOptionsMenu();
		lstLocation = (ListView) findViewById(R.id.id_lst_location);
		
		lstLocation.setAdapter(new SimpleCursorAdapter(this, R.layout.location_lst_item, null, 
				new String[]{UserLocation.COL_NAME, UserLocation.COL_TIME}, new int[] {R.id.id_tv_item_name, R.id.id_tv_item_time}, 0));
		handleNewIntent(getIntent());
	}

	private void handleNewIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
        	Bundle arg = new Bundle();
        	arg.putString("FILTER", intent.getStringExtra(SearchManager.QUERY));
        	getSupportLoaderManager().restartLoader(LOCATION_SEARCH_LOADER, arg, this);
        }else{
        	getSupportLoaderManager().restartLoader(GET_LOCATION_LOADER, null, this);
        }
    }
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		handleNewIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setVisible(true);
		MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// TODO Auto-generated method stub
				handleSearchViewCollapsed();
				return true;
			}
		});
		
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setVisibility(View.VISIBLE);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
 
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	private void handleSearchViewCollapsed(){
		getSupportLoaderManager().restartLoader(GET_LOCATION_LOADER, null, this);
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

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
		// TODO Auto-generated method stub
		Log.i("chosen date in loader ======", date);
		if(loaderID == GET_LOCATION_LOADER){
			String urlLocationDetail = UserLocationProvider.USER_LOCATION_DATE_SEARCH +"/"+ date;
			return new CursorLoader(this, Uri.parse(urlLocationDetail), new String[]{UserLocation.COL_NAME,  UserLocation.COL_TIME}, null, null, null);
		} else {
			String filter = args.getString("FILTER");
			String urlLocationDetail = UserLocationProvider.USER_LOCATION_NAME_SEARCH;
			return new CursorLoader(this, Uri.parse(urlLocationDetail), new String[]{UserLocation.COL_NAME,  UserLocation.COL_TIME}, null, 
					new String[]{date, filter}, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		((SimpleCursorAdapter)lstLocation.getAdapter()).swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		((SimpleCursorAdapter)lstLocation.getAdapter()).swapCursor(null);
	}
}
