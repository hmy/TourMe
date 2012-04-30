package com.cs194.tourme;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

	public class EachAttractionActivity extends LocalizedActivity {
		
		
		// http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/media/MediaPlayerDemo_Audio.html
		TextToSpeech tts;
		boolean debugging = false;
	    private static final String TAG = "MediaPlayerDemo";
	    private MediaPlayer mMediaPlayer;

	    private String path;
	    private boolean firstTimePlaying = true;
	    private int playBackPosition = 0;
	    private int goBack = 1300;
	    HashMap<String, String> myHashRender;		
		
		static String poiName = AttractionRouteActivity.poiName;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub

			String pictureUri = "";
			String description = "";

			super.onCreate(savedInstanceState);
			setContentView(R.layout.eachattraction);

			//Set up Text to Speech, US language
			tts = new TextToSpeech(EachAttractionActivity.this, new
					TextToSpeech.OnInitListener() {

				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					if(status != TextToSpeech.ERROR){
						tts.setLanguage(Locale.US);
					}
				}

			});


			DatabaseHandler dbHandler = new DatabaseHandler ();
			Log.d("ABC", AttractionRouteActivity.poiName);

			//save for later
			//JSONArray AttractionDetail = dbHandler.getDataFromSql("select uri, description from Picture where attraction_id = (select id from Attraction where name = '" + AttractionsActivity.attractionName + "') ");

			JSONArray AttractionDetail = dbHandler.getDataFromSql("select p.uri, p.description from Picture p where p.poi_id = " +
					"(select id from POI where POI.name = '" + AttractionRouteActivity.poiName + "')");

			Log.d("sqlquery", "select p.uri, p.description from Picture p where p.poi_id = " +
					"(select id from POI where POI.name = '" + AttractionRouteActivity.poiName + "')");

			//debug
			try{
				Log.d("eachattraction", AttractionDetail.getJSONObject(0).getString("uri"));
			} catch (Exception e){
				e.printStackTrace();
			}

			try {
				pictureUri = AttractionDetail.getJSONObject(0).getString("uri");	
				description = AttractionDetail.getJSONObject(0).getString("description");
			} catch(JSONException e1){
				Log.d("Json error", e1.toString());
				Toast.makeText(getBaseContext(), "JSONException Has Occured" +
						" in Attractions Activity" ,Toast.LENGTH_LONG).show();
				// no need for below? taken care as default value for this column is this
				//			pictureUri = "http://thegarageblog.com/garage/wp-content/uploads/noimageavailable.jpg";
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			ImageView webView = (ImageView) findViewById(R.id.imageView1);
			//take care when Drawable is null
			Drawable drawable = LoadImageFromWeb (pictureUri);

			webView.setImageDrawable(drawable);

			TextView textView = (TextView) findViewById(R.id.textViewAttractionDescription);
			textView.setText (description); 	
			Toast.makeText(getBaseContext(), "Please wait for media to load",Toast.LENGTH_LONG).show();

	
		}


		private Drawable LoadImageFromWeb(String url) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				return d;
			} catch (Exception e) {
				System.out.println("Error loading image " + e.getMessage());
				return null;
			}
		}

		public void buttonTakePictureOnClick(View v) {
			Intent intent = new Intent (this, FileUploadActivity.class);
			startActivity (intent);
		}

		public void buttonOnButtonPause(View v) throws InterruptedException {
			// We need to mark play back position, otherwise cannot get pause to perform correctly
			// Time 1:09 of http://www.youtube.com/watch?v=hvpYInzY8Xg
			playBackPosition = mMediaPlayer.getCurrentPosition();
			mMediaPlayer.pause();
			if(debugging) Toast.makeText(getBaseContext(), "In pause method, playBackPositions is " + playBackPosition,Toast.LENGTH_LONG).show();
			
		}

		public synchronized void buttonOnButtonPlay(View v) {
			
			if(firstTimePlaying){  //We only want to save to SD card one time!
				mMediaPlayer = new MediaPlayer();
				convertTTSToSD();
			}
			setupFirstTimeMediaUse();
			mMediaPlayer.pause();
			
			//Play will reset to 0 if at beginning or end of media
			if( ( mMediaPlayer.getCurrentPosition() > (mMediaPlayer.getDuration() - 100) ) || 
				(playBackPosition - goBack) < 0) 
				playBackPosition = 0;
			else 
				playBackPosition = playBackPosition - goBack;
				
			
			mMediaPlayer.seekTo(playBackPosition);
			if(debugging)Toast.makeText(getBaseContext(), "In play method, playBackPositions is " + playBackPosition,Toast.LENGTH_SHORT).show();
			if(firstTimePlaying){
				if (debugging) Toast.makeText(getBaseContext(), "Please wait while media is loaded ", Toast.LENGTH_LONG).show();
				mMediaPlayer.setVolume(0, 0);
				mMediaPlayer.start(); //You also need this method here or it won't play after first time either
				mMediaPlayer.setVolume(100, 100);
			}
			else
				mMediaPlayer.start(); //You also need this method here or it won't play after first time either
	
			firstTimePlaying = false;
		}
		
		public void setupFirstTimeMediaUse(){
			
			try {
				path = "/sdcard/test.mp3";
				
		        mMediaPlayer.setDataSource(path);
		        mMediaPlayer.prepare();
		        mMediaPlayer.seekTo(playBackPosition);
		        mMediaPlayer.start();  //You need this method here or it will not play after the first time
		        } catch (Exception e) {
		        	Log.e(TAG, "error: " + e.getMessage(), e);
		        }

		}
		
		public void convertTTSToSD(){

			//read the text and form into words
			TextView text = (TextView)findViewById(R.id.textViewAttractionDescription);
			String textString = (String)text.getText();

			if(debugging) textString = "one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eightteen nineteen twenty";
			tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);
			
			
			//push to SD card
			myHashRender = new HashMap<String, String>();
			String destFileName = "/sdcard/test.mp3";
			myHashRender.put(TextToSpeech.Engine.KEY_PARAM_STREAM, textString);
			tts.synthesizeToFile(textString, myHashRender, destFileName);
			
			if(debugging) Toast.makeText(getBaseContext(), "In convert method, file is synthesizing. Please wait for media to load.",Toast.LENGTH_SHORT).show();
			
		}

		public void buttonOnButtonPrevious(View v) {
			mMediaPlayer.pause();
			playBackPosition = 0;
			mMediaPlayer.seekTo(playBackPosition);
			mMediaPlayer.start();
			if(debugging) Toast.makeText(getBaseContext(), "In reset method, playBackPositions is " + playBackPosition,Toast.LENGTH_SHORT).show();

		}
		
		    @Override
		    protected void onDestroy() {
		        super.onDestroy();
		        // TODO Auto-generated method stub
		        if (mMediaPlayer != null) {
		            mMediaPlayer.release();
		            mMediaPlayer = null;
		        }

		    }

	}