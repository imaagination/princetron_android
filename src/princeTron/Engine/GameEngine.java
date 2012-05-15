package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import princeTron.Network.NetworkIP;
import princeTron.UserInterface.Arena;

import android.os.Handler;
import android.os.Message;

import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	
	private static final int TRUE = 1;
	private static final int FALSE = 0;
	// array of players' turns. Indexed by player id
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	// number of tics since game started
	public Integer numTics = 0;
	private boolean isReady = false;
	private int myId = -1;
	// for collision detection
	private HashSet<Coordinate> visited;
	// to handle backing up on turns
	private HashSet<Coordinate> doubleVisited;
	private NetworkIP network;
	private Handler handler;

	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long mMoveDelay = 100;

	public GameEngine(Handler handler, NetworkIP network) {
		this.handler = handler;
		this.network = network;
		visited = new HashSet<Coordinate>();
		players = new HashMap<Integer, Player>();
		numTics = 0;
		myId = -1;
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
	
	public void passLobbyUpdate(String name, boolean hasEntered) {
		Log.i("GameEngine", "passing update");
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.LOBBY_UPDATE);
		msg.obj = name;
		if (hasEntered) msg.arg1 = TRUE;
		else msg.arg1 = FALSE;
		msg.sendToTarget();
	}
	
	// initializes the game, and the informs the UI
	// the player id's are with respect to the initial X values, 
	// and then Y values to break a tie
	public void passEnterArena(Coordinate[] starts, int[] dirs, String[] names, int myId) {
		visited = new HashSet<Coordinate>();
		doubleVisited = new HashSet<Coordinate>();
		players = new HashMap<Integer, Player>();
		Log.i("GameEngine", ""+starts.length);
		for (int i = 0; i < starts.length; i++) {
				Player p = new Player(starts[i], dirs[i], i, names[i]);
				players.put(i, p);
		}
		this.myId = myId;
		Log.i("GameEngine", "myId: " + myId);
		Message msg = handler.obtainMessage(Arena.IN_ARENA);
		msg.arg1 = myId;
		msg.sendToTarget();
		Thread.yield();
	}

	// used to send a crash message when the user leaves in middle of a  game
	// doesn't seem fair that cutting off the connection guarantees victory...
	public Coordinate getMyLocation() {
		return players.get(myId).currentPoint();
	}
	
	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public void update(boolean toReturn) {
		for (Integer i : players.keySet()) {
			Player player = players.get(i);
			if (!player.hasStopped() && !player.hasLost && !player.hasWon) {
				player.stepForward();
				Coordinate current = player.currentPoint();
				if (toReturn && visited.contains(current)) {
					doubleVisited.add(current);
					if (player.getId() == myId) {
						Log.i("GameEngine", "crash!");
						network.userCrash(current, player.numTics);
						player.stop();
					}
					else {
						player.stop();
					}
				}
				else if (current.x >= 100 || current.x <= 0 || current.y >= 100 || current.y <= 0) {
					if (player.getId() == myId) {
						Log.i("GameEngine", "crash!");
						network.userCrash(current, player.numTics);
						player.stop();
					}
				}
				visited.add(current);
			}
			players.put(i, player);
		}
		numTics++;
	}

	public Iterable<Player> getPlayers() {
		return players.values();
	}

	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public void turn(boolean isLeft) {
		Log.i("GameEngine", "turning in gameEngine");
		Player player = players.get(myId);
		player.turn(isLeft, numTics);
		players.put(myId, player);
		network.userTurn(player.numTics, isLeft);
		Log.i("GameEngine", "finishing turn in gameEngine");
	}
	
	@Override
	public void opponentTurn(int playerId, int time, boolean isLeft) {
		int oldNumTics = numTics;
		if (time > numTics) {
			Player player = players.get(playerId);
			player.turn(isLeft, time);
			players.put(playerId, player);
		}
		for (Integer i : players.keySet()) {
			Player p = players.get(i);
			if (p.hasLost || p.hasWon) continue; 
			p.start();
			while (p.numTics >= time) {
				ArrayList<Coordinate> removed = p.stepBackward(1);
				for (Coordinate c : removed) {
					visited.remove(c);
				}
			}
			players.put(i, p);
		}
		for (Coordinate c : doubleVisited) {
			visited.add(c);
		}
		doubleVisited = new HashSet<Coordinate>();
		Player player = players.get(playerId);
		player.turn(isLeft, time);
		players.put(playerId, player);
		numTics = time;
		while (numTics < oldNumTics) {
			update(true);
		}
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
		String report = "";
		for (Player p : players.values()) {
			if (!p.hasLost) {
				report += p.name + " wins! :)\n";
			}
			else {
				report += p.name + " loses :(\n";
			}
		}
		msg.obj = report;
		msg.sendToTarget();
	}

	public void gameResult(int playerId, boolean isWin) {
		for (Player player : players.values()) {
			Log.i("GameEngine", "player " + player.getId() + "loc: " + player.currentPoint());
			if (player.getId() == playerId) {
				if (!isWin) {
					player.hasLost = true;
				}
				else {
					player.hasWon = true;
				}
				player.stop();
				Message msg = handler.obtainMessage(Arena.PLAYER_CRASH);
				msg.obj = player.name;
				msg.sendToTarget();
			}
		}
	}
}
