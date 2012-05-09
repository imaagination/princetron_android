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
import java.util.Collection;

import json.org.json.JSONArray;
import json.org.json.JSONException;
import json.org.json.JSONObject;

import org.java_websocket.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import princeTron.Engine.Coordinate;
import princeTron.Engine.GameEngine;
import android.util.Log;

public class NetworkIP 
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
							// parses the json, passes the data to the engine
							JSONObject info = j.getJSONObject("enterArena");
							JSONArray opponentsArray = info.getJSONArray("players");
							int numOp = opponentsArray.length();
							int[] xStarts = new int[numOp];
							int[] yStarts = new int[numOp];
							int[] dirStarts = new int[numOp];
							int myId;
							for (int i = 0; i < opponentsArray.length(); i++) {
								JSONObject opponent = opponentsArray.getJSONObject(i);
								xStarts[i] = opponent.getInt("xStart");
								yStarts[i] = opponent.getInt("yStart");
								String d = opponent.getString("dirStart");
								if (d.equals("north")) dirStarts[i] = GameEngine.NORTH;
								if (d.equals("east")) dirStarts[i] = GameEngine.EAST;
								if (d.equals("south")) dirStarts[i] = GameEngine.SOUTH;
								if (d.equals("west")) dirStarts[i] = GameEngine.WEST;
								Log.i("NetworkIP", "id = " + i + "\tdir = " + d);
							}
							myId = info.getInt("playerId");
							game.enterArena(xStarts, yStarts, dirStarts, myId);
						}
						else if (j.has("startGame")) {
							game.startGame();
						}
						else if (j.has("opponentTurn"))
						{
							JSONObject ot = j.getJSONObject("opponentTurn");

							int timestamp = ot.getInt("timestamp");
							boolean isLeft = ot.getBoolean("isLeft");
							int playerId = ot.getInt("playerId");
							game.opponentTurn(playerId, timestamp, isLeft);
							System.out.println("Turn Occured");
						}
						else if (j.has("gameResult")) {
							JSONObject result = j.getJSONObject("gameResult");
							boolean isWin = false;
							if (result.getString("result").equals("win")) {
								isWin = true;
							}
							int playerId = result.getInt("playerId");
							game.gameResult(playerId, isWin);
						}
						else if (j.has("invitation")) {
							JSONObject invite = j.getJSONObject("invitation");
							String user = invite.getString("user");
							Log.i("NetworkIP", "got invitation from " + user);
							game.invitationReceived(user);
						}
						else if (j.has("endGame"))
						{
							Log.i("NetworkIP", "ending game");
							game.endGame();
						}
						else if (j.has("lobby")) {
							JSONObject lobby = j.getJSONObject("lobby");
							JSONArray array = lobby.getJSONArray("users");
							String[] users = new String[array.length()];
							for (int i = 0; i < users.length; i++) {
								try {
									users[i] = array.getString(i);
								}
								catch (Exception e) {
									users[i] = "";
								}
							}
							game.newLobby(users);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
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

	// Set game engine
	public void setGameEngine (GameEngine engine)
	{
		game = engine;
	}
	/*
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
		
	}*/

	// informs the Network that the user has turned                                   
	public void sendTurn(int time, boolean isLeft)  
	{
		try
		{
			JSONObject j = new JSONObject();
			JSONObject turn = new JSONObject();
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
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// informs the Network that the user has crashed
	public void sendCollision(int time) 
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
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logIn(String username) {
		try {
			Log.i("NetworkIP", "logging in");
			JSONObject j = new JSONObject();
			JSONObject user = new JSONObject();
			user.put("user", username);
			j.put("logIn", user);
			while (client.getConnection() == null);
			System.out.println("connection isn't null");
			while (client.getReadyState() != 1);
			System.out.println(j.toString() + " about to send login");
			client.send(j.toString());
			System.out.println(j.toString() + " sent login");
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void acceptInvitation() {
		Log.i("NetworkIP", "accepting invitation");
		JSONObject j = new JSONObject();
		try {
			j.put("acceptInvitation", true);
			client.send(j.toString());
			Log.i("NetworkIP", "invitation acceptance sent: " + j.toString());
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendInvites (String[] userInvites) {
		JSONObject j = new JSONObject();
		JSONObject invites = new JSONObject();
		try {
			if (userInvites != null) {
				invites.put("invitations", userInvites);
			}
			j.put("readyToPlay", invites);
			Log.d("NetworkIP", "Sending " + j.toString());
			client.send(j.toString());
		}
		catch (JSONException jex) {
			jex.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (java.nio.channels.NotYetConnectedException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			client.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}