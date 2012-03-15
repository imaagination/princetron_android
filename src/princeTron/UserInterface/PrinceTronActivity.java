package princeTron.UserInterface;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PrinceTronActivity extends Activity{

	RelativeLayout _main;
	int tapCount = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		_main = (RelativeLayout)findViewById(R.id.relativeLayout1);


		//		Button leftButton = (Button)findViewById(R.id.leftButton);
		//		Button rightButton = (Button)findViewById(R.id.rightButton);
		//		leftButton.setOnClickListener(new OnClickListener() {
		//			public void onClick(View v) {
		//				Toast toast=Toast.makeText(getApplicationContext(), "Left button pressed", Toast.LENGTH_SHORT);  
		//				toast.show();
		//			}
		//		});
		//		rightButton.setOnClickListener(new OnClickListener() {
		//			public void onClick(View v) {
		//				Toast toast=Toast.makeText(getApplicationContext(), "Right button pressed", Toast.LENGTH_SHORT);  
		//				toast.show();
		//			}
		//		});
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		
		
		View arena = new ArenaView(this, height, width);
		setContentView(arena);
		
		

	}

}









