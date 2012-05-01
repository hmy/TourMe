package com.cs194.tourme;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class InstructionActivity extends LocalizedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);

	}
	
	/*
	@Override
	public void onAttachedToWindow() {  
	    Log.i("TESTE", "onAttachedToWindow");
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();  
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_HOME:
			LandingPageActivity.mlocManager.removeUpdates(LandingPageActivity.mlocListener);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	*/
}
