package com.cs194.tourme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


public class RouteCustomOverLay extends Overlay { 
	private GeoPoint gp1;
	private GeoPoint gp2;
	private int mRadius = 6;
	private int mode = 0;
	private int defaultColor;
	private String text="";
	private Bitmap img = null;
//	private Bitmap bmp = null;
	private Context parentContext = null;

	public RouteCustomOverLay (GeoPoint gp1, GeoPoint gp2, int mode, Context context) { 
		this.gp1 = gp1; 
		this.gp2 = gp2;
		this.mode = mode;
		// no defaultColor
		this.defaultColor = -1; 
		this.parentContext = context;
//		this.bmp = BitmapFactory.decodeResource(parentContext.getResources(), R.drawable.marker); 
	} 


	public RouteCustomOverLay (GeoPoint gp1, GeoPoint gp2, int mode, int defaultColor, Context context) { 
		this.gp1 = gp1; 
		this.gp2 = gp2;
		this.mode = mode;
		this.defaultColor = defaultColor;
		this.parentContext = context;
//		this.bmp = BitmapFactory.decodeResource(parentContext.getResources(), R.drawable.marker); 
	} 

	public void setText(String t) {
		this.text = t;
	}

	public void setBitmap(Bitmap bitmap) {
		this.img = bitmap;
	}

	public int getMode() {
		return mode;
	}

	@Override 
	public boolean draw (Canvas canvas, MapView mapView, boolean shadow, long when) { 

		Projection projection = mapView.getProjection(); 
		if (shadow == false) 
		{      
			Paint paint = new Paint(); 
			paint.setAntiAlias(true); 

			Point point = new Point(); 
			projection.toPixels(gp1, point);

			int colorToDraw;
			switch (mode) {
			
			/*
			// case = 1, Creating bullet point for start point 
			case 1: colorToDraw =  (defaultColor == -1) ? Color.BLUE :defaultColor;
					paint.setColor(colorToDraw);
					canvas.drawBitmap(bmp, point.x, point.y, null); 
					
					
//					RectF oval=new RectF(point.x - mRadius, point.y - mRadius,  
//										 point.x + mRadius, point.y + mRadius); 
//					canvas.drawOval(oval, paint); 
					break;
			
			*/
			
			
			// case = 2, drawing path from one place to other
			case 2: colorToDraw =  (defaultColor == -1) ? Color.RED :defaultColor;
					paint.setColor(colorToDraw);
					Point point2 = new Point(); 
					projection.toPixels(gp2, point2);
					paint.setStrokeWidth(4);
					paint.setAlpha(200);       
					canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);   
					break;
					
			// case = 3, Creating bullet point for end point 
			/*
			case 3: colorToDraw =  (defaultColor == -1) ? Color.GREEN :defaultColor;
					paint.setColor(colorToDraw);
					Point point3 = new Point(); 
					projection.toPixels(gp2, point3);
					paint.setStrokeWidth(5);
					paint.setAlpha(120);
					canvas.drawLine(point.x, point.y, point3.x, point3.y ,paint);
					RectF oval3 = new RectF(point3.x - mRadius,point3.y - mRadius,  
											point3.x + mRadius,point3.y + mRadius); 
					//end point
					paint.setAlpha(255);
					canvas.drawOval(oval3, paint);
					break;
					
			case 4: colorToDraw =  (defaultColor == -1) ? Color.GREEN :defaultColor;
					paint.setColor(colorToDraw);
					Point point4 = new Point(); 
					projection.toPixels(gp2, point4); 
					paint.setTextSize(20);
					paint.setAntiAlias(true); 
					canvas.drawBitmap(img, point4.x, point4.y,paint);      
					canvas.drawText(this.text, point4.x, point4.y, paint);
					break;
			
			case 5: colorToDraw =  (defaultColor == -1) ? Color.GREEN :defaultColor;
					paint.setColor(colorToDraw);
					Point point5 = new Point(); 
					projection.toPixels(gp2, point5); 
					paint.setTextSize(20);
					paint.setAntiAlias(true); 
					canvas.drawBitmap(img, point5.x, point5.y,paint);
					break;
			*/
			}

		} 
		return super.draw(canvas, mapView, shadow, when); 
	} 
} 