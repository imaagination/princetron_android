package princeTron.Engine;

import android.os.SystemClock;
import android.util.Log;

public class GameEngineThread extends Thread {
    private GameEngine engine;
    
    private long startTime;
    
    private boolean isRunning;
    private boolean callingBack;
	
    public GameEngineThread(GameEngine engine) {
    	this.engine = engine;
    	this.isRunning = true;
    	this.callingBack = false;
    }
    
    public void stopExecution() {
    	isRunning = false;
    }
    
    public void callBack() {
    	startTime = SystemClock.uptimeMillis();
    	callingBack = true;
    }
	
    // Just keeps sleeping. Probably not the right way to do this
    @Override
	public void run() {
    	while (isRunning) {
    		if (callingBack) {
	    		long curTime = SystemClock.uptimeMillis();
	    		//Log.d("GameEngineThread", "Starting loop at " + curTime);
	    		long elapsedTime = curTime - startTime;
	    		int steps = ((int)elapsedTime) / 100;
	    		engine.advance(steps);
    		}
    		try{Thread.sleep(40);}catch(Exception e){}
    	}
    }   
}
