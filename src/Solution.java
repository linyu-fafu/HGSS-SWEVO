import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public  class Solution implements Comparable<Solution> {
    public static Problem prob;
    
    protected static Point[] nodes;
    protected static Random rand;
    protected static int decodeTypeNum = 2;
    protected static int[][] eLists;

    protected Edge[] edges;

    protected List<Point> stNodes; //decoded nodes

    protected List<Edge> stEdgeDire000;
    protected List<Edge> stEdgeDire045;
    protected List<Edge> stEdgeDire090;
    protected List<Edge> stEdgeDire135;
    protected List<Edge> sEdge; //decoded edges
    public int cost;
    public int lastImprove = 0;

    public int getCost() {
        return cost;
    }

    public int getLastImprove() {
        return lastImprove;
    }

    public List<Edge> getEdges() {
        List<Edge> edgeList = new ArrayList<>();
        for (Edge e : edges) {
            edgeList.add(new Edge(e.p1, e.p2, e.direction));
        }
        return edgeList;
    }


    public static void init() {
        prob = Problem.get();
        int nodeNum = prob.getNodeNum();
        nodes = new Point[nodeNum];
        for (int i = 0; i < nodeNum; i++) {
            nodes[i] = prob.getNode(i);
        }
        rand = new Random();
        if (Simulations.STRU_TYPE == EStructType.RECTILINEAR) {
            decodeTypeNum = 2;
        } else {
            decodeTypeNum = 4;
        }
        eLists = new int[nodeNum][nodeNum];
    }

    public static Solution make() {
        if (Simulations.STRU_TYPE == EStructType.RECTILINEAR) {
            return new Solution();
        } else {
            return new XSolution();
        }
    }

    public static Solution make(Edge[] edges) {
        if (Simulations.STRU_TYPE == EStructType.RECTILINEAR) {
            return new Solution(edges);
        } else {
            return new XSolution(edges);
        }
    }

    public static Solution make(List<Edge> edges) {
        if (Simulations.STRU_TYPE == EStructType.RECTILINEAR) {
            return new Solution(edges);
        } else {
            return new XSolution(edges);
        }
    }

    public static Solution make(int number) {
        if (Simulations.STRU_TYPE == EStructType.RECTILINEAR) {
            return new Solution(number);
        } else {
            return new XSolution(number);
        }
    }

    public Solution() {
        if (prob.getNodeNum() > 1) {
            if (Simulations.initType == EInitializeType.RANDOM) {
                construct();
            } else {
                modifyKruskal(0);
            }
            decode();
        } else {
            cost = 0;
        }
    }

    public Solution(Edge[] edges) {
        this.edges = new Edge[edges.length];
        for (int i = 0; i < this.edges.length; i++) {
            this.edges[i] = new Edge(edges[i].p1, edges[i].p2, edges[i].direction);
        }
        decode();
    }

    public Solution(List<Edge> edges) {
        this.edges = new Edge[edges.size()];
        for (int i = 0; i < this.edges.length; i++) {
            this.edges[i] = new Edge(edges.get(i).p1, edges.get(i).p2, edges.get(i).direction);
        }

        decode();
    }

    public Solution(int num) {
        if (prob.getNodeNum() > 1) {
            modifyKruskal(num);

           decode();
        } else {
            cost = 0;
        }
    }

    protected void construct() {
        int nodeNum = prob.getNodeNum();
        List<Integer> inTree = new ArrayList<>();
        List<Integer> remained = new ArrayList<>();
        for (int i = 0; i < nodeNum; i++) {
            remained.add(i);
        }
        Collections.shuffle(remained);

        edges = new Edge[nodeNum - 1];
        int index = 0;
        int node1 = remained.remove(0);
        inTree.add(node1);
        while (!remained.isEmpty()) {
            int node2 = remained.remove(0);
            edges[index] = new Edge(node1, node2, rand.nextInt(decodeTypeNum));
            inTree.add(node2);
            node1 = inTree.get(rand.nextInt(inTree.size()));
            index++;
        }

        cost = 0;
        for (Edge e : edges) {
            cost += e.rectDistance(nodes);
        }
    }
    protected void modifyKruskal(int num) {
        double pro;

        int nodeNum = prob.getNodeNum();
        edges = new Edge[nodeNum - 1];
        int[] treeID = new int[nodeNum];
        int[][] treeIDs = new int[nodeNum][nodeNum];
        int[] treeSize = new int[nodeNum];
        for (int i = 0; i < nodeNum; i++) {
            treeID[i] = i;
            treeIDs[i][0] = i;
            treeSize[i] = 1;
        }
        List<Edge> odrEdges = prob.getOrderedEdges();
        List<Edge> skippedValidEdges = new ArrayList<>(); // 存储被跳过的有效边

        if (num == 1) {
            pro = Simulations.iniProb;

        } else {
            pro = 1;
            Collections.shuffle(odrEdges);
        }

        int index = 0;
        //第一阶段
        for (Edge e : odrEdges) {
            if (index >= edges.length) break;

            int id1 = treeID[e.p1];
            int id2 = treeID[e.p2];
            if (id1 != id2) {
                if (rand.nextDouble() < pro) {
                    mergeTrees(treeID, treeIDs, treeSize, id1, id2);
                    e.direction = rand.nextInt(decodeTypeNum);
                    edges[index++] = e;
                } else {
                    skippedValidEdges.add(e);
                }
            }
        }
        Collections.sort(skippedValidEdges);
        Collections.sort(odrEdges);
        // 第二阶段
        if (index < edges.length) {
            Iterator<Edge> it = skippedValidEdges.iterator();
            while (it.hasNext() && index < edges.length) {
                Edge e = it.next();
                int id1 = treeID[e.p1];
                int id2 = treeID[e.p2];
                if (id1 != id2) {
                    mergeTrees(treeID, treeIDs, treeSize, id1, id2);
                    e.direction = rand.nextInt(decodeTypeNum);
                    edges[index++] = e;
                    it.remove();
                }
            }
        }
        Collections.sort(odrEdges);
        if (index < edges.length) {
            throw new IllegalStateException("无法生成完整生成树，图可能不连通");
        }
    }


    private void mergeTrees(int[] treeID, int[][] treeIDs, int[] treeSize, int id1, int id2) {
        if (treeSize[id1] < treeSize[id2]) {
            int temp = id1;
            id1 = id2;
            id2 = temp;
        }

        for (int j = 0; j < treeSize[id2]; j++) {
            int node = treeIDs[id2][j];
            treeID[node] = id1;
            treeIDs[id1][treeSize[id1]] = node;
            treeSize[id1]++;
        }
        treeSize[id2] = 0;
    }


    public List<Edge> greedyPerturbEdgesStage(List<Edge> edges) {

        int index = rand.nextInt(edges.size());
        if (rand.nextDouble() < Simulations.FLIP_PROB) {//0.05) {
            //change the direction of the selected edge
            edges.get(index).direction = (edges.get(index).direction + 1 + rand.nextInt(decodeTypeNum - 1)) % decodeTypeNum; //1 - edges.get(index).direction;
            return edges;
        } else {
            if (Simulations.PERTURB_TYPE == EPerturbType.REMOVE_ADD ||
                    (Simulations.PERTURB_TYPE == EPerturbType.HYBRID && rand.nextDouble() < 0.33)) {
                return greedyRemoveAddEdges(edges);
            } else if (rand.nextDouble() < 0.66) {
                return greedyInsertNode(edges);
            } else {
                return greedyAddRemoveEdges(edges);
            }
        }
    }

    private List<Edge> greedyInsertNode(List<Edge> edges) {
        List<Edge> edgesList = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            edgesList.add(new Edge(edges.get(i).p1, edges.get(i).p2, edges.get(i).direction));
        }
        //edgesList.addAll(edges);
        List<Edge> newEdges = new ArrayList<>();
        int[] degrees = new int[prob.getNodeNum()];
        for (int i = 0; i < edges.size(); i++) {
            newEdges.add(new Edge(edges.get(i).p1, edges.get(i).p2, edges.get(i).direction));
            degrees[edges.get(i).p1]++;
            degrees[edges.get(i).p2]++;
        }

        int count = 0;
        List<Integer> notNode = new ArrayList<>();
        for (int i = 0; i < degrees.length; i++) {
            if (degrees[i] != 1) {
                count++;
                notNode.add(i);
            }
        }
        int delIndex = rand.nextInt(prob.getNodeNum());//选择其中的一个节点
        while (notNode.contains(delIndex) && count == 1) {
            delIndex = rand.nextInt(prob.getNodeNum());
        }
        List<Edge> temp = new ArrayList<>();//临时列表
        int index = 0;
        for (Edge edge : edgesList) {
            if (edge.p1 == delIndex || edge.p2 == delIndex) {
                temp.add(edge);
            }
        }
        //Collections.sort(temp);
        if (temp.size() == 1) {//叶节点
            edgesList.remove(temp.get(0));
            Edge edgeIndex = findInsertEdge(edgesList, delIndex, Distance(temp.get(0).p1, temp.get(0).p2));
            newEdges.remove(temp.get(0));
            newEdges.remove(edgeIndex);
            newEdges.add(new Edge(delIndex, edgeIndex.p1, rand.nextInt(decodeTypeNum)));
            newEdges.add(new Edge(delIndex, edgeIndex.p2, rand.nextInt(decodeTypeNum)));
        } else {
            boolean flag = true;
            Collections.reverse(temp);
            int delNum_1 = 0;
            int delNum_2 = 1;
            while (flag) {
                flag = false;
                int node_1 = (temp.get(delNum_1).p1 == delIndex) ? temp.get(delNum_1).p2 : temp.get(delNum_1).p1;
                int node_2 = (temp.get(delNum_2).p1 == delIndex) ? temp.get(delNum_2).p2 : temp.get(delNum_2).p1;

                Edge addNode_1 = new Edge(node_1, node_2, rand.nextInt(decodeTypeNum)); //增加连接边
                edgesList.remove(temp.get(delNum_1));
                edgesList.remove(temp.get(delNum_2));
                //edgesList.add(addNode_1);
                Map<Integer, List<Integer>> tree = constructTree(edgesList);
                if (tree.get(delIndex) == null) {
                    int delDist = Distance(temp.get(delNum_1).p1, temp.get(delNum_1).p2) + Distance(temp.get(delNum_2).p1, temp.get(delNum_2).p2);
                    Edge addIndex = findInsertEdge(edgesList, delIndex, delDist);
                    if (addNode_1 != addIndex) {
                        newEdges.remove(temp.get(delNum_1));
                        newEdges.remove(temp.get(delNum_2));
                        newEdges.remove(addIndex);
                        newEdges.add(addNode_1);
                        newEdges.add(new Edge(delIndex, addIndex.p1, rand.nextInt(decodeTypeNum)));
                        newEdges.add(new Edge(delIndex, addIndex.p2, rand.nextInt(decodeTypeNum)));
                    } else {

                        return newEdges;
                    }

                } else if (degrees[node_1] == 1 && degrees[node_2] == 1) {

                    flag = true;
                    edgesList.remove(addNode_1);
                    edgesList.add(temp.get(delNum_1));
                    edgesList.add(temp.get(delNum_2));
                    delNum_1 = rand.nextInt(temp.size());//随机选择两条边
                    delNum_2 = rand.nextInt(temp.size());
                    while (delNum_1 == delNum_2) {
                        index++;
                        delNum_2 = rand.nextInt(temp.size());

                    }
                } else {
                    //遍历子集树
                    List<Integer> traversalResult = traverseTree(tree, delIndex);
                    List<Edge> removeEdges = new ArrayList<>();
                    for (Edge edge : edgesList) {
                        if (traversalResult.contains(edge.p1) || traversalResult.contains(edge.p2) || edge.equals(addNode_1))
                            continue;
                        removeEdges.add(edge);
                    }
                    int delDist = Distance(temp.get(delNum_1).p1, temp.get(delNum_1).p2) + Distance(temp.get(delNum_2).p1, temp.get(delNum_2).p2);
                    Edge addIndex = findInsertEdge(removeEdges, delIndex, delDist);
                    if (addIndex != addNode_1) {
                        newEdges.remove(temp.get(delNum_1));
                        newEdges.remove(temp.get(delNum_2));
                        newEdges.remove(addIndex);
                        newEdges.add(addNode_1);
                        newEdges.add(new Edge(delIndex, addIndex.p1, rand.nextInt(decodeTypeNum)));
                        newEdges.add(new Edge(delIndex, addIndex.p2, rand.nextInt(decodeTypeNum)));
                    } else {

                        return newEdges;
                    }


                }
            }
        }
        return newEdges;
    }

    private static void dfs(Map<Integer, List<Integer>> tree, int node, Set<Integer> visited, List<Integer> traversalResult) {
        visited.add(node);
        traversalResult.add(node);

        if (tree.containsKey(node)) {
            for (int neighbor : tree.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfs(tree, neighbor, visited, traversalResult);
                }
            }
        }
    }

    private static List<Integer> traverseTree(Map<Integer, List<Integer>> tree, int startNode) {
        List<Integer> traversalResult = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        dfs(tree, startNode, visited, traversalResult);

        return traversalResult;
    }

    private static Map<Integer, List<Integer>> constructTree(List<Edge> edges) {
        Map<Integer, List<Integer>> tree = new HashMap<>();
        for (Edge edge : edges) {
            int from = edge.p1;
            int to = edge.p2;

            tree.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            tree.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        }
        return tree;
    }

    public int Distance(int p1, int p2) {
        int e1x1 = stNodes.get(p1).x;
        int e1y1 = stNodes.get(p1).y;
        int e2x1 = stNodes.get(p2).x;
        int e2y1 = stNodes.get(p2).y;

        int dx = Math.abs(e1x1 - e2x1);
        int dy = Math.abs(e1y1 - e2y1);


        return (int) (Math.sqrt(dx * dx + dy * dy) + 0.5);
    }

    private Edge findInsertEdge(List<Edge> edges, int index, int delDist) {
        // 创建一个优先队列，其中边按差异值升序排列
        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                double del1 = Distance(e1.p1, e1.p2) + delDist;
                double add1 = Distance(index, e1.p1) + Distance(index, e1.p2);
                double diff1 = add1 - del1;

                double del2 = Distance(e2.p1, e2.p2) + delDist;
                double add2 = Distance(index, e2.p1) + Distance(index, e2.p2);
                double diff2 = add2 - del2;

                return Double.compare(diff1, diff2);
            }
        });
//
        // 将所有边添加到优先队列中
        for (Edge edge : edges) {
            edgeQueue.add(edge);
        }
//
//        int Index = rand.nextInt(edgeQueue.size());
//        if (rand.nextDouble() < 1) {
//            return edges.get(Index);
//        }
        // 从优先队列中获取差异值最小的边
        return edgeQueue.peek();
    }


    private List<Edge> greedyRemoveAddEdges(List<Edge> edges) {
        List<Edge> newEdges = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            newEdges.add(new Edge(edges.get(i).p1, edges.get(i).p2, edges.get(i).direction));
        }

        //randomly select an edge
        int index = rand.nextInt(edges.size());
        int n1 = edges.get(index).p1;
        int n2 = edges.get(index).p2;

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        boolean[] flag = new boolean[prob.getNodeNum()];

        //search the nodes which are in the same subtree with n1
        int[] idx = new int[eLists.length];
        try {
            for (Edge e : edges) {
                if ((e.p1 == n1 && e.p2 == n2) || (e.p1 == n2 && e.p2 == n1)) {
                } else {
                    Solution.eLists[e.p1][idx[e.p1]++] = e.p2;
                    Solution.eLists[e.p2][idx[e.p2]++] = e.p1;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
            System.exit(1);
        }

        int[] s = new int[Solution.eLists.length];
        int top = 0;
        s[0] = n1;
        flag[n1] = true;
        while (top >= 0) {
            int n = s[top--];
            list1.add(n);
            for (int i = 0; i < idx[n]; i++) {//int node : eLists[n]) {
                int node = eLists[n][i];
                if (!flag[node]) {
                    s[++top] = node;
                    flag[node] = true;
                }
            }
        }

        //Search the nodes which are in the same subtree with n2
        for (int i = 0; i < flag.length; i++) {
            if (!flag[i]) {
                list2.add(i);
            }
        }
        int best1 = n1, best2 = n2;
        for (Edge e :  prob.getOrderedEdges()) {//
            if ((!flag[e.p1] && flag[e.p2]) || (flag[e.p1] && !flag[e.p2])) {
                best1 = e.p1;
                best2 = e.p2;
                if (rand.nextDouble() < Simulations.greedyAddProb) break;
            }
        }
//        int best1 = list1.get(rand.nextInt(list1.size()));
//        int best2 = list2.get(rand.nextInt(list2.size()));
        newEdges.get(index).p1 = best1;
        newEdges.get(index).p2 = best2;
        newEdges.get(index).direction = rand.nextInt(decodeTypeNum);
        return newEdges;
    }


    private List<Edge> greedyAddRemoveEdges(List<Edge> edges) {
        List<Edge> newEdges = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            newEdges.add(new Edge(edges.get(i).p1, edges.get(i).p2, edges.get(i).direction));
        }

        //Greedily select a new edge
        Edge e = Graphic.findNewEdge(newEdges);
        e.direction = rand.nextInt(decodeTypeNum);
        Graphic.addEdge(newEdges, e);
        return newEdges;
    }


    Solution hillClimbFlip() {
        Solution s = make(edges);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < s.edges.length && !improved; i++) {
                int temp = s.edges[i].direction;
                for (int d = 1; d < decodeTypeNum; d++) {
                    s.edges[i].direction = (temp + d) % decodeTypeNum;
                    Solution ns = make(s.edges);
                    if (ns.cost < s.cost) {
                        improved = true;
                        s = ns;
                        break;
                    }
                }
                if (!improved) {//restore
                    s.edges[i].direction = temp;
                }
            }
        }
        return s;
    }


    public void decode() {
        stNodes = prob.getNodes();
        sEdge = new ArrayList<>();
        int nodeIndex = stNodes.size();
        for (int i = 0; i < edges.length; i++) {
            int n1 = edges[i].p1;
            int n2 = edges[i].p2;
            if (stNodes.get(n1).x != stNodes.get(n2).x && stNodes.get(n1).y != stNodes.get(n2).y) {
                //create a steiner point
                int[] newNode = new int[2];
                if (edges[i].direction == 0) {
                    newNode[0] = stNodes.get(n1).x;
                    newNode[1] = stNodes.get(n2).y;
                } else {
                    newNode[0] = stNodes.get(n2).x;
                    newNode[1] = stNodes.get(n1).y;
                }
                stNodes.add(new Point(newNode[0], newNode[1]));
                sEdge.add(new Edge(n1, nodeIndex, 0));
                sEdge.add(new Edge(nodeIndex, n2, 0));
                nodeIndex++;
            } else {
                sEdge.add(new Edge(n1, n2, 0));
            }
        }

        //to guarantee that the first node of an edge is the min one
        for (int i = 0; i < sEdge.size(); i++) {
            int n1 = sEdge.get(i).p1;
            int n2 = sEdge.get(i).p2;
            if (stNodes.get(n2).x < stNodes.get(n1).x || stNodes.get(n2).y < stNodes.get(n1).y) {
                //swap
                sEdge.get(i).p1 = n2;
                sEdge.get(i).p2 = n1;
            }
//			System.out.println(sNode.get(sEdge.get(i)[0])[0]+","+sNode.get(sEdge.get(i)[0])[1] + "-" +
//					sNode.get(sEdge.get(i)[1])[0]+","+sNode.get(sEdge.get(i)[1])[1]);
        }


        cost = quickAdjustedCost();
        //cost = costAfterRemoveRepeatSegements();
    }

    public int quickAdjustedCost() {
        List<Edge>[] added = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
        for (int i = 0; i < sEdge.size(); i++) {
            Edge e = sEdge.get(i);
            if (stNodes.get(e.p1).y == stNodes.get(e.p2).y) {
                added[0].add(e);
            } else {
                added[1].add(e);
            }
        }

        Collections.sort(added[0], new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return stNodes.get(e1.p1).y - stNodes.get(e2.p1).y;
            }
        });

        Collections.sort(added[1], new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return stNodes.get(e1.p1).x - stNodes.get(e2.p1).x;
            }
        });


        int removed = 0;
        for (int direc = 0; direc < added.length; direc++) {
            for (int k = 1; k < added[direc].size(); k++) {
                Edge e = added[direc].get(k);
                int e1x = stNodes.get(e.p1).x;
                int e1y = stNodes.get(e.p1).y;
                int e2x = stNodes.get(e.p2).x;
                int e2y = stNodes.get(e.p2).y;
                int max = 0;
                for (int i = k - 1; i >= 0; i--) {
                    Edge ae = added[direc].get(i);
                    int ae1x = stNodes.get(ae.p1).x;
                    int ae1y = stNodes.get(ae.p1).y;
                    int ae2x = stNodes.get(ae.p2).x;
                    int ae2y = stNodes.get(ae.p2).y;

                    if (direc == 0 && e1y != ae1y) break;
                    if (direc == 1 && e1x != ae1x) break;

                    if (e1x == ae1x && e1x == e2x && ae1x == ae2x) {
                        if (e1y >= ae1y && e2y <= ae2y) {
                            int d = e2y - e1y;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1y > ae1y && e1y < ae2y && e2y > ae2y) {
                            int d = ae2y - e1y;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1y < ae1y && e2y > ae1y && e2y < ae2y) {
                            int d = e2y - ae1y;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1y < ae1y && e2y > ae2y) {
                            int d = ae2y - ae1y;
                            if (d > max) {
                                max = d;
                            }
                        }
                    } else if (e1y == ae1y && e1y == e2y && ae1y == ae2y) {
                        if (e1x >= ae1x && e2x <= ae2x) {
                            int d = e2x - e1x;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1x > ae1x && e1x < ae2x && e2x > ae2x) {
                            int d = ae2x - e1x;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1x < ae1x && e2x > ae1x && e2x < ae2x) {
                            int d = e2x - ae1x;
                            if (d > max) {
                                max = d;
                            }
                        } else if (e1x < ae1x && e2x > ae2x) {
                            int d = ae2x - ae1x;
                            if (d > max) {
                                max = d;
                            }
                        }
                    }
                }
                removed += max;
            }
        }

        int cost = 0;
        for (int direc = 0; direc < added.length; direc++) {
            for (Edge e : added[direc]) {
                cost += stNodes.get(e.p2).x - stNodes.get(e.p1).x + stNodes.get(e.p2).y - stNodes.get(e.p1).y;
            }
        }
        return cost - removed;
    }


    public int costAfterRemoveRepeatSegements() {
        List<Point[]> remain = new ArrayList<>();
        List<Point[]> added = new ArrayList<>();
        Edge e1 = sEdge.get(0);
        added.add(new Point[]{stNodes.get(e1.p1), stNodes.get(e1.p2)});

        for (int i = 1; i < sEdge.size(); i++) {
            e1 = sEdge.get(i);
            remain.add(new Point[]{stNodes.get(e1.p1), stNodes.get(e1.p2)});
        }

        while (!remain.isEmpty()) {
            Point[] e = remain.remove(0);
            for (int i = 0; i < added.size() && e != null; i++) {
                Point[] ae = added.get(i);
                if (e[0].x == ae[0].x && e[0].x == e[1].x && ae[0].x == ae[1].x) {
                    if (e[0].y >= ae[0].y && e[1].y <= ae[1].y) {
                        e = null;
                    } else if (e[0].y > ae[0].y && e[0].y < ae[1].y && e[1].y > ae[1].y) {
                        //e[0].y = ae[1].y;
                        remain.add(new Point[]{new Point(e[0].x, ae[1].y), new Point(e[1].x, e[1].y)});
                        e = null;
                    } else if (e[0].y < ae[0].y && e[1].y > ae[0].y && e[1].y < ae[1].y) {
                        //e[1].y = ae[0].y;
                        remain.add(new Point[]{new Point(e[0].x, e[0].y), new Point(e[1].x, ae[0].y)});
                        e = null;
                    } else if (e[0].y < ae[0].y && e[1].y > ae[1].y) {
                        remain.add(new Point[]{new Point(e[0].x, e[0].y), new Point(e[0].x, ae[0].y)});
                        remain.add(new Point[]{new Point(e[0].x, ae[1].y), new Point(e[0].x, e[1].y)});
                        e = null;
                    }
                } else if (e[0].y == ae[0].y && e[0].y == e[1].y && ae[0].y == ae[1].y) {
                    if (e[0].x >= ae[0].x && e[1].x <= ae[1].x) {
                        e = null;
                    } else if (e[0].x > ae[0].x && e[0].x < ae[1].x && e[1].x > ae[1].x) {
                        //e[0].x = ae[1].x;
                        //remain.add(e);
                        remain.add(new Point[]{new Point(ae[1].x, e[0].y), new Point(e[1].x, e[1].y)});
                        e = null;
                    } else if (e[0].x < ae[0].x && e[1].x > ae[0].x && e[1].x < ae[1].x) {
                        //e[1].x = ae[0].x;
                        //remain.add(e);
                        remain.add(new Point[]{new Point(e[0].x, e[0].y), new Point(ae[0].x, ae[0].y)});
                        e = null;
                    } else if (e[0].x < ae[0].x && e[1].x > ae[1].x) {
                        remain.add(new Point[]{new Point(e[0].x, e[0].y), new Point(ae[0].x, e[0].y)});
                        remain.add(new Point[]{new Point(ae[1].x, e[1].y), new Point(e[1].x, e[1].y)});
                        e = null;
                    }
                }
            }

            if (e != null) {
                added.add(e);
            }
        }


        int cost = 0;
        for (Point[] e : added) {
            cost += e[1].x - e[0].x + e[1].y - e[0].y;
        }

        return cost;
    }



    public void saveSteiner(String fileName) {
        //decode();
        try {
            List<Edge> allEdge = new ArrayList<>();
            allEdge.addAll(stEdgeDire000);
            allEdge.addAll(stEdgeDire045);
            allEdge.addAll(stEdgeDire090);
            allEdge.addAll(stEdgeDire135);
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
            printWriter.write((edges.length + 1) + "\t0\t0\t0\t0\n");
            int nodeNum = stNodes.size();
            printWriter.write(nodeNum + "\t0\t0\t0\t0\n");
            for (int i = 0; i < nodeNum; i++) {
                printWriter.write(i + "\t" + stNodes.get(i).x + "\t" + stNodes.get(i).y + "\t0\t0\n");
            }

            for (int i = 0; i < allEdge.size(); i++) {
                printWriter.write(stNodes.get(allEdge.get(i).p1).x + "\t" + stNodes.get(allEdge.get(i).p1).y + "\t");
                printWriter.write(stNodes.get(allEdge.get(i).p2).x + "\t" + stNodes.get(allEdge.get(i).p2).y + "\t" + allEdge.get(i).direction + "\n");
            }

            printWriter.write(cost + "\t0\t0\t0\t0\n");

            printWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String toString() {
        String str = ""; //cost
        for (int i = 0; i < edges.length; i++) {
            str += edges[i].p1 + "\t" + edges[1].p2 + "\t" + edges[i].direction + "\n";
        }
        str += "\n";
        return str;
    }

    public int compareTo(Solution s) {
        if (this.cost < s.cost) {
            return -1;
        } else if (this.cost == s.cost) {
            return 0;
        } else {
            return 1;
        }
    }

}
