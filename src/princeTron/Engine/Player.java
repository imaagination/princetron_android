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
	public int numTics;
	public static int static_id = 0;
	private boolean hasStopped = false;
	private HashMap<Integer, Boolean> turns = new HashMap<Integer, Boolean>();
	
	public Player(Coordinate coord, int direction, int id){
		turns = new HashMap<Integer, Boolean>();
		playerTrail = new ArrayList<Coordinate>();
		playerTrail.add(coord);
		lastPoint = coord;
		this.direction = direction;
		this.id = id;
		//this.nextDirection = direction;
	}
	
	public void stepForward(int numSteps) {
		for (int i = 0; i < numSteps; i++) {
			stepForward();
		}
	}
	
	public void turn(boolean isLeft, int numTics) {
		if (turns.containsKey(numTics)) {
			turns.put(numTics+1, isLeft);
			return;
		}
		turns.put(numTics, isLeft);
	}
	
	public synchronized void stepForward() {
		//Log.i("Player", "Number of points = " + playerTrail.size());
		//Log.i("Player", ""+direction);
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
			//Log.i("Player", ""+direction);
		}
		Coordinate newPoint = new Coordinate(lastPoint.x, lastPoint.y);
		lastPoint = playerTrail.get(playerTrail.size() - 1);
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
		Log.i("Player " + id, ""+newPoint);
		playerTrail.add(newPoint);
		numTics++;
	}
	//account
	
	// if a playerTurned message arrives in the past, we rewind the player
	public synchronized ArrayList<Coordinate> stepBackward(int numSteps) {
		ArrayList<Coordinate> toReturn = new ArrayList<Coordinate>();
		for (int i = 0; i < numSteps; i++) {
			playerTrail.remove(playerTrail.size() - 1);
			lastPoint = playerTrail.get(playerTrail.size() - 1);
			Log.i("Player " + id, ""+lastPoint);
			toReturn.add(lastPoint);
			if (turns.containsKey(numTics)) {
				Log.i("Player", "turned back!");
				boolean isLeft = turns.get(numTics);
				switch (direction) {
				case GameEngine.NORTH:
					if (isLeft) {
						//Log.i("Player", "NORTH to WEST");
						direction = GameEngine.EAST;
					}
					else {
						//Log.i("Player", "NORTH to EAST");
						direction = GameEngine.WEST;
					}
					break;
				case GameEngine.EAST:
					if (isLeft) {
						//Log.i("Player", "EAST to NORTH");
						direction = GameEngine.SOUTH;
					}
					else {
						//Log.i("Player", "EAST to SOUTH");
						direction = GameEngine.NORTH;
					}
					break;
				case GameEngine.SOUTH:
					if (isLeft) {
						//Log.i("Player", "SOUTH to EAST");
						direction = GameEngine.WEST;
					}
					else {
						//Log.i("Player", "SOUTH to WEST");
						direction = GameEngine.EAST;
					}
					break;
				case GameEngine.WEST:
					if (isLeft) {
						//Log.i("Player", "WEST to SOUTH");
						direction = GameEngine.NORTH;
					}
					else {
						//Log.i("Player", "WEST to NORTH");
						direction = GameEngine.SOUTH;
					}
				}
			}
			numTics--;
		}
		return toReturn;
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
	
	public synchronized Iterable<Coordinate> getPoints() {
		return playerTrail;
	}
	
	public void stop() {
		hasStopped = true;
	}
	
	public boolean hasStopped() {
		return hasStopped;
	}
}

