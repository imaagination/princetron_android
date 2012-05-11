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
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Leaderboard extends ListActivity{
	private static String[] data;
	private String leaderLink = "http://www.princetron.com/leaderboard/";
	private PopupWindow popupWindow;




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

		setListAdapter(new EfficientAdapter(this));
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
						url.openStream(), "UTF-8"), 8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(reader != null)
				for (String line; (line = reader.readLine()) != null;) {
					builder.append(line.trim());
				}
			else
				Toast.makeText(this, "Sorry, the leaderboard is currently Down", Toast.LENGTH_LONG).show();
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


		top10[0] = "1.) " + arr.get(0).toString();
		top10[1] = "2.) " + arr.get(1).toString();
		top10[2] = "3.) " + arr.get(2).toString();
		top10[3] = "4.) " + arr.get(3).toString();
		top10[4] = "5.) " + arr.get(4).toString();
		top10[5] = "6.) " + arr.get(5).toString();
		top10[6] = "7.) " + arr.get(6).toString();
		top10[7] = "8.) " + arr.get(7).toString();
		top10[8] = "9.) " + arr.get(8).toString();
		top10[9] = "10.) "+ arr.get(9).toString();

		return top10;
	}


	public class EfficientAdapter extends BaseAdapter implements Filterable {
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
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.adaptor_content, null);

				holder = new ViewHolder();
				holder.textLine = (TextView) convertView.findViewById(R.id.textLine);
				holder.textLine.setTextColor(Color.WHITE);
				holder.iconLine = (ImageView) convertView.findViewById(R.id.iconLine);
				holder.buttonLine = (Button) convertView.findViewById(R.id.buttonLine);





				convertView.setOnClickListener(new OnClickListener() {
					private int pos = position;
					@Override
					public void onClick(View v) {    
					}
				});

				holder.buttonLine.setOnClickListener(new OnClickListener() {
					private int pos = position;



					@Override
					public void onClick(View v) {


						LayoutInflater inflater = (LayoutInflater) 
								EfficientAdapter.this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
						popupWindow = new PopupWindow(inflater.inflate(R.layout.dialog,null, false),300,140,true);



						String userName = data[pos].toString();
						char check = userName.charAt(1);

						if(check == '.')
							userName = userName.substring(4);
						else
							userName = userName.substring(5);


						try {
							int[] profile = Profile.DownloadProfile(userName);

							String month = new DateFormatSymbols().getMonths()[profile[1]-1];

							String info =
									"User Rank: " + profile[4] + "\n" +
											"Wins: " + profile[2] + "\n" +
											"Losses: " + profile[3] + "\n" +
											"Date Joined: " + month + " " + Profile.Ordinal(profile[0]) + 
											", " + profile[5] + "\n"
											;


							((TextView)popupWindow.getContentView().findViewById(R.id.Tv1)).setText(userName);							
							((TextView)popupWindow.getContentView().findViewById(R.id.Tv2)).setText(info);

							popupWindow.setBackgroundDrawable(new BitmapDrawable());

							// RelativeLayout01 is Main Activity Root Layout
							popupWindow.showAtLocation(findViewById(R.id.RelativeLayout01), Gravity.CENTER, 0,0);


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

			// Bind the data efficiently with the holder.
			holder.iconLine.setImageBitmap(mIcon1);
			holder.textLine.setText(data[position]);

			return convertView;
		}

		class ViewHolder {
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
			return data[position];
		}

	}

	public void closeDialog(View v) {
		popupWindow.dismiss();

	}



}
