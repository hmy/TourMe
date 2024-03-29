package com.cs194.tourme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class FileUploadActivity extends Activity {

	protected boolean _taken = true;
	File sdImageMainDirectory;
	protected static final String PHOTO_TAKEN = "photo_taken";
	protected Intent currentIntent;
	static int Counter = 1;
	String poiName = EachAttractionActivity.poiName;
	static public String pictureName = null;
	static public boolean isUpload = false;
	
	static double picWidth;
	static double picHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {         
			File root = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "TourMe" + File.separator +  poiName + File.separator);
			root.mkdirs();
			sdImageMainDirectory = new File(root, LandingPageActivity.userId + "_" + poiName + 
					"_" + ++LandingPageActivity.numPics + ".jpg");
			currentIntent = this.getIntent();
			startCameraActivity();
			
			FileUploadActivity.pictureName =  sdImageMainDirectory.toString();

			
			
		} catch (Exception e) {
			finish();
			Toast.makeText(this, "Error occured. Please try again later.",
					Toast.LENGTH_SHORT).show();
		}

		
		
	}

	protected void startCameraActivity() {

		Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 0);
		Log.d("abc", "after intent");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:
			finish();
			break;

		case -1:
			try { 
				StoreImage(this, Uri.parse("file://" + sdImageMainDirectory.toString()), sdImageMainDirectory);
				FileUploadActivity.isUpload = true;
				//				StoreImage(this, Uri.parse(data.toURI()), sdImageMainDirectory);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("abc", "AHH Go Here after take picture");
			//			UploadImage uploadPicture = new UploadImage ();
			//			uploadPicture.upload("file://" + sdImageMainDirectory.toString());

			finish();
			finishActivity(0);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.getBoolean(FileUploadActivity.PHOTO_TAKEN)) {
			_taken = true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(FileUploadActivity.PHOTO_TAKEN, _taken);
	}

	public static void StoreImage(Context mContext, Uri imageLoc, File imageDir) {
		Bitmap bm = null;
		Bitmap resized = null;
		int heightScale, widthScale;
		
		try {
			bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
			FileOutputStream out = new FileOutputStream(imageDir);
			
			heightScale = (int) (bm.getHeight() > bm.getWidth() ? bm.getHeight()/100 : bm.getHeight()/58.8);
			widthScale = (int) (bm.getWidth() > bm.getHeight() ? bm.getWidth()/100 : bm.getWidth()/58.8);
			FileUploadActivity.picWidth = bm.getWidth()/widthScale;
			FileUploadActivity.picHeight = bm.getHeight()/heightScale;
			
			bm = Bitmap.createScaledBitmap(bm, bm.getWidth()/widthScale, 
					bm.getHeight()/heightScale, true);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			bm.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	@Override
	public void onAttachedToWindow() {  
	    Log.i("TESTE", "onAttachedToWindow");
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();  
	}
	*/

}
