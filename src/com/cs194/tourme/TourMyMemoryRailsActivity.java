package com.cs194.tourme;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TourMyMemoryRailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rails);
		
		//load rails	
		WebView railsView = (WebView) findViewById(R.id.railsView);
		railsView.getSettings().setJavaScriptEnabled(true);
		railsView.loadUrl("http://ec2-23-20-205-81.compute-1.amazonaws.com:3000/tour_my_memory/showallmaps");
		
	}
	
}
