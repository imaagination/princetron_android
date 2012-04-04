package princeTron.Engine;

import java.util.ArrayList;

import princeTron.UserInterface.ArenaView;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	// array of players' turns. Indexed by player id
	private ArrayList<Player> players;
	// ArenaView, to be updated
	private ArenaView arenaView;
	// number of tics since game started
	private int numTics = 0;
	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long mMoveDelay = 100;

	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// advances all of the players
			for (Player player : GameEngine.this.players) {
				player.stepForward(1);
			}
			// updates the UI
			GameEngine.this.arenaView.update(players);
			GameEngine.this.arenaView.invalidate();
			GameEngine.this.numTics++;
			sleep(GameEngine.mMoveDelay);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

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
			Log.i("GameEngine 96", ""+player.getId());
		}
		GameEngine.this.arenaView.update(players);
		GameEngine.this.arenaView.invalidate();
		mRedrawHandler.sleep(countdown*1000);
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
