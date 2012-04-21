package com.cs194.tourme;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
				+ File.separator + ".TourMeUser" + File.separator);
		userIdDir.mkdirs();
		userIdDir.setWritable(true); 

		File userIdLog = new File(userIdDir, ".userIdLog");
		userIdLog.setWritable(true); 

		boolean userExist = false;

		try {
			userExist = userIdLog.createNewFile();

			if(userExist) {
				BufferedWriter userIdWriter = new BufferedWriter(new FileWriter (userIdLog));
				Random rand = new Random();
				String uniqueUserId = Secure.ANDROID_ID + System.currentTimeMillis() + "" + rand.nextInt(99999) + "\n";
				userIdWriter.write(uniqueUserId);
				LandingPageActivity.userId = uniqueUserId;
				userIdWriter.close();
			} 


		}  catch (IOException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d("userid", LandingPageActivity.userId);


	}
}

