import java.util.Objects;

public class Edge implements Comparable<Edge> {
    int p1, p2;
    int direction;
    int dist;

    public Edge(int p1, int p2, int direction) {
        this.p1 = p1;
        this.p2 = p2;
        this.direction = direction;
    }


    public int rectDistance(Point[] ps) {
        dist = Math.abs(ps[p2].x - ps[p1].x) + Math.abs(ps[p2].y - ps[p1].y);
        return dist;
    }


    @Override
    public int compareTo(Edge arg) {
        if (this.dist > arg.dist) {
            return 1;
        } else if (this.dist == arg.dist) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge that = (Edge) obj;
        return direction == that.direction  &&  ((p1 == that.p1 && p2 == that.p2) || (p1 == that.p2 && p2 == that.p1));
       //

    }

    @Override
    public int hashCode() {
        return Objects.hash(p1, p2);
    }

}
