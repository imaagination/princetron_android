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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
 */
public class PrinceTron extends Activity {
	private String userName;
	private MenuItem mItemAbout;
	private MenuItem mItemSilent;
	private MenuItem mItemVibrate;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private String tutorialMessage = "Welcome to PrinceTron!\nThe game is simple: " +
			"touch the left half of the screen to turn left, and the right to turn right.";


	/**
	 * Called when Activity is first created. Turns off the title bar and sets up
	 * the content views.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//get the google account associated with this phone
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");

		//set content view
		setContentView(R.layout.princetron_layout);
		TextView tv = (TextView) findViewById(R.id.hello);


		if(accounts.length != 0){
			userName = accounts[0].name;
			userName = userName.substring(0, userName.length() - 10); //get rid of @gmail.com
		}else
			userName = "_____androidEmulator_____";

		tv.setText("\"Hello, " + userName + "\"");
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (!settings.contains("silent")) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("silent", true);
			editor.commit();
		}
		if (!settings.contains("vibrate")) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("vibrate", true);
			editor.commit();
		}
	}
	
	public void onStart() {
		super.onStart();
		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
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
                    	SharedPreferences.Editor editor = settings.edit();
                    	editor.putBoolean("firstTime", false);
                    	editor.commit();
                    }
                }
            });
            tutorialBuilder.setMessage(tutorialMessage);
            tutorialBuilder.show();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("PrinceTron", "onCreateOptionsMenu");
		mItemAbout = menu.add("About");
		return true;
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean soundOn = settings.getBoolean("soundOn", true);
		Log.i("PrinceTron", ""+soundOn);
		try {
			menu.removeItem(mItemSilent.getItemId());
		}
		catch (Exception e) {}
		if (soundOn) mItemSilent = menu.add("Turn Sound Off");
		else mItemSilent = menu.add("Turn Sound On");
		
		boolean vibrate = settings.getBoolean("vibrateOn", true);
		Log.i("PrinceTron", ""+vibrate);
		try {
			menu.removeItem(mItemVibrate.getItemId());
		}
		catch (Exception e) {}
		if (vibrate) mItemVibrate = menu.add("Turn Vibrate Off");
		else mItemVibrate = menu.add("Turn Vibrate On");
		
		return true;
	}

	public void goToGame(View v){
		Log.i("PrinceTron", "starting Arena");
		Intent myIntent = new Intent(v.getContext(),
				Arena.class);
		//pass along username to next activity
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}

	public void goToProfile(View v){
		Log.i("PrinceTron", "going to Profile");
		Intent myIntent = new Intent(v.getContext(),
				Profile.class);
		//pass along username to next activity
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}

	public void goToLeaderBoard(View v){
		Log.i("PrinceTron", "going to LeaderBoard");
		Intent myIntent = new Intent(v.getContext(),
				Leaderboard.class);
		startActivityForResult(myIntent, 0);	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("PrinceTron", "Menu Item selected " + item);

		if (item == mItemAbout){
			String text = "Program created by:\nMichael Franklin,\n" +
					"Andrew Kaier,\nPeter Maag, and\nKashif Smith"; 

			
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
		else if (item == mItemSilent) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			boolean soundOn = settings.getBoolean("soundOn", true);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("soundOn", !soundOn);
			editor.commit();
			String text;
			if (soundOn) text = "Sound turned off";
			else text = "Sound turned on";
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
		else if (item == mItemVibrate) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			boolean vibrateOn = settings.getBoolean("vibrateOn", true);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("vibrateOn", !vibrateOn);
			editor.commit();
			String text;
			if (vibrateOn) text = "Vibrate turned off";
			else text = "Vibrate turned on";
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
		

		return true;
	}

}