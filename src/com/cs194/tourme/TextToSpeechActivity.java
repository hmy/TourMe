package com.cs194.tourme;
// http://www.youtube.com/watch?NR=1&feature=fvwp&v=kNoMFBsdBIo
//THIS ACTIVITY IS NOT USED. JUST USES METHODS FOR COMPARISON FOR CLINT
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TextToSpeechActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    
    TextToSpeech tts;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eachattraction);
        
        //TextView text = (TextView) findViewById(R.id.textView1);
        //text.setText("Hello, we are Tour Me");
   
        
        Button playButton = (Button)findViewById(R.id.buttonPlay);
        Button pauseButton = (Button)findViewById(R.id.buttonPause);
        Button previousButton = (Button)findViewById(R.id.buttonPrevious);
        Button nextButton = (Button)findViewById(R.id.buttonNext);

        //USE THE XML MENU TO IMPLEMENT THESE METHODS. MAKE THE "FUNCTIONS" BELOW, USE "ONCLICK" TO CHANGE VALUE IN XML MENU
        playButton.setOnClickListener(this);  
        pauseButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        
        tts = new TextToSpeech(TextToSpeechActivity.this, new TextToSpeech.OnInitListener() {
			
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if(status != TextToSpeech.ERROR){
					tts.setLanguage(Locale.US);
				}
			}
		});
        
    }

    //Change this in the xml file: chooose button and change "on Click" method
	protected void onButtonPause() {
		// TODO Auto-generated method stub
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		TextView text = (TextView) findViewById(R.id.textViewAttractionDescription);
        String textString = (String)text.getText();
        //Queue Flush drops pre-existing media
        tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);
		
	}
}