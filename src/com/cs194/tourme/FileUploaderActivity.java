package com.cs194.tourme;

import android.os.Bundle;

import com.phonegap.DroidGap;

public class FileUploaderActivity extends DroidGap {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.loadUrl("file:///android_asset/www/index.html");
	}
	
}
