package com.cs194.tourme;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class DetectMovementMapView extends com.google.android.maps.MapView {
	
	private int oldZoomLevel=-1;
	
	public DetectMovementMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DetectMovementMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DetectMovementMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	//touch event
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {

			Log.d("DetectMovementMapView", "hello1");
		}
		return super.onTouchEvent(ev);
	}

	//zooming
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != oldZoomLevel) {
			Log.d("DetectMovementMapView", "hello2");
			oldZoomLevel = getZoomLevel();
		}
	}

}