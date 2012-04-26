package princeTron.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Leaderboard extends ListActivity{
	TextView tv;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv = new TextView(this);




		String[] leaders = DownloadLeaders("http://morning-sword-5225.herokuapp.com/leaderboard/"); 

		setListAdapter(new ArrayAdapter<String>(this, R.layout.leaderboard, leaders));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
						Toast.LENGTH_SHORT).show();
			}
		});



	}





	private String[] DownloadLeaders(String URL) {
		try {
			URL url = null;

			try {
				url = new URL(URL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			BufferedReader reader = null;
			StringBuilder builder = new StringBuilder();
			try {
				try {
					reader = new BufferedReader(new InputStreamReader(
							url.openStream(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				for (String line; (line = reader.readLine()) != null;) {
					builder.append(line.trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException logOrIgnore) {
					}
			}
			String start = "[&quot;";
			int startLen = 7; //length of string "start"
			String reg = "&quot;, &quot;";
			//		Log.i("start index", builder.indexOf(start) + "");
			String part = builder
					.substring(builder.indexOf(start) + startLen);



			String[] top10 = part.split(reg);
			for(int i = 0; i < top10.length; i++){
				if(i != 9){
					top10[i] = (i+1) + " " + top10[i];
				}
				else{
					//get rid of extra stuff at end
					top10[i] = (String) ((i+1) + " " + top10[i].subSequence(0, top10[i].length() - 8)); 
				}
			}
			return top10;

		}
		catch (Exception e) {
			return new String[0];
		}
	}




}
