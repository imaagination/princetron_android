package princeTron.UserInterface;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Records extends Activity{
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://morning-sword-5225.herokuapp.com/leaderboard/"));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
            System.out.println(page);
            page = page.replaceAll("&quot;", "\'");
            System.out.println(page);
            TextView tv = new TextView(this);
            json.org.json.JSONObject j = new json.org.json.JSONObject(page);
            json.org.json.JSONArray a = j.getJSONArray("users");
            String toDisplay = "";
            for (int i = 0; i < a.length(); i++) {
            	toDisplay += a.getString(i) + "\n";
            }
            tv.setText(toDisplay);
			setContentView(tv);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
