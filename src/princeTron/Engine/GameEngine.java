package princeTron.Engine;

import java.util.ArrayList;
import android.graphics.Point;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private ArrayList<Player> players;
	public int X_SCALE = 100;
	public int Y_SCALE = 100;
	int NORTH = 0;
	int EAST = 1;
	int SOUTH = 2;
	int WEST = 3;
	
	public GameEngine() {
		players = new ArrayList<Player>();
	}
	
	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public void turn(boolean isLeft) {
		Player player = players.get(0);
		int direction = 0;
		if (isLeft) direction = -1;
		else direction = 1;
		player.setDirection((player.getDirection() + direction)%4);
	}
	
	public ArrayList<Player> getTrails() {
		return players;
	}
	
	@Override
	public void startGame(int countdown, int numPlayers) {
		//should initialize based on screen height and width
				if(numPlayers == 1){
					Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), SOUTH);
					players.add(player);
				}

				else if(numPlayers == 2){
					Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), SOUTH);
					players.add(player);

					player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.9*Y_SCALE)), NORTH);
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
				}
	}
	
	@Override
	public void gameOver(boolean isWin) {
		
	}
	
	@Override
	public void opponentTurn(int player_id, Point position, int time, boolean isLeft) {
		
	}
}
