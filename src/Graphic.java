import java.io.File;
import java.util.*;


public class Graphic {
    private static Random rand = new Random();




    public static void addEdge(List<Edge> edges, Edge e) {
        Problem prob = Problem.get();
        List<Edge> path = findPath(edges, e);//findPath(edges, e, prob.getNodeNum());
        int idx = 0;//rand.nextInt(path.size());
        int maxDist = prob.getDistance(path.get(0).p1, path.get(0).p2);
        for (int i = 1; i < path.size(); i++) {
            int dist = prob.getDistance(path.get(i).p1, path.get(i).p2);
            if (dist > maxDist) {
                maxDist = dist;
                idx = i;
            }
        }
        for (int i = 0; i < edges.size(); i++) {

            if ((edges.get(i).p1 == path.get(idx).p1 && edges.get(i).p2 == path.get(idx).p2) ||
                    (edges.get(i).p1 == path.get(idx).p2 && edges.get(i).p2 == path.get(idx).p1)) {
                edges.remove(i);
                break;
            }
        }
        edges.add(e);
    }


    public static Edge findNewEdge(List<Edge> edges) {
        Problem prob = Problem.get();
        int nodeNum = prob.getNodeNum();
        while (true) {
            int node = rand.nextInt(nodeNum);
            boolean[] inEdges = new boolean[nodeNum];
            for (Edge e : edges) {
                if (e.p1 == node) {
                    inEdges[e.p2] = true;
                } else if (e.p2 == node) {
                    inEdges[e.p1] = true;
                }
            }

			for (Edge e : prob.getOrderedEdges(node)) {
				if (e.p1 == node && !inEdges[e.p2]) {
					if (rand.nextDouble() < Simulations.greedyAddProb){

                        return e;
                    }
				} else if (e.p2 == node && !inEdges[e.p1]) {
					if (rand.nextDouble() < Simulations.greedyAddProb){
                        return e;
                    }
				}
			}
        }
    }


    public static List<Edge> findPath(List<Edge> edges, Edge e, int nodeNum) {
        List<Edge> path = new ArrayList<>();

        List<Integer> nodes = new ArrayList<>();
        boolean[] processed = new boolean[nodeNum];
        processed[e.p1] = true;
        processed[e.p2] = true;
        int[] parentNode = new int[nodeNum];
        parentNode[e.p1] = -1;
        for (Edge edge : edges) {
            if (edge.p1 == e.p1 && edge.p2 != e.p2) {
                nodes.add(edge.p2);
                parentNode[edge.p2] = e.p1;
                processed[edge.p2] = true;
            } else if (edge.p1 != e.p2 && edge.p2 == e.p1) {
                nodes.add(edge.p1);
                parentNode[edge.p1] = e.p1;
                processed[edge.p1] = true;
            }
        }

        while (!nodes.isEmpty()) {
            int n1 = nodes.remove(0);
            for (Edge edge : edges) {
                if ((edge.p1 == n1 && edge.p2 == e.p2) ||
                        (edge.p2 == n1 && edge.p1 == e.p2)) {//found
                    path.add(edge);
                    while (parentNode[n1] != -1) {
                        path.add(new Edge(n1, parentNode[n1], 0));
                        n1 = parentNode[n1];
                    }
                    return path;
                } else if (edge.p1 == n1 && !processed[edge.p2]) {
                    nodes.add(edge.p2);
                    parentNode[edge.p2] = n1;
                    processed[edge.p2] = true;
                } else if (edge.p2 == n1 && !processed[edge.p1]) {
                    nodes.add(edge.p1);
                    parentNode[edge.p1] = n1;
                    processed[edge.p1] = true;
                }
            }
        }

        return path;//should not reach here
    }

    public static List<Edge> findPath(List<Edge> edges, Edge e) {
        Problem prob = Problem.get();
        int nodeNum = prob.getNodeNum();
        List<Edge> path = new ArrayList<>();

        List<Integer> nodes = new LinkedList<>();
        boolean[] processed = new boolean[nodeNum];
        processed[e.p1] = true;
        processed[e.p2] = true;
        int[] parentNode = new int[nodeNum];
        parentNode[e.p1] = -1;
        List<Integer>[] eLists = toLists(edges);


        for (int node : eLists[e.p1]) {
            if (!processed[node]) {
                nodes.add(node);
                parentNode[node] = e.p1;
                processed[node] = true;
            }
        }

        while (!nodes.isEmpty()) {
            int n1 = ((LinkedList<Integer>) nodes).poll();
            for (int node : eLists[n1]) {
                if (node == e.p2) {
                    path.add(new Edge(n1, node, 0));
                    while (parentNode[n1] != -1) {
                        path.add(new Edge(n1, parentNode[n1], 0));
                        n1 = parentNode[n1];
                    }
                    Collections.reverse(path);
                    return path;
                } else if (!processed[node]) {
                    nodes.add(node);
                    parentNode[node] = n1;
                    processed[node] = true;
                }
            }
        }

        return path;
    }


    public static List<Integer>[] toLists(List<Edge> edges) {
        Problem prob = Problem.get();
        List<Integer>[] eLists = new ArrayList[prob.getNodeNum()];
        for (int i = 0; i < eLists.length; i++) {
            eLists[i] = new ArrayList<>();
        }

        for (Edge e : edges) {
            eLists[e.p1].add(e.p2);
            eLists[e.p2].add(e.p1);
        }

        return eLists;
    }

    public static List<Edge> findExtra(List<Edge> edges1, List<Edge> edges2) {



        List<Edge> extra = new ArrayList<>();
        for (Edge e1 : edges1) {
            boolean found = false;
            for (Edge e2 : edges2) {
                if ((e1.p1 == e2.p1 && e1.p2 == e2.p2) ||
                        (e1.p2 == e2.p1 && e1.p1 == e2.p2 )) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                extra.add(new Edge(e1.p1, e1.p2, e1.direction));
            }
        }

        return extra;
    }

    public static void main(String[] args) {
        String name = "input8";//8,9,10,20,50,70,100,410,500,1000
        String fileName = (new File("")).getAbsolutePath() + "/../datas/GEO/" + name + ".txt";
        try {
            Problem problem = Problem.readProblem(fileName);
            System.out.println(problem);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int nodeNum = Problem.get().getNodeNum();
        Solution.init();
        Solution s = new Solution();
        List<Edge> edges = s.getEdges();
        for (Edge e : edges) {
            System.out.println(e.p1 + "->" + e.p2);
        }
        int node = 1;
        for (Edge e : edges) {
            int n = e.p1;
            for (int i = 0; i < nodeNum; i++) {
                if (i == n) continue;
                boolean in = false;
                for (Edge e1 : edges) {
                    if (e1.p1 == n && e1.p2 == i) {
                        in = true;
                    }
                }

                if (!in) {
                    System.out.println("Add edge: " + n + "->" + i);
                    Edge e1 = new Edge(n, i, 0);
                    edges.add(e1);
                    List<Edge> path = Graphic.findPath(edges, e1, nodeNum);
                    System.out.println("Path");
                    for (Edge edge : path) {
                        System.out.println(edge.p1 + "->" + edge.p2);
                    }
                    return;
                }
            }
        }
    }


}
