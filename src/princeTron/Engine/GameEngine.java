package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashSet;

import princeTron.UserInterface.Arena;
import princeTron.UserInterface.ArenaView;

import android.os.Handler;
import android.os.Vibrator;
import android.os.Message;

import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private ArrayList<Player> players;
	// ArenaView, to be updated
	private ArenaView arenaView;
	// number of tics since game started
	public int numTics = 0;
	private boolean isReady = false;
	private int myId = -1;
	// for collision detection
	private HashSet<Coordinate> visited = new HashSet<Coordinate>();
	private Vibrator vibe;

	private Handler handler;

	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long mMoveDelay = 100;

	public GameEngine(Handler handler) {
		players = new ArrayList<Player>();
		this.handler = handler;
	}

	public void setArenaView(princeTron.UserInterface.ArenaView arena) {
		arenaView = arena;
	}
	
	public void passInvitation(String username) {
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.INVITED);
		msg.obj = username;
		msg.sendToTarget();
	}
	
	// initializes the game, and the informs the UI
	// the player id's are with respect to the initial X values, 
	// and then Y values to break a tie
	public void passEnterArena(Coordinate[] starts, int[] dirs, int myId) {
		Log.i("GameEngine", ""+starts.length);
		for (int i = 0; i < starts.length; i++) {
				Player p = new Player(starts[i], dirs[i]);
				players.add(p);
		}
		this.myId = myId;
		Log.i("GameEngine", "myId: " + myId);
		Message msg = handler.obtainMessage(Arena.IN_ARENA);
		msg.sendToTarget();
	}

	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public Coordinate update() {
		for (Player player : players) {
			player.stepForward(1);
			Coordinate current = player.currentPoint();
			if (player.getId() == myId && visited.contains(current)) {
				return current; // collision
			}
			else if (player.getId() == myId && (current.x > 200 || current.y > 200 
					|| current.x < 0 || current.y < 0)) {
				return current; // off the edge
			}
			else {
				visited.add(current);
			}
		}
		numTics++;

		return null;
	}

	public Iterable<Player> getPlayers() {
		return players;
	}

	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public Coordinate turn(boolean isLeft) {
		if (myId == -1) return null;
		Player player = players.get(myId);
		Log.i("player id", ""+player.getId());
		int direction = 0;
		if (isLeft) direction = 1;
		else direction = -1;
		Log.i("old player direction", ""+player.getDirection());
		int newDirection = (player.getDirection() + direction)%4;
		if (newDirection == -1) newDirection = 3; // stupid mod op in java
		player.setDirection(newDirection);
		Log.i("new player direction", ""+player.getDirection());
		return player.currentPoint();
	}

	public ArrayList<Player> getTrails() {
		return players;
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void startGame() {
		Message msg = handler.obtainMessage(Arena.PLAYING);
		msg.sendToTarget();
	}
	
	public void endGame() {
		Message msg = handler.obtainMessage(Arena.IN_LOBBY);
		msg.sendToTarget();
	}

	// TODO: Include a "WIN" condition
	@Override
	public void gameOver(boolean isWin) {
		if (isWin) {
			arenaView.setMode(ArenaView.WIN);
		}
		else {
			vibe.vibrate(1000);
			for(int i = 0; i <1000; i++)
				continue;
			arenaView.setMode(ArenaView.LOSE);
		}
	}

	@Override
	public void opponentTurn(int playerId, Coordinate position, int time, boolean isLeft) {
		for (Player player : players) {
			if (player.getId() == playerId) {
				player.stepBackward(numTics - time);
				int direction = 0;
				if (isLeft) direction = -1;
				else direction = 1;
				int newDirection = (player.getDirection() + direction)%4;
				if (newDirection == -1) newDirection = 3; // stupid mod op in java
				player.setDirection(newDirection);
				player.stepForward(numTics - time);
			}
		}
	}
}
