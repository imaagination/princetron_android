package princeTron.Engine;

import java.util.ArrayList;

import princeTron.UserInterface.Arena;
import princeTron.UserInterface.ArenaView;
import android.os.Message;
import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	private ArenaView arenaView;
	private int timestep;
	private int[][] gameboard;
	private Player[] players;
	private int myId;

	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long INTERVAL = 100;

	public GameEngine(int[] playersX, int[] playersY, int[] playersDir, int myId) {
		this.myId = myId;
		players = new Player[playersX.length];
		for (int i = 0; i < playersX.length; i++) {
			players[i] = new Player(playersX[i], playersY[i], playersDir[i]);
		}
		gameboard = new int[X_SCALE][Y_SCALE];
		for (int i = 0; i < X_SCALE; i++) {
			for (int j = 0; j < Y_SCALE; j++) {
				gameboard[i][j] = -1;
			}
		}
	}

	public void setArenaView(ArenaView arena) {
		arenaView = arena;
	}

	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public Coordinate update() {
		//Log.i("GameEngine", "visited: " + visitedMap.size());
		for (Player player : players) {
			if (!player.hasStopped()) {
				player.stepForward(1);
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
		if (isLeft) direction = -1;
		else direction = 1;
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
		Thread.yield();
	}
	
	public void endGame() {
		Message msg = handler.obtainMessage(Arena.IN_LOBBY);
		msg.sendToTarget();
	}

	// TODO: Include a "WIN" condition
	public void gameResult(int playerId, boolean isWin) {
		for (Player player : players) {
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
		//player.stepBackward(numTics - time);
		int count = 0;
		Player player = null;
		for (Player p : players) {
			if (p.getId() == playerId) {player = p; break;}
		}
		while (count <= (numTics - time)) {
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
		}
	}
}
