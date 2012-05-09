package princeTron.Engine;

import java.util.HashMap;

import princeTron.Network.NetworkIP;
import princeTron.UserInterface.Arena;
import princeTron.UserInterface.ArenaView;
import android.util.Log;

public class GameEngine {
    private ArenaView arenaView;
    private Arena arena;
    private NetworkIP net;
    private GameEngineThread geThread;
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

    public GameEngine(Arena arena, NetworkIP net) {
    	this.arena = arena;
    	this.net = net;
    	net.setGameEngine(this);
    	resetGameboard();
    }
    
    public void resetGameboard() {
		gameboard = new int[X_SCALE][Y_SCALE];
		for (int i = 0; i < X_SCALE; i++) {
		    for (int j = 0; j < Y_SCALE; j++) {
			gameboard[i][j] = -1;
		    }
		}
    	if (arenaView != null) arenaView.setGameboard(gameboard);
    }
    
    public void setNetwork(NetworkIP net) {
    	this.net = net;
    }
    
    public void setArenaView(ArenaView arenaView) {
    	this.arenaView = arenaView;
    	if (arenaView != null) arenaView.setGameboard(gameboard);
    }
    
    public synchronized void advance(int goalStep) {
    	while (timestep < goalStep) {
    		stepForward(timestep);
    		timestep++;
    	}
    	// Hack due to double buffering
    	//arenaView.drawBoard();
    	//arenaView.drawBoard();
    }

    public void stepForward(int time)
    {	
		Log.d("GameEngine", "Stepping forward at time " + time + " (" + players[0].x + ", " + players[0].y + ")");
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			
		    if (!player.active)
			continue;
		    
		    switch (player.dir) {
			    case NORTH : player.y++; break;
			    case SOUTH : player.y--; break;
			    case EAST : player.x++; break;
			    case WEST : player.x--; break;
		    }
	
		    if (playerTurns[i].containsKey(time)) {
			    turn(player, playerTurns[i].get(time));
		    }
		    
		    if (player.id == myId) {
				if (player.x < 0 || player.x >= X_SCALE || player.y < 0 ||
				    player.y >= Y_SCALE || gameboard[player.x][player.y] != -1) {
					    player.active = false;
					    net.sendCollision(timestep);
				}
		    }
		    
		    if (player.x >= 0 && player.x < X_SCALE && player.y >= 0 && 
		    		player.y < Y_SCALE) {
		    	gameboard[player.x][player.y] = i;
		    	// Hack because I can't figure out double buffering of SurfaceView
		    	arenaView.fillSquare(player.x, player.y, i);
		    	arenaView.fillSquare(player.x, player.y, i);
		    } 
		}
    }
    
    public void stepBack(int time) {
    	Log.d("GameEngine", "Stepping backward at time " + time + " (" + players[0].x + ", " + players[0].y + ")");
    	
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			
		    if (!player.active)	continue;
		    
		    // Unturn players
		    if (playerTurns[i].containsKey(time)) {
			    unTurn(player, playerTurns[i].get(time));
		    }
		    
		    // Clear game board
		    if (player.x >= 0 && player.x < X_SCALE && player.y >= 0 && 
		    		player.y < Y_SCALE) {
		    	gameboard[player.x][player.y] = -1;
		    } 
		    
		    // Hack due to double buffering
		    arenaView.fillSquare(player.x, player.y, -1);
		    arenaView.fillSquare(player.x, player.y, -1);
		    
		    switch (player.dir) {
			    case NORTH : player.y--; break;
			    case SOUTH : player.y++; break;
			    case EAST : player.x--; break;
			    case WEST : player.x++; break;
		    }
		}
    }
    
    // called by the UI when the player turns. 
    public synchronized void turn(boolean isLeft) {
    	turn(players[myId], isLeft);
    	net.sendTurn(timestep, isLeft);
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
    
    // Callbacks from network
    public void enterArena(int[] playersX, int[] playersY, int[] playersDir, int myId) {
		this.myId = myId;
		players = new Player[playersX.length];
		playerTurns = new HashMap[playersX.length];
		
		for (int i = 0; i < playersX.length; i++) {
		    players[i] = new Player(playersX[i], playersY[i], playersDir[i], i);
		    playerTurns[i] = new HashMap<Integer, Boolean>();
	    }
		
		arenaView.status = ArenaView.READY;
    	// Hack due to double buffering of SurfaceView
    	arenaView.drawBoard();
    	arenaView.drawBoard();
    	geThread = new GameEngineThread(this);
    	geThread.start();
    }
    
    public void logIn(String user) {
    	net.logIn(user);
    }
    
    public void newLobby(String[] users) {
    	//arena.newLobby(users);
    }
    
    public void sendInvites(String[] invites) {
    	net.sendInvites(invites);
    }
    
    public void invitationReceived(String user) {
    	
    }
    
    public void startGame() {
    	arenaView.status = ArenaView.PLAYING;
    	geThread.callBack();
    }
	
    public void endGame() {
    	geThread.stopExecution();
    	try{geThread.join();}catch(Exception e){}
    	//arena.finish();
    }
    
    public void gameResult(int playerId, boolean isWin) {
    }
    
	public synchronized void opponentTurn(int playerId, int time, boolean isLeft) {
		int curTime = timestep;
		for (int i = 0; i < curTime + 1 - time; i++) {
			stepBack(curTime - i - 1);
		}
		
		playerTurns[playerId].put(time, isLeft);
		
		for (int i = 0; i < curTime + 1 - time; i++) {
		    stepForward(time + i - 1);
		}
		
		// Hack due to double buffering
		//arenaView.drawBoard();
		//arenaView.drawBoard();
    }
}
