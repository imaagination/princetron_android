package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import princeTron.Engine.*;
import android.util.Log;

public class Arena extends Activity{

	private GameEngineThread engine;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Resources resource = this.getResources();
		ImageView imView = new ImageView(this);
		Drawable draw = resource.getDrawable(R.drawable.waitingroompic);
		imView.setImageDrawable(draw);
		setContentView(imView);
		engine = new GameEngineThread();
		setContentView(R.layout.arena_layout);
		engine.start();
		while (!engine.isReady());
		Log.i("Arena 29", "going to arena");
		goToArena();
	}

	public void goToArena(){
		setContentView(R.layout.arena_layout);
		ArenaView mArenaView = (ArenaView) findViewById(R.id.arena);
		mArenaView.setGameEngine(engine);
		mArenaView.setTextView((TextView) findViewById(R.id.text));
		mArenaView.setMode(ArenaView.READY);
	}
}
