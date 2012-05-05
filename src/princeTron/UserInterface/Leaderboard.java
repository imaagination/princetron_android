package princeTron.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormatSymbols;

import org.apache.http.client.ClientProtocolException;

import json.org.json.JSONArray;
import json.org.json.JSONException;
import json.org.json.JSONObject;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Gravity;
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
		String[] leaders = null;
		try {
			leaders = DownloadLeaders();
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		setListAdapter(new ArrayAdapter<String>(this, R.layout.leaderboard, leaders));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				try {
					String userName = ((TextView) view).getText().toString().substring(2);
					int[] profile = Profile.DownloadProfile(userName);
					
					String month = new DateFormatSymbols().getMonths()[profile[1]];
					
					String info = "User Name: " + userName + "\n" +
					"Date Joined: " + month + " " + Profile.Ordinal(profile[0])+ ", " + profile[5] + "\n" +
					"User Rank: " + profile[4] + "\n" +
					"Wins: " + profile[2] + "\n" +
					"Losses: " + profile[3];
					
					
					Toast toast = Toast.makeText(getApplicationContext(), info,
							Toast.LENGTH_LONG);
					
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					
					
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// When clicked, show a toast with the TextView text
//				Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//						Toast.LENGTH_SHORT).show();
				
				
			}
		});



	}





	private String[] DownloadLeaders() throws JSONException {
		URL url = null;
		try {
			url = new URL("http://www.princetron.com/leaderboard/");
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
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException logOrIgnore) {
				}
			}
		}





		String input = builder.toString().replace("&quot;", "\"");
		JSONObject j = new JSONObject(input);
		JSONArray arr = j.getJSONArray("users");
		String[] top10 = new String[10];

		for(int i = 0; i < 10; i++)
			top10[i] = (i + 1) + " " + arr.get(i).toString();




		//		for(int i = 0; i < 10; i++){
		//			Toast.makeText(getApplicationContext(), leaders[i]
		//					, Toast.LENGTH_SHORT).show();
		//		}



		return top10;

	}




}
