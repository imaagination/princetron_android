package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashMap;

import princeTron.UserInterface.Arena;

import android.os.Handler;
import android.os.Message;

import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	// number of tics since game started
	public Integer numTics = 0;
	private boolean isReady = false;
	private int myId = -1;
	// for collision detection - the proper way is mysteriously not working
	private HashMap<Integer, ArrayList<Integer>> visitedMap = new HashMap<Integer, ArrayList<Integer>>();
	// for queueing turns
	private HashMap<Integer, HashMap<Integer, Boolean>> turnQueue = new HashMap<Integer, HashMap<Integer, Boolean>>();

	private Handler handler;

	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long mMoveDelay = 100;

	public GameEngine(Handler handler) {
		visitedMap = new HashMap<Integer, ArrayList<Integer>>();
		this.handler = handler;
	}
	
	public void passInvitation(String username) {
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.INVITED);
		msg.obj = username;
		msg.sendToTarget();
	}
	
	public void passLogin(String[] otherUsers) {
		Log.i("GameEngine", "in passLogin()");
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.LOGGED_IN);
		Log.i("GameEngine", ""+msg);
		msg.obj = otherUsers;
		msg.sendToTarget();
	}
	
	// initializes the game, and the informs the UI
	// the player id's are with respect to the initial X values, 
	// and then Y values to break a tie
	public void passEnterArena(Coordinate[] starts, int[] dirs, int myId) {
		Log.i("GameEngine", ""+starts.length);
		for (int i = 0; i < starts.length; i++) {
				Player p = new Player(starts[i], dirs[i]);
				players.put(i, p);
		}
		this.myId = myId;
		Log.i("GameEngine", "myId: " + myId);
		Message msg = handler.obtainMessage(Arena.IN_ARENA);
		msg.sendToTarget();
	}

	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public Coordinate update() {
		//Log.i("GameEngine", "visited: " + visitedMap.size());
		for (Integer i : players.keySet()) {
			Player player = players.get(i);
			if (!player.hasStopped()) {
				player.stepForward();
				Coordinate current = player.currentPoint();
				ArrayList<Integer> yList = visitedMap.get(current.x);
				if (yList == null) {
					yList = new ArrayList<Integer>();
				}
				if (player.getId() == myId && yList.contains(current.y)) {
					Log.i("GameEngine", "crash!");
					return current; // collision
				}
				else {
					yList.add(current.y);
				}
				if ((player.getId() == myId) && (current.x > 100 || current.y > 100 
						|| current.x < 0 || current.y < 0)) {
					return current; // off the edge
				}
				visitedMap.put(current.x, yList);
			}
			players.put(i, player);
		}
		numTics++;
		return null;
	}

	public Iterable<Player> getPlayers() {
		return players.values();
	}

	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public void turn(boolean isLeft) {
		Player player = players.get(myId);
		player.turn(isLeft, numTics);
		players.put(myId, player);
		/*
		if (myId == -1) return;
		Player player = players.get(myId);
		Log.i("player id", ""+player.getId());
		int direction = 0;
		if (isLeft) direction = -1;
		else direction = 1;
		Log.i("old player direction", ""+player.getDirection());
		int newDirection = (player.getDirection() + direction)%4;
		if (newDirection == -1) newDirection = 3; // stupid mod op in java
		player.setDirection(newDirection);
		Log.i("new player direction", ""+player.getDirection());*/
	}

	public Iterable<Player> getTrails() {
		return players.values();
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void startGame() {
		Message msg = handler.obtainMessage(Arena.PLAYING);
		msg.sendToTarget();
		Thread.yield();
	}
	
	public void endGame() {
		Message msg = handler.obtainMessage(Arena.IN_LOBBY);
		msg.sendToTarget();
	}

	// TODO: Include a "WIN" condition
	public void gameResult(int playerId, boolean isWin) {
		for (Player player : players.values()) {
			Log.i("GameEngine", "player " + player.getId() + "loc: " + player.currentPoint());
			if (player.getId() == playerId) {
				player.stop();
			}
		}
		if (playerId == myId) {
			Log.i("GameEngine", "id was equal!");
			if (isWin) {
				Message msg = handler.obtainMessage(Arena.WIN);
				msg.sendToTarget();
			}
			else {
				Message msg = handler.obtainMessage(Arena.LOSE);
				msg.sendToTarget();
			}
		}
	}

	@Override
	public void opponentTurn(int playerId, int time, boolean isLeft) {
		Log.i("GameEngine", "time: " + time);
		Log.i("GameEngine", "numTics: " + numTics);
		Player player = players.get(playerId);
		player.turn(isLeft, time);
		players.put(playerId, player);
		int diff = numTics - time;
		for (Integer i : players.keySet()) {
			Player p = players.get(i);
			p.stepBackward(diff);
			players.put(i, p);
		}
		numTics -= diff;
		for (int i = 0; i < diff; i++) {
			update();
		}
		//player.stepBackward(numTics - time);
		/*int count = 0;
		Player player = null;
		for (Player p : players) {
			if (p.getId() == playerId) {player = p; break;}
		}
		while (count < (numTics - time - 1)) {
			player.stepBackward(1);
			count++;
		}
		int direction = 0;
		if (isLeft) direction = -1;
		else direction = 1;
		int newDirection = (player.getDirection() + direction)%4;
		if (newDirection == -1) newDirection = 3; // stupid mod op in java
		player.setDirection(newDirection);
		for (int i = 0; i < count; i++) {
			player.stepForward(1);
		}*/
	}
}
