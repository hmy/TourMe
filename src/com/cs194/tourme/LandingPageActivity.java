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
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

public class LandingPageActivity extends Activity {


	static public String userId = "";
	static public int numPics = 0; 
	public Intent intent;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing);	

		// SLEEP 2 SECONDS HERE ...
		intent = new Intent(this, TourMeActivity.class);
		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() { 
			public void run() { 
				startActivity(intent); 
			} 
		}, 2000); 

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
}

