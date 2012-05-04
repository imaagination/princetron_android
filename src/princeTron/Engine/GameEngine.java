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
    private HashMap<Integer, Boolean>[] playerTurns;
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
	    players[i] = new Player(playersX[i], playersY[i], playersDir[i], i);
	    playerTurns[i] = new HashMap<Integer, Boolean>();
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

	
    public void update() {
	numTics++;
	stepForward();
	
    }

    public void stepForward(int time)
    {
	
	for (Player player : players) {
	    if (!player.active)
		continue;
	    
	    switch (player.direction()) {
	    case NORTH : players.y++; break;
	    case SOUTH : players.y--; break;
	    case EAST : players.x++; break;
	    case WEST : players.x--; break;
	    }

	    if (playerTurns[i].contains(time)) {
		    turnPlayer(player, playerTurns[i].get(time));
	    }
	   
	    if (player.id == myId) {
		if (player.x < 0 || player.x >= X_SCALE || player.y < 0 ||
		    player.y >= Y_SCALE || gameboard[player.x][player.y] != -1) {
		    player.active = False;
		    sendCollision();
		}
	    }

	    if (player.x >= 0 && player.x < X_SCALE && player.y >= 0 && 
		player.y < Y_SCALE) {
		gameboard[player.x][player.y] = i;
	    } 
	}
    }

    public Iterable<Player> getPlayers() {
	return players;
    }
    
    // called by the UI when the player turns. 
    public void turn(boolean isLeft) {
	turn(players[myId], isLeft);
    }
    
    public void turn(Player player, boolean isLeft) {
	if (isLeft) {
	    switch (player.dir) {
	    case NORTH: player.dir = WEST; break;
	    case SOUTH: player.dir = EAST; break;
	    case EAST: player.dir = NORTH; break;
	    case WEST: player.dir = SOUTH; break;
	    }
	}
	else {
	    switch (player.dir) {
	    case NORTH: player.dir = EAST; break;
	    case SOUTH: player.dir = WEST; break;
	    case EAST: player.dir = SOUTH; break;
	    case WEST: player.dir = NORTH; break;
	    } 
	}
    }

    public void unTurn(Player player, boolean isLeft) {
	if (isLeft) {
	    switch (player.dir) {
	    case NORTH: player.dir = EAST; break;
	    case SOUTH: player.dir = WEST; break;
	    case EAST: player.dir = SOUTH; break;
	    case WEST: player.dir = NORTH; break;
	    }
	}
	else {
	    switch (player.dir) {
	    case NORTH: player.dir = WEST; break;
	    case SOUTH: player.dir = EAST; break;
	    case EAST: player.dir = NORTH; break;
	    case WEST: player.dir = SOUTH; break;
	    }
	}
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
    
    
    @Override
	public void opponentTurn(int playerId, int time, boolean isLeft) {
	currentTime = numTics;
	for (int i = 0; i < currentTime + 1 - time; i++)
	    stepBack(currentTime - i);
	
	player_turns[playerId].add(time, isLeft);
	
	for (int i = 0; i < currentTime + 1 - time; i++)
	    stepForward(time + i);
    }
}
