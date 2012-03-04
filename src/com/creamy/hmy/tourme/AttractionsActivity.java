package com.creamy.hmy.tourme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AttractionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attractions);
	}
	
	public void showAttraction(View view) {
		Intent intent = new Intent(this, EachAttractionActivity.class);
		startActivity(intent);
	}	
}
