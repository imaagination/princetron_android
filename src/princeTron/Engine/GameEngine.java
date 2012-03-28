package princeTron.Engine;

import java.util.ArrayList;
import android.graphics.Point;
//import java.awt.Point;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private ArrayList<Point>[] playerTurns;
	
	public GameEngine() {
		
	}
	
	@Override
	public void startGame(int countdown) {
		
	}
	
	@Override
	public void gameOver(boolean isWin) {
		
	}
	
	@Override
	public void opponentTurn(int player_id, Point position, int time, boolean isLeft) {
		ArrayList<Point> thisPlayerTurns = playerTurns[player_id];
		
	}
}
