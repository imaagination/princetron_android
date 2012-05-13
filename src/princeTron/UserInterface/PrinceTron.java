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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * PrinceTron: a simple game that everyone can enjoy.
 */
public class PrinceTron extends Activity {
	private String userName;
	private MenuItem mItemAbout;


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
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("PrinceTron", "onCreateOptionsMenu");
		mItemAbout = menu.add("About");
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
		

		return true;
	}

}