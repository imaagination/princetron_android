package princeTron.Engine;

import java.util.ArrayList;
import android.graphics.Point;

public class GameEngine extends princeTron.Network.GameNetwork {
	// array of players' turns. Indexed by player id
	private ArrayList<Player> players;
	public int X_SCALE = 100;
	public int Y_SCALE = 100;
	int NORTH = 1;
	int SOUTH = 2;
	int EAST = 3;
	int WEST = 4;
	
	public GameEngine() {
		players = new ArrayList<Player>();
	}
	
	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public void turn(boolean isLeft) {
		
	}
	
	public ArrayList<Player> getTrails() {
		
		return null;
	}
	
	@Override
	public void startGame(int countdown, int numPlayers) {
		//should initialize based on screen height and width
				if(numPlayers == 1){
					Player player = new Player(new Point((int) (0.5*X_SCALE), (int) (0.1*Y_SCALE)), EAST);
					players.add(player);
				}

				else if(numPlayers == 2){
					Player player = new Player(new Point(2, 20), EAST);
					players.add(player);

					player = new Player(new Point(40, 20), WEST);
					players.add(player);
				}

				else if(numPlayers == 3){
					Player player = new Player(new Point(2, 20), EAST);
					players.add(player);

					player = new Player(new Point(40, 20), WEST);
					players.add(player);

					player = new Player(new Point(19, 2), SOUTH);
					players.add(player);
				}

				else if(numPlayers == 4){
					Player player = new Player(new Point(2, 20), EAST);
					players.add(player);

					player = new Player(new Point(40, 20), WEST);
					players.add(player);

					player = new Player(new Point(19, 2), SOUTH);
					players.add(player);

					player = new Player(new Point(10, 40), NORTH);
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
