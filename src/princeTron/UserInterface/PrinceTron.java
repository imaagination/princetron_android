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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

/**
 * Tron: a simple game that everyone can enjoy.
 */
public class PrinceTron extends Activity {

	/**
	 * Called when Activity is first created. Turns off the title bar, sets up
	 * the content views, and fires up the ArenaView.
	 */
	private String userName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get the google account associated with this phone
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		
		//set content view
		setContentView(R.layout.welcome);
		TextView tv = (TextView) findViewById(R.id.welcomeP);
		

		if(accounts.length != 0){
			userName = accounts[0].name;
			userName = userName.substring(0, userName.length() - 10); //get rid of @gmail.com
			tv.setText("\"Hello, " + userName + "\"");
		}else
			tv.setText("Hello.");
	}
	
	public void goToGame(View v){
		Log.i("PrinceTron", "starting Arena");
		Intent myIntent = new Intent(v.getContext(),
				Arena.class);
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}
	
	public void goToProfile(View v){
		Log.i("PrinceTron", "going to Profile");
		Intent myIntent = new Intent(v.getContext(),
				Profile.class);
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);
	}
	
	public void goToLeaderBoard(View v){
		Log.i("PrinceTron", "going to LeaderBoard");
		Intent myIntent = new Intent(v.getContext(),
				Leaderboard.class);
		myIntent.putExtra("userName", userName);
		startActivityForResult(myIntent, 0);	
	}

}