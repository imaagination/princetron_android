package princeTron.Engine;

import android.os.Parcelable;
import android.os.Parcel;
import android.os.Handler;

import android.util.Log;

import java.util.ArrayList;
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
	private boolean toUpdate = false;
	
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
				sleep(5);
				if (toUpdate) {
					Coordinate c = engine.update(true);
					if (c != null) {
						network.userCrash(c, engine.numTics);
					}
					else if (p != null) {
						network.userCrash(p, time);
						p = null;
					}
					toUpdate = false;
				}
			}
			catch (Exception e) {
				if (toUpdate) {
					Coordinate c = engine.update(true);
					if (c != null) {
						network.userCrash(c, engine.numTics);
					}
					toUpdate = false;
				}
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
	
	public synchronized void userCrash(Coordinate p, int time) {
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
		toUpdate = true;
	}
	
	public synchronized ArrayList<Player> getPlayers() {
		//Log.i("GET", "getting players!");
		try {
			return (ArrayList<Player>) engine.getPlayers().clone();
		}
		catch (Exception e) {
			return engine.getPlayers();
		}
	}
	
	public synchronized boolean isReady() {
		return engine.isReady();
	}
	
	public synchronized void disconnect() {
		network.disconnect();
	}

}
