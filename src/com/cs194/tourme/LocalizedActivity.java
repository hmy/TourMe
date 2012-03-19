package com.cs194.tourme;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class LocalizedActivity extends Activity {
	protected static Locale locale = null;

	protected void setLocale(Locale newLocale) {
		if(LocalizedActivity.locale != newLocale) {
			LocalizedActivity.locale = newLocale;

			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Intent intent = getIntent();
		finish();
		startActivity(intent);

		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if(locale != null) {
			Resources res = getResources(); 
			DisplayMetrics dm = res.getDisplayMetrics(); 
			Configuration conf = res.getConfiguration(); 
			conf.locale = locale; 
			res.updateConfiguration(conf, dm);
		}
	}
}
