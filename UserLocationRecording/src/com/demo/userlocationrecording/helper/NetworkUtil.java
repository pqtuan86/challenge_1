package com.demo.userlocationrecording.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	public static boolean haveInternetConnection(Context mContext){
		
		ConnectivityManager connectivityMng = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityMng.getActiveNetworkInfo();
		if(networkInfo != null){
			return networkInfo.isConnectedOrConnecting();
		} else {
			return false;
		}
	}
}
