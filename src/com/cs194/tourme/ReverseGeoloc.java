package com.cs194.tourme;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ReverseGeoloc {

	public static JSONObject getLocationInfo (GeoPoint pos, String language) {

		HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps" +
				"/api/geocode/json?latlng=" + pos.getLatitudeE6()/1E6 + ","
				+ pos.getLongitudeE6()/1.0E6 + "&sensor=true&language" + language);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			Log.d("ClientProtocolException in ReverseGeoloc", e.getMessage());
		} catch (IOException e) {
			Log.d("IOException in ReverseGeoloc", e.getMessage());
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static String getCityName (JSONObject jsonObject) {

		String address = null;
		
		try {
			address = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getString("formatted_address");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Cannot find approximate location from given points";
		}
		return address.substring(address.indexOf(",")+2, address.length());
	}

}
