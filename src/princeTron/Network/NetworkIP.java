/* Network Manager class to implement GameToNetwork. Recieves and sends messages
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

import java.awt.Point;

import org.json.*;

import org.java_websocket.*;
import org.java_websocket.drafts.*;
import org.java_websocket.handshake.ServerHandshake;

import princeTron.Engine.*;

public class NetworkIP extends GameNetwork
{
    private WebSocketClient client;

    public NetworkIP()
    {
	try 
	    {
		client = new WebSocketClient(new URI("SOME URL"), new Draft_17())  
		    {
			
			@Override
			    public void onMessage( String message ) 
			{
			    /* Parse message and make appropriate call to Game Manager */
			    try
				{
				    JSONObject j = new JSONObject(message);
				    if (j.has("start"))
					{
					    System.out.println("Start Game");
					}
				    else if (j.has("turn"))
					{
					    System.out.println("Turn Occured");
					}
				    else if (j.has("game_over"))
					{
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
		
		client.connect();
	    }
        catch ( URISyntaxException ex ) 
	    {
		System.out.println("Invalid URI");
	    }
    }    

    void startGame(int wait_time)
    {
	try 
	    {
		JSONObject j = new JSONObject();
		j.append("Start", Integer.toString(wait_time));
		client.send(j.toString());     
	    }
	catch ( InterruptedException ex) 
	    {
		System.out.println("Message Send Failed");
	    }
	catch ( JSONException jex)
	    {
		System.out.println("JSON Error Starting Game");
	    }

    }

    void turn(Point position, int time, boolean isLeft)
    {
        try
            {
		JSONObject j = new JSONObject();
		j.append("Turn", Integer.toString(time));
		j.append("Left", Boolean.toString(isLeft));
		j.append("Position", position.toString());
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

    void collision(Point position)
    {
        try
            {
		JSONObject j = new JSONObject();
		j.append("Collision", position.toString());
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
	n.startGame(5);
	n.turn(new Point(0,0), 17, true);
	n.collision(new Point(0,0));
    }
}