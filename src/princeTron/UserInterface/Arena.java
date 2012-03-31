package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Arena extends Activity{
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arena_layout);
		ArenaView mArenaView = (ArenaView) findViewById(R.id.arena);
		mArenaView.setTextView((TextView) findViewById(R.id.text));
		mArenaView.setMode(ArenaView.READY);
	}
}
