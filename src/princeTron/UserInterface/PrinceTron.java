/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package princeTron.UserInterface;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * PrinceTron: a simple game that everyone can enjoy.
 * This is the main activity, it calls all other activities,
 * sets up the sounds, preferences, and gets the username data
 * from the google account name.
 */
public class PrinceTron extends Activity {
	private static final String TAG = "PrinceTron";
	public static final String PREFS_NAME = "MyPrefsFile";
	private String tutorialMessage = "Welcome to PrinceTron!\nThe game is simple: " +
			"touch the left half of the screen to turn left, and the right to turn right." +
			"\nToggle the sound and vibration settings by pressing the Menu button.";
	private String userName;


	//menu buttons for the about popup and for toggling sound and vibration
	private MenuItem mItemAbout;
	private MenuItem mItemSilent;
	private MenuItem mItemVibrate;

	private Vibrator vibe;

	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	/**
	 * Called when Activity is first created. Turns off the title bar and sets up
	 * the content views.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set up screen, set content view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.princetron_layout);
		TextView tv = (TextView) findViewById(R.id.hello);

		//get the google account associated with this phone
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		if(accounts.length != 0){
			userName = accounts[0].name;
			userName = userName.substring(0, userName.length() - 10); //get rid of @gmail.com
		}else
			userName = "_____androidEmulator_____";

		tv.setText("\"Hello, " + userName + "\"");


		//get user settings for sound and vibration
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		if (!settings.contains("silent")) {
			editor.putBoolean("silent", true);
			editor.commit();
			MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
		} else{
			boolean soundOn = settings.getBoolean("soundOn", true);
			if(soundOn){
				//turn on sounds if appropriate
				MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
				Log.i(TAG, "Started Sound");
			}
		}

		if (!settings.contains("vibrate")) {
			editor.putBoolean("vibrate", true);
			editor.commit();
		}
	}



	public void onStart() {
		super.onStart();

		//show tutorial if this is the first time or if user wants to see it again
		boolean isFirstTime = settings.getBoolean("firstTime", true);
		if (isFirstTime) {
			AlertDialog.Builder tutorialBuilder = new AlertDialog.Builder(this);
			LayoutInflater inflater = LayoutInflater.from(this);
			View tutorialLayout = inflater.inflate(R.layout.tutorial_screen, null);
			tutorialBuilder.setView(tutorialLayout);
			final CheckBox dontShowAgain = (CheckBox)tutorialLayout.findViewById(R.id.dontShowAgain);
			tutorialBuilder.setNeutralButton("OK, thanks", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int m) {
					boolean checked = dontShowAgain.isChecked();
					if (checked) {
						editor.putBoolean("firstTime", false);
						editor.commit();
					}
				}
			});
			tutorialBuilder.setMessage(tutorialMessage);
			tutorialBuilder.show();
		}
	}


	public boolean onPrepareOptionsMenu(Menu menu) {
		//clear the menu and repopulate it each time
		menu.clear();

		boolean soundOn = settings.getBoolean("soundOn", true);
		Log.i(TAG, "is sound on?" + soundOn);
		if (soundOn) mItemSilent = menu.add("Turn Sound Off");
		else mItemSilent = menu.add("Turn Sound On");

		boolean vibrate = settings.getBoolean("vibrateOn", true);
		Log.i(TAG, "is vibration on?" + vibrate);
		if (vibrate) mItemVibrate = menu.add("Turn Vibrate Off");
		else mItemVibrate = menu.add("Turn Vibrate On");

		mItemAbout = menu.add("About");

		return true;
	}


	//method for launching the game arena
	public void goToGame(View v){
		Log.i(TAG, "starting Arena");
		Intent myIntent = new Intent(v.getContext(),
				Arena.class);
		//pass along username to next activity
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}

	//method for launching the profile view activity
	public void goToProfile(View v){
		Log.i(TAG, "going to Profile");
		Intent myIntent = new Intent(v.getContext(),
				Profile.class);
		//pass along username to next activity
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}

	//method for launching the leaderboard activity
	public void goToLeaderBoard(View v){
		Log.i(TAG, "going to LeaderBoard");
		Intent myIntent = new Intent(v.getContext(),
				Leaderboard.class);
		startActivityForResult(myIntent, 0);	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "Menu Item selected " + item);

		if (item == mItemSilent) {
			boolean soundOn = settings.getBoolean("soundOn", true);
			editor.putBoolean("soundOn", !soundOn);
			editor.commit();
			String text;
			if (soundOn) text = "Sound turned off";
			else text = "Sound turned on";
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
		else if (item == mItemVibrate) {
			boolean vibrateOn = settings.getBoolean("vibrateOn", true);
			editor.putBoolean("vibrateOn", !vibrateOn);
			editor.commit();

			String text;
			if (vibrateOn){
				text = "Vibrate turned off";
			} else {
				long[] pattern = {0,200,200,200};
				vibe.vibrate(pattern, -1);
				text = "Vibrate turned on";
			}

			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			
		} else if (item == mItemAbout) {
			String text = "Program created by:\nMichael Franklin,\n" +
					"Andrew Kaier,\nPeter Maag, and\nKashif Smith"; 
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}

		boolean soundOn = settings.getBoolean("soundOn", true);

		if(soundOn){
			Log.i(TAG, "turning on sound");
			MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
		}else{
			Log.i(TAG, "turning off sound");
			MusicManager.release();
		}

		return true;
	}


	@Override
	protected void onPause() {
		super.onPause();
		boolean soundOn = settings.getBoolean("soundOn", true);
		if(soundOn){
			MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
			Log.i(TAG, "Started Sound");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean soundOn = settings.getBoolean("soundOn", true);
		if(soundOn){
			MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
			Log.i(TAG, "Started Sound");
		}
	}

	@Override 
	public void onDestroy(){
		super.onDestroy();
		MusicManager.release();
	}
}