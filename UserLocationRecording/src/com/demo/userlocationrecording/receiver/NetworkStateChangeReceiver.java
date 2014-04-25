package com.demo.userlocationrecording.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.demo.userlocationrecording.UpdateLocationNameService;
import com.demo.userlocationrecording.helper.NetworkUtil;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Network state oooooonnnnnnnnnnnnnnnnnnnnnchanged", Toast.LENGTH_LONG);
		Log.i("Receiver onReceivee........", "Conection state changed");
		if(NetworkUtil.haveInternetConnection(context)){
			Log.i("Receiver onReceivee........", "Internet.....have it!");
			Intent serviceIntent = new Intent(context, UpdateLocationNameService.class);
			context.startService(serviceIntent);
		}
	}

}
