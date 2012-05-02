package com.cs194.tourme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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

import com.google.android.maps.MapController;

public class LandingPageActivity extends Activity {


	static public String userId = "";
	static public Integer numPics = 0; 
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
				LocationManager.GPS_PROVIDER, 20*1000, 0, LandingPageActivity.mlocListener);

		// SLEEP 2 SECONDS HERE ...
		intent = new Intent(this, TourMeActivity.class);
		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() { 
			public void run() { 
				startActivity(intent); 
			} 
		}, 1500); 

		//creating user name
		File userIdDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "TourMe" + File.separator + "UserId");
		userIdDir.mkdirs();
		userIdDir.setWritable(true); 

		File userIdLog = new File(userIdDir, "userIdLog");
		userIdLog.setWritable(true); 

		boolean userExist = false;

		try {
			userExist = userIdLog.createNewFile();

			if(userExist) {
				BufferedWriter userIdWriter = new BufferedWriter(new FileWriter (userIdLog));
				String uniqueUserId = "Android" + Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
				int pictureNumber = 0;
				userIdWriter.write(uniqueUserId + "," + pictureNumber);
				LandingPageActivity.userId = uniqueUserId;
				LandingPageActivity.numPics = pictureNumber; 
				userIdWriter.close();
			} else {
				//reading the userId in filename
				FileInputStream fstream = new FileInputStream(Environment.getExternalStorageDirectory()
						+ File.separator + "TourMe" + File.separator + "UserId" + File.separator + "userIdLog");
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				//read line and assign to userId
				String [] userIdAndNumPic = br.readLine().split(",");
				LandingPageActivity.userId = userIdAndNumPic[0];
				LandingPageActivity.numPics = Integer.parseInt(userIdAndNumPic[1]);
				//Close the input stream
				in.close();
				
				Log.d("LandingPageActivity", LandingPageActivity.userId + "," + LandingPageActivity.numPics);
			}

		}  catch (IOException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	/*
	@Override
	public void onAttachedToWindow() {  
	    Log.i("TESTE", "onAttachedToWindow");
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();  
	}
	*/
	
}

