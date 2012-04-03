package princeTron.Engine;

import java.util.ArrayList;
import android.graphics.Point;

//import android.util.Log;

public class Player {

	//a list of Coordinates that define a players trail
	public ArrayList<Point> playerTrail = new ArrayList<Point>();
	public int direction;

	public Player(Point coord, int direction){
		playerTrail.add(coord);
		this.direction = direction;
		//this.nextDirection = direction;
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
}

