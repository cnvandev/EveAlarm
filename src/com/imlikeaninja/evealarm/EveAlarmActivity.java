package com.imlikeaninja.evealarm;

import android.os.Bundle;

import com.imlikeaninja.conversation.Conversation.Block;
import com.imlikeaninja.conversation.SimpleConversationActivity;

public class EveAlarmActivity extends SimpleConversationActivity {
	private static final String TAG = "EveAlarmActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize the conversation blocks.
		Block firstBlock = conversation.startWith("Good morning, Chris.");
		firstBlock.setUnintelligible("Sorry, what was that?");
		firstBlock.setNoResponse("Chris? Are you there?");
		firstBlock.putUnderstood("hey", "Excellent, it worked!");
		firstBlock.putUnderstood("fuck off", "I know, mornings are pretty terrible, but you have to wake up.");
	}
}
