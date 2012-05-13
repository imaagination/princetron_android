package princeTron.Engine;

/**
 * Simple class containing two integer values and a comparison function.
 * There's probably something I should use instead, but this was quick and
 * easy to build.
 * 
 */
public class Coordinate {
	public int x;
	public int y;

	public Coordinate(int newX, int newY) {
		x = newX;
		y = newY;
	}

	@Override
	public boolean equals(Object oThat) {
		if (this == oThat) return true;
		if (!(oThat instanceof Coordinate)) return false;
		Coordinate that = (Coordinate) oThat;
		return this.x == that.x && this.y == that.y;
	}
	
	@Override
	public int hashCode() {
		return 41*(41 + x) + y;
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
