package princeTron.Engine;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.Handler;
import android.graphics.Point;

import android.util.Log;

public class GameEngineThread extends Thread implements Parcelable {
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		
	}
	
	private GameEngine engine;
	private princeTron.Engine.GameNetwork network;
	
	private boolean toRun = true;
	
	public GameEngineThread (Handler handler) {
		network = new princeTron.Network.NetworkIP();
		engine = new GameEngine(handler);
		Log.i("GET", "setting game engine");
		network.setGameEngine(engine);
		Log.i("GET", "game engine set");
	}
	
	// Just keeps sleeping. Probably not the right way to do this
	@Override
	public void run() {
		if (((princeTron.Network.NetworkIP) network).clientIsNull()) {
			// this is for testing purposes
			Log.i("G.E.T.", "about to start game");
			engine.startGame(5, 2);
		}
		while (toRun) {
			/*try {
				sleep(10000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public synchronized void setArenaView(princeTron.UserInterface.ArenaView arena) {
		engine.setArenaView(arena);
	}
	
	public synchronized void turn(boolean isLeft) {
		Point p = engine.turn(isLeft);
		int time = engine.numTics;
		network.userTurn(p, time, isLeft);
	}
	
	public void cancel() {
		toRun = false;
		interrupt();
	}
	
	public boolean update() {
		return engine.update();
	}
	
	public Iterable<Player> getPlayers() {
		return engine.getPlayers();
	}
	
	public boolean isReady() {
		return engine.isReady();
	}

}
