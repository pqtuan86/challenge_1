package com.demo.userlocationrecording.places;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ReverseGeocoding {
	
	private static final String serviceUrl = "http://maps.google.com/maps/api/geocode/json?latlng=";
	private static final String sensor = "&sensor=true";

	private static JSONObject getAddressList(double lat, double lng){
		JSONObject result = null;
		String params = String.valueOf(lat) + "," + String.valueOf(lng);
		String url = serviceUrl + params + sensor;
		HttpPost httpPost = new HttpPost(url);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response;
		InputStream inputStr = null;
		String receivedJsonStr = null;
		
		try{
			response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			inputStr = entity.getContent();
			
		} catch (ClientProtocolException e){
			
		} catch (IOException e){
			
		}
		
		try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
            		inputStr, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStr.close();
            receivedJsonStr = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
        	result = new JSONObject(receivedJsonStr);
        	if("OK".compareTo(result.getString("status")) != 0){
        		return null;
        	}
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
		
		return result;
	}
	
	public static String getAddressFromCoordinates(double lat, double lng){
		String result = "";
		JSONObject listAddObj = getAddressList(lat, lng);
		if(listAddObj != null){
			try {
				JSONObject firstAddObj = listAddObj.getJSONArray("results").getJSONObject(0);
				result = firstAddObj.getString("formatted_address");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			return null;
		}
		return result;
	}
}
