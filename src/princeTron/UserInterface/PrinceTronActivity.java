package princeTron.UserInterface;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class PrinceTronActivity extends Activity {
	   /** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      View arena = new ArenaView(this);
	      setContentView(arena);
	   }
}







    