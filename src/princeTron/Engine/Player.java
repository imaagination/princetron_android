package princeTron.Engine;

import java.util.ArrayList;
import android.util.Log;

//import android.util.Log;

public class Player {

	//a list of Coordinates that define a players trail
	private ArrayList<Coordinate> playerTrail = new ArrayList<Coordinate>();
	private Coordinate lastPoint = new Coordinate(0, 0);
	private int direction;
	private int id;
	public static int static_id = 0;
	private boolean hasStopped = false;

	public Player(Coordinate coord, int direction){
		playerTrail.add(coord);
		lastPoint = coord;
		this.direction = direction;
		id = static_id;
		static_id++;
		//this.nextDirection = direction;
	}
	
	public void stepForward(int numSteps) {
		for (int i = 0; i < numSteps; i++) {
			stepForward();
		}
	}
	
	// numSteps will likely always be 1, except in the case that a "turn"
	// message is received, in which case the player is "rewound", and then
	// "fast-forwarded" the difference between the time stamp and current time
	public void stepForward() {
		Coordinate newPoint = new Coordinate(lastPoint.x, lastPoint.y);
		switch (direction) {
		case GameEngine.NORTH:
			newPoint = new Coordinate(lastPoint.x, lastPoint.y + 1);
			break;
		case GameEngine.EAST:
			newPoint = new Coordinate(lastPoint.x + 1, lastPoint.y);
			break;
		case GameEngine.SOUTH:
			newPoint = new Coordinate(lastPoint.x, lastPoint.y - 1);
			break;
		case GameEngine.WEST:
			newPoint = new Coordinate(lastPoint.x - 1, lastPoint.y);
			break;
		default:
			Log.i("Player", "No direction!");
		}
		playerTrail.add(newPoint);
		lastPoint = newPoint;
	}
	
	// if a playerTurned message arrives in the past, we rewind the player
	public void stepBackward(long numSteps) {
		for (int i = 0; i < numSteps; i++) {
			playerTrail.remove(playerTrail.size() - 1);
		}
	}

	public int getDirection(){
		return this.direction;
	}

	public void setDirection(int direction){
		this.direction = direction;
	}

	public int getSize(){
		return playerTrail.size();
	}
	
	public int getId() {
		return id;
	}
	
	public Coordinate currentPoint() {
		return lastPoint;
	}
	
	public Iterable<Coordinate> getPoints() {
		return playerTrail;
	}
	
	public void stop() {
		hasStopped = true;
	}
	
	public boolean hasStopped() {
		return hasStopped;
	}
}

