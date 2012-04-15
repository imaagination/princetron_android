package princeTron.UserInterface;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity{
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("Hello, Profile");
		setContentView(tv);


		AccountManager am = AccountManager.get(this); // "this" references the current Context

		Account[] accounts = am.getAccountsByType("com.google");

		for(int i = 0; i < accounts.length; i++)
			Toast.makeText(this, accounts[i].name, Toast.LENGTH_SHORT).show();
	}
}
