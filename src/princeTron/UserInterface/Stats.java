package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Stats extends Activity{
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       TextView tv = new TextView(this);
	       tv.setText("Hello, Stats");
	       setContentView(tv);
	}
}
