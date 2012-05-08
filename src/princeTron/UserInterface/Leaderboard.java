package princeTron.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormatSymbols;

import json.org.json.JSONArray;
import json.org.json.JSONException;
import json.org.json.JSONObject;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Leaderboard extends ListActivity{
	private TextView tv;
	private EfficientAdapter adap;
	private static String[] data;
	private String leaderLink = "http://www.princetron.com/leaderboard/";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.leaders);


		try {
			data = DownloadLeaders();
		} catch (JSONException e) {
			e.printStackTrace();
		}


		adap = new EfficientAdapter(this);
		setListAdapter(adap);

	}





	private String[] DownloadLeaders() throws JSONException {
		URL url = null;
		try {
			url = new URL(leaderLink);
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


		//is there a better way?
		String input = builder.toString().replace("&quot;", "\"");

		JSONObject j = new JSONObject(input);
		JSONArray arr = j.getJSONArray("users");
		String[] top10 = new String[10];

		for(int i = 0; i < 10; i++)
			top10[i] = (i + 1) + ".) " + arr.get(i).toString();

		return top10;
	}


	public static class EfficientAdapter extends BaseAdapter implements Filterable {
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Context context;

		public EfficientAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		/**
		 * Make a view to hold each row.
		 */
		public View getView(final int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.adaptor_content, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.textLine = (TextView) convertView.findViewById(R.id.textLine);
				holder.textLine.setTextColor(Color.WHITE);
				holder.iconLine = (ImageView) convertView.findViewById(R.id.iconLine);
				holder.buttonLine = (Button) convertView.findViewById(R.id.buttonLine);

				//				holder.setTextColor(Color.BLUE);




				convertView.setOnClickListener(new OnClickListener() {
					private int pos = position;

					@Override
					public void onClick(View v) {

						//						Toast.makeText(context, "Click-" + String.valueOf(pos), Toast.LENGTH_SHORT).show();    
					}
				});

				holder.buttonLine.setOnClickListener(new OnClickListener() {
					private int pos = position;

					@Override
					public void onClick(View v) {
						
						String userName = data[pos].toString();
						char check = userName.charAt(1);

						if(check == '.')
							userName = userName.substring(4);
						else
							userName = userName.substring(5);
						

						int[] profile;
						try {
							profile = Profile.DownloadProfile(userName);

							String month = new DateFormatSymbols().getMonths()[profile[1]];

							String info = "User Name: " + userName + "\n" +
									"Date Joined: " + month + " " + Profile.Ordinal(profile[0])+ 
									", " + profile[5] + "\n" +
									"User Rank: " + profile[4] + "\n" +
									"Wins: " + profile[2] + "\n" +
									"Losses: " + profile[3];


							Toast toast = Toast.makeText(context, info,
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
					}
				});



				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Get flag name and id
			String filename = "flag_" + String.valueOf(position);
			int id = context.getResources().getIdentifier(filename, "drawable", context.getString(R.string.package_str));

			// Icons bound to the rows.
			if (id != 0x0) {
				mIcon1 = BitmapFactory.decodeResource(context.getResources(), id);
			}

			// Bind the data efficiently with the holder.
			holder.iconLine.setImageBitmap(mIcon1);
			holder.textLine.setText(data[position]);

			return convertView;
		}

		static class ViewHolder {
			TextView textLine;
			ImageView iconLine;
			Button buttonLine;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data[position];
		}

	}



}
