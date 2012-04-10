package com.imlikeaninja.conversation;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import com.imlikeaninja.conversation.Conversation.Block;
import com.imlikeaninja.evealarm.R;

public abstract class ConversationActivity extends Activity implements OnInitListener, Conversation.Listener {
	private static final String TAG = "ConversationActivity";
	
	// Some internal constants that will be checked by us, so it doesn't matter what they are.
	private static final int MY_DATA_CHECK_CODE = 0;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	/** Called when the activity is first created. */
	@Override
 	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() > 0) {
        	Intent checkIntent = new Intent();
    		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
      		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        } else {
            Toast.makeText(this, "Speech recognizer not present - please download Google Voice Search from the Play Store.", Toast.LENGTH_LONG).show();
            finish();
        }
 	}
	
	private void handleTTSInstalledResult(int resultCode) {
		if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
   			// success, create the TTS instance
    		ttsInstalled();
  		} else {
	        // missing data, install it
	        Intent installIntent = new Intent();
	        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	        startActivity(installIntent);
        }
	}
 
 	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
 		switch (requestCode) {
 			// We've asked if Text-To-Speech is installed, set it up if it is or install it if it's not.
 			case MY_DATA_CHECK_CODE:
		  		handleTTSInstalledResult(resultCode);
		  		break;
		  		
		  	// We're coming back after a voice recognition request, run that.
 			case VOICE_RECOGNITION_REQUEST_CODE:
 				if (resultCode == Activity.RESULT_OK) {
 			        // Fill the list view with the strings the recognizer thought it could have heard
 			        List<String> matches = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 			    	responsesAvailable(matches);
 				} else if (resultCode == Activity.RESULT_CANCELED) {
 					// Either cancelled or timed out, handle the response.
 					noResponse();
 				}
 				break;
        }
	 	
	 	super.onActivityResult(requestCode, resultCode, intent);
    }

    public void startVoiceRecognition(Block block) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, (block.hint != null)? block.hint : Conversation.DEFAULT_HINT);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

	public abstract void ttsInstalled();
	public abstract void onInit(int status);
	public abstract void responsesAvailable(List<String> matches);
	public abstract void noResponse();
	public abstract void finishedProcessingResponses();
}