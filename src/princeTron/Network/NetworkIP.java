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

import android.graphics.Point;
import android.util.Log;



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

							game.opponentTurn(1, new Point(xPos,yPos), timestamp, isLeft);
							System.out.println("Turn Occured");
						}
						else if (j.has("endGame"))
						{
							boolean win = false;
							JSONObject eg = j.getJSONObject("endGame");
							if (eg.getString("result").equals("win"))
								win = true;
							game.gameOver(win);
							System.out.println("Game Over");
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
			JSONObject rtp = new JSONObject();
			rtp.put("readyToPlay", true);
			String r = rtp.toString();
			Thread.sleep(1000);
			client.send(r);
			Log.i("NetworkIP", "sent rtp");
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
	public void userTurn(android.graphics.Point position, int time, boolean isLeft)  
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
	public void userCrash(Point location, int time) 
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


	public static void main(String[] args)
	{
		NetworkIP n = new NetworkIP();
		//	n.startGame(5);
		//n.turn(new Point(0,0), 17, true);
		//n.collision(new Point(0,0));
	}


}