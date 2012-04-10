package com.imlikeaninja.conversation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class Conversation {
	// Constants to modify behaviour.
	private static final int MAX_REPETITION = 3;
	
	// The default hint for the Android speech listener to display.
	public static final String DEFAULT_HINT = "Waiting for you to respond...";
	
	private TextToSpeech tts;
	private Listener listener = new Listener() {
		@Override
		public void startVoiceRecognition(Block currentBlock) {
			throw new AssertionError("Need to set a ConversationListener!");
		}
	};

	private final Block droppedBlock = new Block("Alright, I can take a hint.", false);
	private Block currentBlock;
	private Block nextBlock;
	private int repeatCount = 0;
	
	public Block startWith(String firstWords) {
		nextBlock = new Block(firstWords);
		return nextBlock;
	}
	
	public void converse() {
		// If our blocks are null, someone has forgotten to start us off.
		if (nextBlock == null && droppedBlock == null) throw new AssertionError("Can't converse without a first block!");
		
		// If we've moved to a different block, reset the repeat count.
		if (nextBlock != currentBlock) repeatCount = 0;
		
		// If we've hit the max repetitions with no result, drop the conversation.
		if (repeatCount == MAX_REPETITION) {
			nextBlock = droppedBlock;
		}
		currentBlock = nextBlock;
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, currentBlock.id);
		tts.speak(currentBlock.speechToSay, TextToSpeech.QUEUE_FLUSH, params);
		tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
			@Override
			public void onUtteranceCompleted(String utteranceId) {
				if (currentBlock.continueConversation == true) {
					listener.startVoiceRecognition(currentBlock);
					repeatCount++;
					
					// Default is the unintelligible response.
					nextBlock = currentBlock.unintelligibleResponse;
				}
			}
		});
	}
	
	public void handleResponses(List<String> matches) {
		// Find the first match that fits the response. If no matches are found, we're already set to use the unintelligible response.
    	for (String match : matches) {
    		if (currentBlock.understoodResponses.containsKey(match)) {
    			nextBlock = currentBlock.understoodResponses.get(match);
    		}
    	}
	}
	
	public void handleNoResponse() {
		nextBlock = currentBlock.noResponse;
	}
	
	public TextToSpeech getTts() {
		return tts;
	}

	public void setTts(TextToSpeech tts) {
		this.tts = tts;
	}
	
	public void destroy() {
		tts.shutdown();
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public interface Listener {
		public void startVoiceRecognition(Block currentBlock);
	}
	
	public class Block {
		public String id = UUID.randomUUID().toString();
		
		public String speechToSay;
		public boolean continueConversation = true;
		public String hint; 
		public Map<String, Block> understoodResponses = new HashMap<String, Block>();
		public Block unintelligibleResponse = this; // Default response is to repeat.
		public Block noResponse = this; // Default response is to repeat.
		
		public Block(String speech) {
			speechToSay = speech;
		}
		
		public Block(String speech, boolean shouldContinueConversation) {
			speechToSay = speech;
			continueConversation = shouldContinueConversation;
		}
		
		public void setUnintelligible(String sayWhat) {
			unintelligibleResponse = new Block(sayWhat);
		}
		
		public void setNoResponse(String couldntHear) {
			noResponse = new Block(couldntHear);
		}
		
		public void putUnderstood(String theySaid, String weSay) {
			understoodResponses.put(theySaid, new Block(weSay));
		}
	}
}
