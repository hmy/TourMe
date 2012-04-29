package com.cs194.tourme;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class TourMeActivity extends LocalizedActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Spinner languageSpinner = (Spinner) findViewById(R.id.spinnerChooseLanguage);

		ArrayAdapter<CharSequence> languageSpinnerAdapter = 
				ArrayAdapter.createFromResource(
						this, R.array.languageList, R.layout.languagespinner);

		languageSpinnerAdapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		languageSpinner.setAdapter(languageSpinnerAdapter);

		if (LocalizedActivity.locale == Locale.KOREAN) {
			languageSpinner.setSelection(1);
		} else if (LocalizedActivity.locale == Locale.CHINESE) {
			languageSpinner.setSelection(2);
		} else { 
			languageSpinner.setSelection(0);
		}

		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, 
					View selectedItemView, int position, long id) {
				switch (position) {
				case 0:  setLocale(Locale.ENGLISH);
				break;
				case 1:  setLocale(Locale.KOREAN);
				break;
				case 2:  setLocale(Locale.CHINESE);
				break;
				} 
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});


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
		Intent intent = new Intent(this, ShowMapsActivity.class);
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

		new AlertDialog.Builder(TourMeActivity.this)
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
	
	@Override
	public void onResume() {
		super.onResume();
		LandingPageActivity.mlocManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 30*1000, 0, LandingPageActivity.mlocListener);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LandingPageActivity.mlocManager.removeUpdates(LandingPageActivity.mlocListener);
	}
}

