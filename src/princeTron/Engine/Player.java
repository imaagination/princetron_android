package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;

//import android.util.Log;

public class Player {

	//a list of Coordinates that define a players trail
	private ArrayList<Coordinate> playerTrail = new ArrayList<Coordinate>();
	private Coordinate lastPoint = new Coordinate(0, 0);
	private int direction;
	private int id;
	private int numTics;
	public static int static_id = 0;
	private boolean hasStopped = false;
	private HashMap<Integer, Boolean> turns = new HashMap<Integer, Boolean>();
	
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
	
	public void turn(boolean isLeft, int numTics) {
		turns.put(numTics, isLeft);
	}
	
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
		Log.i("Player", ""+direction);
		if (turns.containsKey(numTics)) {
			boolean isLeft = turns.get(numTics);
			switch (direction) {
			case GameEngine.NORTH:
				if (isLeft) {
					Log.i("Player", "NORTH to WEST");
					direction = GameEngine.WEST;
				}
				else {
					Log.i("Player", "NORTH to EAST");
					direction = GameEngine.EAST;
				}
				break;
			case GameEngine.EAST:
				if (isLeft) {
					Log.i("Player", "EAST to NORTH");
					direction = GameEngine.NORTH;
				}
				else {
					Log.i("Player", "EAST to SOUTH");
					direction = GameEngine.SOUTH;
				}
				break;
			case GameEngine.SOUTH:
				if (isLeft) {
					Log.i("Player", "SOUTH to EAST");
					direction = GameEngine.EAST;
				}
				else {
					Log.i("Player", "SOUTH to WEST");
					direction = GameEngine.WEST;
				}
				break;
			case GameEngine.WEST:
				if (isLeft) {
					Log.i("Player", "WEST to SOUTH");
					direction = GameEngine.SOUTH;
				}
				else {
					Log.i("Player", "WEST to NORTH");
					direction = GameEngine.NORTH;
				}
			}
			Log.i("Player", ""+direction);
		}
		numTics++;
	}
	//account
	
	// if a playerTurned message arrives in the past, we rewind the player
	public void stepBackward(int numSteps) {
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

