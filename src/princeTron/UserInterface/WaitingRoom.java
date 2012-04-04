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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * WaitingRoom: a player is at this stage while
 * waiting for other players to join
 */
public class WaitingRoom extends Activity {

	/**
	 * Called when Activity is first created. Turns off the title bar, sets up
	 * the content views, and fires up the ArenaView.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Resources resource = this.getResources();
		ImageView imView = new ImageView(this);
		Drawable draw = resource.getDrawable(R.drawable.waitingroompic);
		imView.setImageDrawable(draw);
		

		setContentView(imView);



	}

	public void goToArena(){
		
		/*Intent myIntent = new Intent(this,
				ArenaView.class);
		startActivityForResult(myIntent, 0);*/
	}



}