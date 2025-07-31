import java.util.Objects;

public class Point implements Comparable<Point>{
    int x;
    int y;
    
    Point(int x, int y) {
    	this.x = x;
    	this.y = y;
    }
    
    public String toString() {
    	return  x + "\t" + y;
    }

	@Override
	public int compareTo(Point o) {
		if (this.x == o.x) {
			return this.y - o.y;
		} else {
			return this.x - o.x;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return this.compareTo((Point)o) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.x+"x", this.y+"y");
	}
}
