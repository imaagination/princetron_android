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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Tron: a simple game that everyone can enjoy.
 */
public class PrinceTron extends Activity {

	/**
	 * Called when Activity is first created. Turns off the title bar, sets up
	 * the content views, and fires up the ArenaView.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		setContentView(R.layout.homescreen);

		// initialize the home screen buttons
		Button newgamebutton = (Button) findViewById(R.id.newgamebutton);
		Button statsbutton = (Button) findViewById(R.id.statsbutton);
		Button recordsbutton = (Button) findViewById(R.id.recordsbutton);
		Button profilebutton = (Button) findViewById(R.id.profilebutton);

		// set up listeners for the home screen buttons
		newgamebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {		
				Intent myIntent = new Intent(view.getContext(),
						WaitingRoom.class);
				startActivityForResult(myIntent, 0);				
			}
		});

		statsbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						Stats.class);
				startActivityForResult(myIntent, 0);		
			}
		});

		recordsbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {		
				Intent myIntent = new Intent(view.getContext(),
						Records.class);
				startActivityForResult(myIntent, 0);		
			}
		});

		profilebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						Profile.class);
				startActivityForResult(myIntent, 0);		
			}
		});







	}




}