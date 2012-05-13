package princeTron.Engine;

// THIS CLASS HAS BEEN REMOVED FROM USAGE

public class GameEngineThread extends Thread{
	
	/*public int describeContents() {
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
		while (toRun) {
			try {
				sleep(5);
			}
			catch (Exception e) {
				
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
		
	}
	
	public void turn(boolean isLeft) {
		Log.i("GameEngineThread", "in turn()");
		int time = engine.numTics;
		engine.turn(isLeft);
		network.userTurn(time, isLeft);
		Log.i("GameEngineThread", "ending turn()");
	}
	
	public void cancel() {
		toRun = false;
		interrupt();
	}
	
	public synchronized void update() {
		//Log.i("GameEngineThread", "in update()");
	}
	
	public synchronized ArrayList<Player> getPlayers() {
		//Log.i("GET", "getting players!");
		try {
			return (ArrayList<Player>) engine.getPlayers().clone();
		}
		catch (Exception e) {
			return engine.getPlayers();
		}
		return null;
	}
	
	public synchronized boolean isReady() {
		return engine.isReady();
	}
	
	public synchronized void disconnect() {
		network.disconnect();
	}*/

}
