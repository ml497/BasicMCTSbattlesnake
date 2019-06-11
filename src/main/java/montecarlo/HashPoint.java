package montecarlo;

import java.awt.Point;

public class HashPoint extends Point {
	
	public HashPoint(int x, int y) {
		super(x, y);
	}
	
	@Override
	public int hashCode() {
	    int result = x;
	    result = 31 * result + y;
	    return result;
	}
}
