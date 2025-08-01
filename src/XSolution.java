import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XSolution extends Solution {
    public XSolution() {
        super();
    }

    public XSolution(Edge[] edges) {
        super(edges);
    }

    public XSolution(List<Edge> edges) {
        super(edges);
    }
    public XSolution(int num) {
        super(num);
    }

    @Override
    public void decode() {
        stNodes = prob.getNodes();
        stEdgeDire000 = new ArrayList<>();
        stEdgeDire045 = new ArrayList<>();
        stEdgeDire090 = new ArrayList<>();
        stEdgeDire135 = new ArrayList<>();

        int nodeIndex = stNodes.size();

        for (Edge edge : edges) {
            int n1 = edge.p1, n2 = edge.p2;
            Point p1 = stNodes.get(n1), p2 = stNodes.get(n2);

            if (p1.x > p2.x || (p1.x == p2.x && p1.y > p2.y)) {
                Point temp = p1;
                p1 = p2; p2 = temp;

                int t = n1;
                n1 = n2; n2 = t;
            }

            if (p1.x != p2.x && p1.y != p2.y) {   // p1 and p2 are not in a vertical/horizontal line
                //create a steiner point
                int[] newNode = new int[2];

                if (edge.direction == 0) {              //左上角直角
                    newNode[0] = p1.x;
                    newNode[1] = p2.y;

                    // add sNode & sEdge
                    stNodes.add(new Point(newNode[0], newNode[1]));

                    if (p2.y > p1.y) {
                        stEdgeDire090.add(new Edge(n1, nodeIndex, 90));// vertical
                        stEdgeDire000.add(new Edge(nodeIndex, n2, 0));    // horizontal

                    } else {
                        stEdgeDire090.add(new Edge(nodeIndex, n1, 90));   // vertical
                        stEdgeDire000.add(new Edge(nodeIndex, n2, 0));    // horizontal
                    }
                    nodeIndex++;


                } else if (edge.direction == 1) {       //右下角直角
                    newNode[0] = p2.x;
                    newNode[1] = p1.y;

                    // add sNode & sEdge
                    stNodes.add(new Point(newNode[0], newNode[1]));

                    if (p2.y > p1.y) {
                        stEdgeDire000.add(new Edge(n1, nodeIndex, 0));    // horizontal
                        stEdgeDire090.add(new Edge(nodeIndex, n2, 90));   // vertical

                    } else {
                        stEdgeDire000.add(new Edge(n1, nodeIndex, 0));    // horizontal
                        stEdgeDire090.add(new Edge(n2, nodeIndex, 90));   // vertical
                    }
                    nodeIndex++;

                } else if (edge.direction == 2) {
                    int dx = p2.x - p1.x;
                    int dy = Math.abs(p2.y - p1.y);

                    if (dx == dy) {         // do not need steiner point
                        if (p2.y > p1.y) {
                            stEdgeDire045.add(new Edge(n1, n2, 45));      // 45 degree
                        } else {
                            stEdgeDire135.add(new Edge(n1, n2, 135));    // 135 degree
                        }

                    } else if (p2.y > p1.y) {
                        if (dy > dx) {
                            newNode[0] = p2.x;
                            newNode[1] = p1.y + dx;

                            stNodes.add(new Point(newNode[0], newNode[1]));
                            stEdgeDire045.add(new Edge(n1, nodeIndex, 45));   // 45 degree
                            stEdgeDire090.add(new Edge(nodeIndex, n2, 90));   // vertical

                        } else {
                            newNode[0] = p1.x + dy;
                            newNode[1] = p2.y;

                            stNodes.add(new Point(newNode[0], newNode[1]));
                            stEdgeDire045.add(new Edge(n1, nodeIndex, 45));   // 45 degree
                            stEdgeDire000.add(new Edge(nodeIndex, n2, 0));    // horizontal

                        }
                        nodeIndex++;

                    // p2.y <= p1.y
                    } else if (dy > dx) {
                        newNode[0] = p2.x;
                        newNode[1] = p1.y - dx;

                        stNodes.add(new Point(newNode[0], newNode[1]));
                        stEdgeDire135.add(new Edge(n1, nodeIndex, 135));     // 135 degree
                        stEdgeDire090.add(new Edge(n2, nodeIndex, 90));       // vertical

                        nodeIndex++;

                    } else {        // p2.y <= p1.y && dy <= dx
                        newNode[0] = p1.x + dy;
                        newNode[1] = p2.y;

                        stNodes.add(new Point(newNode[0], newNode[1]));
                        stEdgeDire135.add(new Edge(n1, nodeIndex, 135));     // 135 degree
                        stEdgeDire000.add(new Edge(nodeIndex, n2, 0));        // horizontal

                        nodeIndex++;
                    }

                } else {        //edges[i].direction == 3
                    int dx = p2.x - p1.x;
                    int dy = Math.abs(p2.y - p1.y);
                    if (dx == dy) {
                        // do not need steiner point
                        if (p2.y > p1.y) {
                            stEdgeDire045.add(new Edge(n1, n2, 45));      // 45 degree
                        } else {
                            stEdgeDire135.add(new Edge(n1, n2, 135));    // 135 degree
                        }

                    } else if (p2.y > p1.y) {
                        if (dy > dx) {
                            newNode[0] = p1.x;
                            newNode[1] = p2.y - dx;

                            stNodes.add(new Point(newNode[0], newNode[1]));
                            stEdgeDire090.add(new Edge(n1, nodeIndex, 90));   // vertical
                            stEdgeDire045.add(new Edge(nodeIndex, n2, 45));   // 45 degree

                        } else {
                            newNode[0] = p2.x - dy;
                            newNode[1] = p1.y;

                            stNodes.add(new Point(newNode[0], newNode[1]));
                            stEdgeDire000.add(new Edge(n1, nodeIndex, 0));    // horizontal
                            stEdgeDire045.add(new Edge(nodeIndex, n2, 45));   // 45 degree
                        }
                        nodeIndex++;

                    // p2.y <= p1.y
                    } else if (dy > dx) {
                        newNode[0] = p1.x;
                        newNode[1] = p2.y + dx;

                        stNodes.add(new Point(newNode[0], newNode[1]));
                        stEdgeDire090.add(new Edge(nodeIndex, n1, 90));       // vertical
                        stEdgeDire135.add(new Edge(nodeIndex, n2, 135));     // 135 degree

                        nodeIndex++;

                    // p2.y <= p1.y  && dy <= dx
                    } else {
                        newNode[0] = p2.x - dy;
                        newNode[1] = p1.y;

                        stNodes.add(new Point(newNode[0], newNode[1]));
                        stEdgeDire000.add(new Edge(n1, nodeIndex, 0));        // horizontal
                        stEdgeDire135.add(new Edge(nodeIndex, n2, 135));     // 135 degree

                        nodeIndex++;
                    }
                }

            } else {    // horizontal or vertical line

                if (p2.x > p1.x) {
                    stEdgeDire000.add(new Edge(n1, n2, 0));           // horizontal
                } else {
                    stEdgeDire090.add(new Edge(n1, n2, 90));          // vertical
                }
            }
        }

        cost = quickAdjustedCost();
    }


    @Override
    public int quickAdjustedCost() {

        int removed = 0;
        stEdgeDire000.sort((e1, e2) -> stNodes.get(e1.p1).y - stNodes.get(e2.p1).y);
        for (int k = 1; k< stEdgeDire000.size(); k++) {
            Edge e1 = stEdgeDire000.get(k);
            int e1x1 = stNodes.get(e1.p1).x;
            int e1y1 = stNodes.get(e1.p1).y;
            int e1x2 = stNodes.get(e1.p2).x;
//            int e1y2 = stNodes.get(e1.p2).y;
            int max = 0;
            for (int i=k-1; i>=0; i--) {
                Edge e2 = stEdgeDire000.get(i);
                int e2x1 = stNodes.get(e2.p1).x;
                int e2y1 = stNodes.get(e2.p1).y;
                int e2x2 = stNodes.get(e2.p2).x;
//                int e2y2 = stNodes.get(e2.p2).y;
                if (e1y1 != e2y1) { break; }
                if (e1x1 >= e2x2) { continue; }
                // 计算重叠的部分
                int d = Math.min(e2x2, e1x2) - Math.max(e2x1, e1x1);
                if (d > max) { max = d;}

            }
            removed += max;

        }

        stEdgeDire045.sort((e1, e2) -> (stNodes.get(e1.p1).y - stNodes.get(e1.p1).x) - (stNodes.get(e2.p1).y - stNodes.get(e2.p1).x));
        for (int k = 1; k< stEdgeDire045.size(); k++) {
            Edge e1 = stEdgeDire045.get(k);
            int e1x1 = stNodes.get(e1.p1).x;
            int e1y1 = stNodes.get(e1.p1).y;
            int e1x2 = stNodes.get(e1.p2).x;
//            int e1y2 = stNodes.get(e1.p2).y;
            int max = 0;
            for (int i=k-1; i>=0; i--) {
                Edge e2 = stEdgeDire045.get(i);
                int e2x1 = stNodes.get(e2.p1).x;
                int e2y1 = stNodes.get(e2.p1).y;
                int e2x2 = stNodes.get(e2.p2).x;
//                int e2y2 = stNodes.get(e2.p2).y;
                if (e1y1-e1x1 != e2y1-e2x1) { break; }
                if (e1x1 >= e2x2) { continue; }
                // 计算重叠的部分
                int dx = Math.min(e2x2, e1x2) - Math.max(e2x1, e1x1);
                int d = (int)(1.4142136 * dx + 0.5);
                if (d > max) { max = d;}
            }
            removed += max;
        }


        stEdgeDire090.sort((e1, e2) -> stNodes.get(e1.p1).x - stNodes.get(e2.p1).x);
        for (int k = 1; k< stEdgeDire090.size(); k++) {
            Edge e1 = stEdgeDire090.get(k);
            int e1x1 = stNodes.get(e1.p1).x;
            int e1y1 = stNodes.get(e1.p1).y;
//            int e1x2 = stNodes.get(e1.p2).x;
            int e1y2 = stNodes.get(e1.p2).y;
            int max = 0;
            for (int i=k-1; i>=0; i--) {
                Edge e2 = stEdgeDire090.get(i);
                int e2x1 = stNodes.get(e2.p1).x;
                int e2y1 = stNodes.get(e2.p1).y;
//                int e2x2 = stNodes.get(e2.p2).x;
                int e2y2 = stNodes.get(e2.p2).y;
                if (e1x1 != e2x1) { break; }
                if (e1y1 >= e2y2) { continue; }
                // 计算重叠的部分
                int d = Math.min(e2y2, e1y2) - Math.max(e2y1, e1y1);
                if (d > max) { max = d;}
            }
            removed += max;
        }

        stEdgeDire135.sort((e1, e2) -> (stNodes.get(e1.p1).y + stNodes.get(e1.p1).x) - (stNodes.get(e2.p1).y + stNodes.get(e2.p1).x));
        for (int k = 1; k< stEdgeDire135.size(); k++) {
            Edge e1 = stEdgeDire135.get(k);
            int e1x1 = stNodes.get(e1.p1).x;
            int e1y1 = stNodes.get(e1.p1).y;
            int e1x2 = stNodes.get(e1.p2).x;
//            int e1y2 = stNodes.get(e1.p2).y;
            int max = 0;
            for (int i=k-1; i>=0; i--) {
                Edge e2 = stEdgeDire135.get(i);
                int e2x1 = stNodes.get(e2.p1).x;
                int e2y1 = stNodes.get(e2.p1).y;
                int e2x2 = stNodes.get(e2.p2).x;
//                int e2y2 = stNodes.get(e2.p2).y;
                if (e1y1+e1x1 != e2y1+e2x1) { break; }
                if (e1x1 >= e2x2) { continue; }
                // 计算重叠的部分
                int dx = Math.min(e2x2, e1x2) - Math.max(e2x1, e1x1);
                int d = (int)(1.4142136 * dx + 0.5);
                if (d > max) { max = d;}
            }
            removed += max;
        }

        int cost = 0;
        for (Edge e : stEdgeDire000) {
            cost += stNodes.get(e.p2).x - stNodes.get(e.p1).x;
        }
        for (Edge e : stEdgeDire045) {
            int dx = stNodes.get(e.p2).x - stNodes.get(e.p1).x;
            cost += (int) (1.4142136 * dx + 0.5);
        }
        for (Edge e : stEdgeDire090) {
            cost += stNodes.get(e.p2).y - stNodes.get(e.p1).y;
        }
        for (Edge e : stEdgeDire135) {
            int dx = stNodes.get(e.p2).x - stNodes.get(e.p1).x;
            cost += (int) (1.4142136 * dx + 0.5);
        }

        return cost - removed;
    }


    public static void main(String[] args) {
        String name = "1";//"01input8";//8,9,10,20,50,70,100,410,500,1000
        //String fileName = (new File("")).getAbsolutePath() + "/../instances/GEO/" + name + ".txt";
        String fileName = (new File("")).getAbsolutePath() + "/../instances/ISPD/" + name + ".txt";

        try {
            Problem problem = Problem.readProblem(fileName);
            System.out.println(problem);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Problem.get().set(31);
        System.out.println(Problem.get());
        Solution.init();
        Solution gs = new XSolution();
        int times = 1;
        int total = 0;
        for (int t = 0; t < times; t++) {
            Solution s = new XSolution();

            System.out.println(t + "----" + Problem.get().getXBestCost() + ", " + s.cost + "-->" + s.hillClimbFlip().cost);
            total += s.cost;
            if (s.cost < gs.cost) {
                gs = s;
            }
        }
        System.out.println(Problem.get().getXBestCost() + ", " + gs.cost + "," + total / times);

    }


}
