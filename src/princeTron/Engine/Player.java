package princeTron.Engine;

import java.util.ArrayList;

import android.util.Log;

public class Player {

	//a list of Coordinates that define a players trail
	public ArrayList<Coordinate> playerTrail = new ArrayList<Coordinate>();
	public int curDirection;
	public int nextDirection;

	public Player(Coordinate coord, int direction){
		playerTrail.add(coord);
		this.curDirection = direction;
		this.nextDirection = direction;
	}

	public int getCurDirection(){
		return this.curDirection;
	}

	public int getNextDirection(){
		return this.nextDirection;
	}

	public void setCurDirection(int direction){
		this.curDirection = direction;
	}

	public void setNextDirection(int direction){
		this.nextDirection = direction;
	}

	public int getSize(){
		return playerTrail.size();
	}
}

