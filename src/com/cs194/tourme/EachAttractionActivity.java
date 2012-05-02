package com.cs194.tourme;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EachAttractionActivity extends LocalizedActivity {


	// http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/media/MediaPlayerDemo_Audio.html
	TextToSpeech tts = null;
	private static final String TAG = "MediaPlayerDemo";
	private MediaPlayer mMediaPlayer = null;

	private String path;
	private boolean firstTimePlaying = true;
	private int playBackPosition = 0;
	private int goBack = 1300;
	HashMap<String, String> myHashRender;		

	static String poiName = AttractionRouteActivity.poiName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		String pictureUri = "";
		String description = "";

		super.onCreate(savedInstanceState);
		setContentView(R.layout.eachattraction);

		//Set up Text to Speech, US language
		tts = new TextToSpeech(EachAttractionActivity.this, new
				TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
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
					" in Attractions Activity" ,Toast.LENGTH_SHORT).show();
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
		Toast.makeText(getBaseContext(), "   Please Press Play: \n Then wait for media to load",Toast.LENGTH_SHORT).show();


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
		if(firstTimePlaying){
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
			path = Environment.getExternalStorageDirectory()
					+ File.separator + "TourMe" + File.separator + "speak.mp3";

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
		tts.setSpeechRate((float) 1.3);
		tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);


		//push to SD card
		myHashRender = new HashMap<String, String>();
		String destFileName = Environment.getExternalStorageDirectory()
				+ File.separator + "TourMe" + File.separator + "speak.mp3";
		myHashRender.put(TextToSpeech.Engine.KEY_PARAM_STREAM, textString);
		tts.synthesizeToFile(textString, myHashRender, destFileName);

	}

	public void buttonOnButtonPrevious(View v) {
		mMediaPlayer.pause();
		playBackPosition = 0;
		mMediaPlayer.seekTo(playBackPosition);
		mMediaPlayer.start();

	}

	//upload picture
	private void uploadPicture() throws UnsupportedEncodingException{

		Toast.makeText(getApplicationContext(), 
				"Uploading your picture to server. Please be patient. Thank you", 
				Toast.LENGTH_SHORT).show();

		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		String pathToOurFile = FileUploadActivity.pictureName;
		String urlServer = "http://ec2-23-20-205-81.compute-1.amazonaws.com/UploadPicture.php";

		Log.d("urlServer", urlServer);


		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try	{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

			Toast.makeText(getApplicationContext(), 
					"Finished uploading the picture, Thank you for waiting!", 
					Toast.LENGTH_SHORT).show();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(getApplicationContext(), 
					"Upload Failed, please try again later.", 
					Toast.LENGTH_SHORT).show();
		}

	}

	private void storePictureIntoRails() {
		//based on DBHandler
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try {

			//			String phpLocation = "http://ec2-23-20-205-81.compute-1.amazonaws.com/postPicture.php?";
			String phpLocation = "http://ec2-23-20-205-81.compute-1.amazonaws.com:3000" +
					"/tour_my_memory/postPicture?";
			String userID = URLEncoder.encode(LandingPageActivity.userId, "UTF-8");
			String title =  URLEncoder.encode(EachAttractionActivity.poiName, "UTF-8");

			String lat = URLEncoder.encode(Double.toString(LandingPageActivity.currLat), "UTF-8");
			String lng = URLEncoder.encode(Double.toString(LandingPageActivity.currLong), "UTF-8");


			Log.d("pictureURI", FileUploadActivity.pictureName);
			String pictureURI = URLEncoder.encode(FileUploadActivity.pictureName.replace(
					"/mnt/sdcard/TourMe/", "/TourMePics/"), "UTF-8");


			Log.d("pictureURI", pictureURI);

			//get time -> description
			Time currTime = new Time(Time.getCurrentTimezone());
			currTime.setToNow();
			String description = URLEncoder.encode(currTime.toString(), "UTF-8");

			//get width / height (this should be int though)		
			String width = URLEncoder.encode(Double.toString(FileUploadActivity.picWidth), "UTF-8");
			String height = URLEncoder.encode(Double.toString(FileUploadActivity.picHeight), "UTF-8");

			// utf-8 format
			String arguments = "pictureURI=" + pictureURI + "&userID=" + userID + "&title=" + title + "&lat=" + 
					lat + "&lng=" + lng + "&description=" + description + "&width=" + width + "&height=" + height;


			//			InputStream is = null;

			HttpParams httpParameters = new BasicHttpParams();
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(phpLocation + arguments);

			Log.d("PHP CALL from Each Attraction Activity", phpLocation + arguments);

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			//			is = entity.getContent();

			//			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//disable home button for now
	@Override
	public void onAttachedToWindow() {  
		Log.i("TESTE", "onAttachedToWindow");
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();  
	}


	@Override
	protected void onResume() {
		Log.d("EachAttractionActivity", Boolean.toString(FileUploadActivity.isUpload));
		try {	
			if(FileUploadActivity.isUpload) {
				uploadPicture();
				FileUploadActivity.isUpload = false;
				storePictureIntoRails();
			}	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
	}

}