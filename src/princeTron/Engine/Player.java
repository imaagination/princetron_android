package princeTron.Engine;

import java.util.ArrayList;
import android.graphics.Point;

//import android.util.Log;

public class Player {

	//a list of Coordinates that define a players trail
	private ArrayList<Point> playerTrail = new ArrayList<Point>();
	private Point lastPoint = new Point();
	private int direction;
	private int id;
	public static int static_id = 0;

	public Player(Point coord, int direction){
		playerTrail.add(coord);
		lastPoint = coord;
		this.direction = direction;
		id = static_id;
		static_id++;
		//this.nextDirection = direction;
	}
	
	// numSteps will likely always be 1, except in the case that a "turn"
	// message is received, in which case the player is "rewound", and then
	// "fast-forwarded" the difference between the time stamp and current time
	public void stepForward(int numSteps) {
		Point newPoint = new Point(lastPoint.x, lastPoint.y);
		switch (direction) {
		case GameEngine.NORTH:
			newPoint = new Point(lastPoint.x, lastPoint.y + numSteps);
			break;
		case GameEngine.EAST:
			newPoint = new Point(lastPoint.x + numSteps, lastPoint.y);
			break;
		case GameEngine.SOUTH:
			newPoint = new Point(lastPoint.x, lastPoint.y - numSteps);
			break;
		case GameEngine.WEST:
			newPoint = new Point(lastPoint.x - numSteps, lastPoint.y);
			break;
		}
		playerTrail.add(newPoint);
		lastPoint = newPoint;
	}
	
	// if a playerTurned message arrives in the past, we rewind the player
	public void stepBackward(long numSteps) {
		int stepsBack = 0;
		Point prev = playerTrail.get(playerTrail.size() - 1);
		while (stepsBack < numSteps && !playerTrail.isEmpty()) {
			Point removed = playerTrail.remove(playerTrail.size() - 1);
			// this assumes that only one dimension will change per point
			int dist = Math.abs((prev.x - removed.x)) + Math.abs((prev.x - removed.x));
			stepsBack += dist;
			prev = removed;
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
	
	public Iterable<Point> getPoints() {
		return playerTrail;
	}
}

