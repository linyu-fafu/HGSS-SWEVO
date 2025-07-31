import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Problem {
	private static Problem problem = null;

	private List<Instance> lstInstances = new ArrayList<>();
    private int nodeNum;
    private int rBestCost;
    private int xBestCost;
	/* -------------ISPD Modified------------------ */
	private int[] grid = new int[2];
	/* ------------------------------------------- */
    private int[][] pos;
    private int[][] dists;
    private int[][] rank;
    List<Edge> orderedEdges;
    List<Edge>[] orderedEdgesOfNode;
	List<Edge> allEdge;

	public int getInstanceNum() { return lstInstances.size(); }
    public int getNodeNum() { return nodeNum;}
    public int getRBestCost() { return rBestCost;}
    public int getXBestCost() { return xBestCost;}
    public Point getNode(int index) { return new Point(pos[index][0], pos[index][1]); }
    public int getRank(int n1, int n2) { return rank[n1][n2]; }
    public int getDistance(int n1, int n2) { return Math.abs(pos[n1][0] - pos[n2][0]) + Math.abs(pos[n1][1] - pos[n2][1]);}//dists[n1][n2];}//Math.abs(pos[n1][0] - pos[n2][0]) + Math.abs(pos[n1][1] - pos[n2][1]); }
    public List<Point> getNodes() {
    	List<Point> nodes = new ArrayList<>();
        for (int[] po : pos) {
            nodes.add(new Point(po[0], po[1]));
        }
    	return nodes;
    }
    
    public List<Edge> getOrderedEdges() {
    	return orderedEdges;
    }

	public int[][] getDists() {
		return dists;
	}



	public List<Edge> getOrderedEdges(int node) {
    	return orderedEdgesOfNode[node];
    }

	public List<Edge> getAllEdge() {
		return allEdge;
	}

	private Problem(String dataFilePath) throws FileNotFoundException,IOException {

		String[] pathSplits = dataFilePath.split("\\\\");
		String dataType = pathSplits[pathSplits.length - 2].toLowerCase();
		assert (dataType.equals("geo") || dataType.contains("ispd")): "Unknown problem data type!";

		File dataFile = new File(dataFilePath);
		try (Scanner dataScan = new Scanner(dataFile)) {

			if (dataType.equals("geo")) {

//				 read GEO data
				int nodeNum = dataScan.nextInt();
				int rBestCost = dataScan.nextInt();
				int xBestCost = dataScan.nextInt();

				int[][] pos = new int[nodeNum][2];
				for (int i = 0; i < nodeNum; i++) {
					//dataScan.nextInt();
					pos[i][0] = dataScan.nextInt();
					pos[i][1] = dataScan.nextInt();
				}
				lstInstances.add(new Instance(nodeNum, rBestCost, xBestCost, pos));

			}  else {
				readFileISPD(dataScan);
			}
		}
    	set(0); //use the first instance
    }



    public void set(int idx) {
    	Instance inst = lstInstances.get(idx);
    	nodeNum = inst.getNodeNum();
    	rBestCost = inst.getRBestCost();
    	xBestCost = inst.getXBestCost();
    	pos = inst.getPos();
		allEdge = new ArrayList<>();
    	orderedEdges = new ArrayList<Edge>();
    	dists = new int[nodeNum][nodeNum];
    	for (int i = 0; i < pos.length; i++) {
			//System.out.println(i);
    		for (int j = i + 1; j < pos.length; j++) {
    		 	 dists[i][j] = Math.abs(pos[i][0] - pos[j][0]) + Math.abs(pos[i][1] - pos[j][1]);
    			dists[j][i] = dists[i][j];
				allEdge.add(new Edge(i, j, 0));
				allEdge.add(new Edge(i, j,1));
				allEdge.add(new Edge(i, j, 2));
				allEdge.add(new Edge(i, j,3));
    			if (i < j) {
    				Edge e = new Edge(i, j, dists[i][j]);
    				e.dist = dists[i][j];
    				//tempLists.add(e);
                    orderedEdges.add(e);
    			}
    		}

    	}

    	Collections.sort(orderedEdges);
    	orderedEdgesOfNode = new ArrayList[nodeNum];
    	for (int i = 0; i < nodeNum; i++) {
    		orderedEdgesOfNode[i] = new ArrayList<>();
    	}
		//System.out.println("1");
    	for (Edge e : orderedEdges) {
    		orderedEdgesOfNode[e.p1].add(e);
    		orderedEdgesOfNode[e.p2].add(e);
    	}

    	int[] index = new int[nodeNum];
    	rank = new int[nodeNum][nodeNum];//rank二维数组用来记录排名，数值越小说明边的优先级越高
    	for (Edge e : orderedEdges) {
    		rank[e.p1][e.p2] = index[e.p1];
    		index[e.p1] += 1;

    		rank[e.p2][e.p1] = index[e.p2];
    		index[e.p2] += 1;
    	}
    }
    
    private void readFileISPD(Scanner scan) {
		scan.nextLine();
		lstInstances = new ArrayList<>();
		while (scan.hasNext()){
			String netInfo = scan.nextLine();
			String[] infos = netInfo.split(" ");
			int idx = Integer.parseInt(infos[1]);
			int n = Integer.parseInt(infos[2]);
			Set<Point> pset = new HashSet<>();
			while (n-- > 0){
				String nodeInfo = scan.nextLine();
    			String[] nodes = nodeInfo.split(" ");
    			//System.out.println(nodeInfo);
    			pset.add(new Point(Integer.parseInt(nodes[0]), Integer.parseInt(nodes[1])));
			}
			int nodeNum = pset.size();
			int bestCost = 0;
			int[][] pos = new int[nodeNum][2];
			int i = 0;
			for (Point p : pset) {
				pos[i][0] = p.x;
				pos[i][1] = p.y;
				i++;
			}
			lstInstances.add(new Instance(nodeNum, bestCost, pos));
		}
    }

    
    public static Problem readProblem(String fileName) {
    	try {
    		problem = new Problem(fileName);
        	return problem;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
    
    public static Problem get() { return problem; }
    
    public String toString() {
    	String str = "" + nodeNum  + "\n";
    	
    	for (int i = 0; i < nodeNum; i++) {
    		str += pos[i][0] + "\t" + pos[i][1] + "\n";
    	}
    	return str;
    }
    
    public static void main(String[] args) {
//    	String fileName = (new File("")).getAbsolutePath() + "\\..\\datas\\GEO\\01input8.txt";
//    	String fileName = (new File("")).getAbsolutePath() + "\\..\\datas\\ISPD98\\10.txt";
		String fileName = (new File("")).getAbsolutePath() + "\\..\\datas\\ISPD98Modified\\ibm05.modified.txt";
    	try {
    	    Problem problem = Problem.readProblem(fileName);
    	    //System.out.println(problem);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
     }
}

class Instance {

    private int nodeNum;
    private int rBestCost;
    private int xBestCost;
    private int[][] pos;
    
    public Instance(int n, int best, int[][] p) {
     	this.nodeNum = n;
     	this.rBestCost = best;
    	this.pos = p;
    }
    
    public Instance(int n, int rBest, int xBest, int[][] p) {
     	this.nodeNum = n;
     	this.rBestCost = rBest;
     	this.xBestCost = xBest;
    	this.pos = p;
    }
    
    public int getNodeNum() { return nodeNum; }
    public int getRBestCost() { return rBestCost; }
    public int getXBestCost() { return xBestCost; }
    public int[][] getPos() { return pos; }

}

