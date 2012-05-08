package princeTron.Engine;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.Handler;

import android.util.Log;
import java.util.Collection;

public class GameEngineThread extends Thread implements Parcelable {
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel out, int flags) {
		
	}
	
	private GameEngine engine;
	private princeTron.Engine.GameNetwork network;
	
	private Coordinate p;
	private int time;
	
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
		while (toRun) {
			try {
				sleep(10);
				if (p != null) {
					Log.i("GameEngineThread", "crashing");
					network.userCrash(p, time);
					p = null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				p = null;
			}
		}
		System.out.println("GameEngineThread is ending");
	}
	
	public synchronized void readyToPlay(Collection<String> arg) {
		network.readyToPlay(arg);
	}
	
	public synchronized void acceptInvitation() {
		Log.i("GET", "accepting invitation");
		network.acceptInvitation();
	}
	
	public synchronized boolean logIn(String accountName) {
		return network.logIn(accountName);
	}
	
	private synchronized void userCrash(Coordinate p, int time) {
		this.p = p;
		this.time = time;
	}
	
	public synchronized void turn(boolean isLeft) {
		int time = engine.numTics;
		engine.turn(isLeft);
		network.userTurn(time, isLeft);
	}
	
	public void cancel() {
		toRun = false;
		interrupt();
	}
	
	public synchronized void update() {
		//Log.i("GameEngineThread", "in update()");
		Coordinate crashLoc = engine.update(true);
		if (crashLoc != null) {
			//Log.i("GET", "crashLoc wasn't null!");
			userCrash(crashLoc, engine.numTics);
		}
	}
	
	public synchronized Iterable<Player> getPlayers() {
		//Log.i("GET", "getting players!");
		return engine.getPlayers();
	}
	
	public synchronized boolean isReady() {
		return engine.isReady();
	}
	
	public synchronized void disconnect() {
		network.disconnect();
	}

}
