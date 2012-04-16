package com.cs194.tourme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LandingPageActivity extends Activity {

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
		
		


	}
}

