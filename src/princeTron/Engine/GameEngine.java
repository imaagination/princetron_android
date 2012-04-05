package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import princeTron.UserInterface.ArenaView;
import android.graphics.Point;

import android.os.Handler;

import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private ArrayList<Player> players;
	// ArenaView, to be updated
	private ArenaView arenaView;
	// number of tics since game started
	private int numTics = 0;
	private boolean isReady = false;
	// for collision detection
	private HashMap<Integer, HashSet<Integer>> visited = new HashMap<Integer, HashSet<Integer>>();
	
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
	
	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public boolean update() {
		for (Player player : players) {
			player.stepForward(1);
			Point current = player.currentPoint();
			// this is a terrible collision detection algorithm
			if (visited.containsKey(current.x)) {
				Log.i("visited contains", "true");
				HashSet<Integer> yvals = visited.get(current.x);
				if (yvals.contains(current.y)) {
					Log.w("returning", "TRUE!");
					return true;
				}
				yvals.add(current.y);
				visited.put(current.x, yvals);
			}
			else {
				HashSet<Integer> yvals = new HashSet<Integer>();
				yvals.add(current.y);
				visited.put(current.x, yvals);
			}
		}
		numTics++;
		
		return false; // for now!
	}
	
	public Iterable<Player> getPlayers() {
		return players;
	}

	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public void turn(boolean isLeft) {
		Player player = players.get(0);
		Log.i("player id", ""+player.getId());
		int direction = 0;
		if (isLeft) direction = 1;
		else direction = -1;
		Log.i("old player direction", ""+player.getDirection());
		Log.i("", ""+(-1%4));
		int newDirection = (player.getDirection() + direction)%4;
		if (newDirection == -1) newDirection = 3; // stupid mod op in java
		player.setDirection(newDirection);
		Log.i("new player direction", ""+player.getDirection());
	}

	public ArrayList<Player> getTrails() {
		return players;
	}
	
	public boolean isReady() {
		return isReady;
	}

	@Override
	public void startGame(int countdown, int numPlayers) {
		try {
			//should initialize based on screen height and width
			if(numPlayers == 1){
				Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), SOUTH);
				players.add(player);
			}

			else if(numPlayers == 2){
				Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), NORTH);
				players.add(player);

				player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.9*Y_SCALE)), SOUTH);
				players.add(player);
			}

			else if(numPlayers == 3){
				Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), SOUTH);
				players.add(player);

				player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.9*Y_SCALE)), NORTH);
				players.add(player);

				player = new Player(new Point((int) (0.1*X_SCALE), (int) (0.5*Y_SCALE)), SOUTH);
				players.add(player);
			}

			else if(numPlayers == 4){
				Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), SOUTH);
				players.add(player);

				player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.9*Y_SCALE)), NORTH);
				players.add(player);

				player = new Player(new Point((int) (0.1*X_SCALE), (int) (0.5*Y_SCALE)), WEST);
				players.add(player);

				player = new Player(new Point((int) (0.9*X_SCALE), (int) (0.5*Y_SCALE)), NORTH);
				players.add(player);
				Log.i("GameEngine 96", ""+player.getId());
			}
			if (GameEngine.this.arenaView == null) {
				System.out.println("null!");
			}
			handler.sendEmptyMessage(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: Include a "WIN" condition
	@Override
	public void gameOver(boolean isWin) {
		arenaView.setMode(ArenaView.LOSE);
	}

	@Override
	public void opponentTurn(int playerId, Point position, int time, boolean isLeft) {
		for (Player player : players) {
			if (player.getId() == playerId) {
				player.stepBackward(numTics - time);
				int direction = 0;
				if (isLeft) direction = -1;
				else direction = 1;
				player.setDirection((player.getDirection() + direction)%4);
				player.stepForward(numTics - time);
			}
		}
	}
}
