package princeTron.UserInterface;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

/**
 * Tron: a simple game that everyone can enjoy.
 */
public class PrinceTron extends Activity {
	private String userName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get the google account associated with this phone
		//AccountManager am = AccountManager.get(this);
		//Account[] accounts = am.getAccountsByType("com.google");
		
		
		//set content view
		setContentView(R.layout.homescreen);
		//TextView tv = (TextView) findViewById(R.id.welcomePrompt);

		/*
		if(accounts.length != 0){
			userName = accounts[0].name;
			userName = userName.substring(0, userName.length() - 10); //get rid of @gmail.com
			tv.setText("Hello, " + userName + ". What would you like to do?");
		}else
			tv.setText("Hello." + " What would you like to do?");
			*/
		userName = "peterman123";

		Button newgamebutton = (Button) findViewById(R.id.playnowbutton);
		Button profilebutton = (Button) findViewById(R.id.profilebutton);
		Button leaderbutton = (Button) findViewById(R.id.leaderbutton);

		newgamebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Arena.class);
				myIntent.putExtra("userName", userName);
				startActivityForResult(myIntent, 0);				
			}
		});

		profilebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Profile.class);
				myIntent.putExtra("userName", userName);
				startActivityForResult(myIntent, 0);		
			}
		});
		
		leaderbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Leaderboard.class);
				myIntent.putExtra("userName", userName);
				startActivityForResult(myIntent, 0);				
			}
		});

	}

}