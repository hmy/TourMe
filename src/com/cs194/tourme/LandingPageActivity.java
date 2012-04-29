package com.cs194.tourme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.MapController;

public class LandingPageActivity extends Activity {


	static public String userId = "";
	static public int numPics = 0; 
	public Intent intent;

	static public MapController mcForListener;
	static public LocationManager mlocManager;
	static public LocationListener mlocListener;

	static public double currLat;
	static public double currLong;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing);

		LandingPageActivity.mlocManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		LandingPageActivity.mlocListener = new MyLocationListener(getApplicationContext());
		LandingPageActivity.mlocManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 30*1000, 0, LandingPageActivity.mlocListener);  
		try {
			LandingPageActivity.currLat = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			LandingPageActivity.currLong = LandingPageActivity.mlocManager.
					getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		} catch (Exception e) {
			Log.d("error in loc", "error in AttractionActivity");
			e.printStackTrace();
			LandingPageActivity.currLat = 37.87309;
			LandingPageActivity.currLong = -122.25921;
		}
		
		Toast.makeText(this.getApplicationContext(), "Curr Location is = " + LandingPageActivity.currLat 
				+ " " + LandingPageActivity.currLong, Toast.LENGTH_LONG);
		

		// SLEEP 2 SECONDS HERE ...
		intent = new Intent(this, TourMeActivity.class);
		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() { 
			public void run() { 
				startActivity(intent); 
			} 
		}, 5000); 

		//creating user name
		File userIdDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "TourMeUser" + File.separator);
		userIdDir.mkdirs();
		userIdDir.setWritable(true); 

		File userIdLog = new File(userIdDir, "userIdLog");
		userIdLog.setWritable(true); 

		boolean userExist = false;

		try {
			userExist = userIdLog.createNewFile();

			if(userExist) {
				BufferedWriter userIdWriter = new BufferedWriter(new FileWriter (userIdLog));
				Random rand = new Random();
				String uniqueUserId = Secure.ANDROID_ID + "_" + System.currentTimeMillis() + "_" + rand.nextInt(99999) + "\n";
				userIdWriter.write(uniqueUserId);
				LandingPageActivity.userId = uniqueUserId;
				userIdWriter.close();
			} else {
				//reading the userId in filename
				FileInputStream fstream = new FileInputStream(Environment.getExternalStorageDirectory()
						+ File.separator + "TourMeUser" + File.separator + "userIdLog");
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				//read line and assign to userId
				LandingPageActivity.userId = br.readLine();
				Log.d("UserID already exists, user id is = ", LandingPageActivity.userId);
				//Close the input stream
				in.close();
			}

		}  catch (IOException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d("userid", LandingPageActivity.userId);





	}

	@Override
	public void onPause() {
		super.onPause();
		LandingPageActivity.mlocManager.removeUpdates(LandingPageActivity.mlocListener);
	}
}

