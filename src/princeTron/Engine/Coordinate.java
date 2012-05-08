package princeTron.Engine;

/**
 * Simple class containing two integer values and a comparison function.
 * There's probably something I should use instead, but this was quick and
 * easy to build.
 * 
 */
public class Coordinate implements Cloneable {
	public int x;
	public int y;

	public Coordinate(int newX, int newY) {
		x = newX;
		y = newY;
	}

	public boolean equals(Coordinate other) {
		
		return true;//return (x == other.x && y == other.y);
	}

	@Override
	public String toString() {
		return "Coordinate: [" + x + "," + y + "]";
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
