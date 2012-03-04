package com.creamy.hmy.tourme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class TourMeCreahmyActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			exitEvent();
		}
		return true;
	}

	public void showAttractions(View view) {
		Intent intent = new Intent(this, AttractionsActivity.class);
		startActivity(intent);
	}	
	
	public void showMaps(View view) {
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
	public void showInstruction(View view) {
		Intent intent = new Intent(this, InstructionActivity.class);
		startActivity(intent);
	}

	public void exitEvent() {
		String alertTitle = getResources().getString(R.string.app_name);
		String buttonMessage = getResources().getString(R.string.alertMessageExit);
		String buttonYes = getResources().getString(R.string.buttonYes);
		String buttonNo = getResources().getString(R.string.buttonNo);

		new AlertDialog.Builder(TourMeCreahmyActivity.this)
		.setTitle(alertTitle)
		.setMessage(buttonMessage)
		.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				moveTaskToBack(true);
				finish();
			}
		})
		.setNegativeButton(buttonNo, null)
		.show();
	}
}

