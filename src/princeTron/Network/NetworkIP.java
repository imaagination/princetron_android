package princeTron.Network;

/* Network Manager class to implement GameToNetwork. Receives and sends messages
   to and from the server. Passes along received messages to GameManager via 
   a NetworkToGame class, and can be told to send messages by being Called
   from the Game Manager 


 Classpath must be set to include JavaWebSockets's build/classes/ directory 
   for this import to work, and to include the json library
   use 'export CLASSPATH=/dir/to/build/classes' command

export CLASSPATH=/Users/andykaier/Documents/cos333/princetron_android/src/princeTron/Network/WebSocket/build/classes/:/Users/andykaier/Documents/cos333/princetron_android/src/princeTron/Network/json/:/Users/andykaier/Documents/cos333/princetron_android/src/princeTron/Network/:/Users/andykaier/Documents/cos333/princetron_android/src/


 */


import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.*;
import org.java_websocket.drafts.*;
import org.java_websocket.handshake.*;

import json.org.json.*;

import princeTron.Engine.Coordinate;
import android.util.Log;
import java.util.Collection;



import princeTron.Engine.*;

public class NetworkIP extends princeTron.Engine.GameNetwork
{

	private GameEngine game;
	private WebSocketClient client;

	public NetworkIP()
	{
		try 
		{
			client = new WebSocketClient(new URI("ws://ec2-107-22-122-48.compute-1.amazonaws.com:8080"), new Draft_17())  
			{

				@Override
				public void onMessage( String message ) 
				{
					Log.i("NetworkIP", message);
					/* Parse message and make appropriate call to Game Manager */
					try
					{
						JSONObject j = new JSONObject(message);
						if (j.has("enterArena"))
						{
							JSONObject ea= j.getJSONObject("enterArena");
							int waitTime = ea.getInt("waitTime");
							game.startGame(waitTime, 2); // 2 players is default for now
							System.out.println("Start Game in " + waitTime);
						}
						else if (j.has("opponentTurn"))
						{
							JSONObject ot = j.getJSONObject("opponentTurn");

							int xPos = ot.getInt("xPos");
							int yPos = ot.getInt("yPos");
							int timestamp = ot.getInt("timestamp");
							boolean isLeft = ot.getBoolean("isLeft");

							game.opponentTurn(1, new Coordinate(xPos,yPos), timestamp, isLeft);
							System.out.println("Turn Occured");
						}
						else if (j.has("gameResult")) {
							JSONObject result = j.getJSONObject("result");
							boolean win = false;
							if (result.getString("result").equals("win")) {
								win = true;
							}
							game.gameOver(win);
						}
						else if (j.has("invitation")) {
							JSONObject invite = j.getJSONObject("invitation");
							String user = invite.getString("user");
							game.passInvitation(user);
						}
						else if (j.has("endGame"))
						{
							
						}
					}
					catch (JSONException e)
					{
						System.out.println("JSON Error Creating Object");
					}

				}
				@Override
				public void onOpen( ServerHandshake handshake ) 
				{
				}

				@Override
				public void onClose( int code, String reason, boolean remote)
				{
				}

				@Override
				public void onError( Exception ex )
				{
				}
			};
			Log.i("NetworkIP", "client made");
			client.connect();
			Log.i("NetworkIP", "client connected");
		}
		catch ( URISyntaxException ex ) 
		{
			ex.printStackTrace();
			Log.e("NetworkIP 109", "" + (client==null));
		}
		catch (Exception e) {
			
		}
	}
	
	public boolean clientIsNull() {
		return client == null;
	}

	// pass GameEngine to GameNetwork so network can call back to GameEngine
	public void setGameEngine (princeTron.Engine.GameEngine engine)
	{
		game = engine;

		try 
		{
			JSONObject j = new JSONObject();
			j.put("connect", true);
			String toSend = j.toString();
			Log.i("NetworkIP", "about to send 'connect' message");
			client.send(toSend);     
			Log.i("NetworkIP", "sent 'connect' message");
			//JSONObject rtp = new JSONObject();
			//rtp.put("readyToPlay", true);
			//String r = rtp.toString();
			//Thread.sleep(1000);
			//client.send(r);
			//Log.i("NetworkIP", "sent rtp");
		}
		catch ( InterruptedException ex) 
		{
			System.out.println("Message Send Failed");
		}
		catch ( JSONException jex)
		{
			System.out.println("JSON Error Starting Game");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// informs the Network that the user has turned                                   
	public void userTurn(princeTron.Engine.Coordinate position, int time, boolean isLeft)  
	{
		try
		{
			JSONObject j = new JSONObject();
			JSONObject turn = new JSONObject();
			turn.put("xPos", position.x);
			turn.put("yPos", position.y);
			turn.put("timestamp", time);
			turn.put("isLeft", isLeft);
			j.put("turn", turn);

			client.send(j.toString());
		}
		catch ( InterruptedException ex )
		{
			System.out.println("Message Send Failed Turn");
		}
		catch ( JSONException jex)
		{
			System.out.println("JSON Error Turn");
		}
	}

	// informs the Network that the user has crashed
	public void userCrash(Coordinate location, int time) 
	{
		try
		{
			JSONObject j = new JSONObject();
			JSONObject collision = new JSONObject();
			collision.put("timestamp", time);
			j.put("collision", collision);

			client.send(j.toString());
		}
		catch ( InterruptedException ex )
		{
			System.out.println("Message Send Failed");
		}
		catch ( JSONException jex)
		{
			System.out.println("JSON Error");
		}
	}

	public void logIn(String username) {
		JSONObject j = new JSONObject();
		JSONObject user = new JSONObject();
		try {
			user.put("user", username);
			j.put("logIn", user);
			client.send(j.toString());
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void acceptInvitation() {
		JSONObject j = new JSONObject();
		try {
			j.put("acceptInvitation", "true");
			client.send(j.toString());
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void readyToPlay(Collection<String> invitations) {
		JSONObject j = new JSONObject();
		JSONObject invites = new JSONObject();
		try {
			if (invitations != null) {
				invites.put("invitations", invitations);
			}
			j.put("readyToPlay", invites);
			client.send(j.toString());
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		//NetworkIP n = new NetworkIP();
		//	n.startGame(5);
		//n.turn(new Coordinate(0,0), 17, true);
		//n.collision(new Coordinate(0,0));
	}


}