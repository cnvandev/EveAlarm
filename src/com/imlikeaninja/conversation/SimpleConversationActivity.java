package com.imlikeaninja.conversation;

import java.util.List;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public class SimpleConversationActivity extends ConversationActivity {
	private static final String TAG = "SimpleConversationActivity";
	
	protected Conversation conversation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		conversation = new Conversation();
		conversation.setListener(this);
	}
	
	@Override
	public void ttsInstalled() {
		conversation.setTts(new TextToSpeech(this, this));
	}
	
	@Override
	public void onInit(int status) {
		conversation.converse();
	}
	
	@Override
	public void responsesAvailable(List<String> matches) {
		conversation.handleResponses(matches);
	}

	@Override
	public void noResponse() {
		conversation.handleNoResponse();
	}

	@Override
	public void finishedProcessingResponses() {
		conversation.converse();
	}
	
	@Override
	public void onDestroy() {
		conversation.destroy();
	}
}
