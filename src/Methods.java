
import java.io.File;
import java.util.*;

public class Methods {

    public static Solution scatterSearch() {

        final int POP_SIZE = Simulations.popSize;
        final int ARCHIVE_SIZE = Simulations.ARCHIVE_SIZE;
        Solution.init();
        Solution[] pop = new Solution[POP_SIZE]; //
        Solution[] archive = new Solution[ARCHIVE_SIZE];//Personal best solution
        Solution gBest = null;
        Solution s = Solution.make(1);

        int archSize = 0;
        archive[archSize] = s;
        archSize++;
        while (archSize < archive.length) {
            s = Solution.make(1);

            if (Arrays.binarySearch(Arrays.copyOf(archive, archSize), s) < 0) {
                archive[archSize] = s;
                archSize++;
                Arrays.sort(archive, 0, archSize);
            } else {
                boolean flag = true;
                for (int i = 0; i < archSize; i++) {
                    if (archive[i].getEdges().containsAll(s.getEdges())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    archive[archSize] = s;
                    archSize++;
                    Arrays.sort(archive, 0, archSize);
                }
            }
        }
        double baseThreshold = 0.2;
        double alpha = 0.06;

        int popSize = 0;
        boolean popDiverse = false;
        while (popSize < POP_SIZE) {
            double adaptiveThreshold = baseThreshold + alpha * popSize;
            s = Solution.make(0);
            boolean flag = true;
            for (int i = 0; i < archive.length; i++) {
                if (archive[i].getEdges().containsAll(s.getEdges())) {
                    flag = false;
                    break;
                }
            }
            //if not in archive
            if (flag) {
                if (popSize == 0) {
                    pop[popSize] = s;
                    popSize++;
                } else {
                    popDiverse = checkHammingDistance(s, pop, adaptiveThreshold);
                    if (popDiverse) {
                        pop[popSize] = s;
                        popSize++;
                    }
                }
            }
        }

        Arrays.sort(archive);

        gBest = archive[0];
        int fet = 0;
        for (int q = 0; fet < Simulations.MAX_FET; q++) {
            for (int i = 0; i < pop.length; i++) {
                int idx = rand.nextInt(archive.length);
                Solution[] ss = make2ChildrenStage(pop[i], archive[idx]);
                fet += ss.length;
                if (ss[0].getCost() < ss[1].getCost()) {
                    s = ss[0];
                } else {
                    s = ss[1];

                }

                boolean numP = !contains(pop, s);
                boolean numA = true;
                if (Arrays.binarySearch(archive, s) >= 0) {
                    numA = !contains(archive, s);
                }


                //  s not in pop and archive
                if (numP && s.getCost() < archive[archive.length - 1].getCost() && numA) {
                    int index = archive.length - 1;
                    while (index > 0 && archive[index - 1].getCost() > s.getCost()) {
                        archive[index] = archive[index - 1];
                        index--;

                    }
                    archive[index] = s;


                } else {
                    if (numP && numA){
                        pop[i] = s;
                    }
                }
            }

            // 更新全局最优解
            if (archive[0].getCost() < gBest.cost) {

                gBest = archive[0];
                gBest.lastImprove = q + 1;

            }
//            if (cost == gBest.cost) {
//                count++;
//             //   System.out.println(count);
//                if (count == 1000) {
//                    break;
//                }
//            } else {
//                count = 0;
//                cost = gBest.cost;
//            }
        }
        s = gBest;
        s.lastImprove = gBest.lastImprove;

        return s;
    }


    public static double calculateHammingDistance(Solution sol1, Solution sol2) {
        List<Edge> edges1 = sol1.getEdges();
        List<Edge> edges2 = sol2.getEdges();
        Set<Edge> set2 = new HashSet<>(edges2);

        if (edges1.size() != edges2.size()) {
            throw new IllegalArgumentException("Solutions have different edge counts.");
        }


        int differences = 0;
        for (Edge edge : edges1) {
            if (!set2.contains(edge.hashCode())) {
                differences++;
            }
        }

        return (double) differences / edges1.size();
    }



    public static boolean checkHammingDistance(Solution newSolution, Solution[] R2, double threshold) {
        for (Solution existingSolution : R2) {
            if (existingSolution == null) break;
            double distance = calculateHammingDistance(newSolution, existingSolution);
            if (distance < threshold) {
                return false;
            }
        }
        return true;
    }


    private static boolean contains(Solution[] array, Solution target) {
        for (Solution item : array) {
            if (item.getEdges().containsAll(target.getEdges())) {
                return true;
            }
        }
        return false;
    }




    public static Solution searchMin(List<Edge> allEdge, int[][] dists) {
        Solution s = null;
        List<Edge> index = new ArrayList<>();
        int best = Integer.MAX_VALUE;
        Deque<State> stack = new ArrayDeque<>();
        stack.push(new State(new ArrayList<>(), 0)); // 初始状态

        while (!stack.isEmpty()) {
            State state = stack.pop();
            List<Edge> current = state.current;
            int start = state.start;

            if (current.size() == dists.length - 1) {
                if (isValidSpanningTree(current, dists.length)) {
                    s = Solution.make(current);
                    if (s.cost < best) {
                        index.clear();
                        index.addAll(current);
                        best = s.cost;
                    }
                }
                continue;
            }

            for (int i = start; i < allEdge.size(); i += 2) {
                for (int j = 0; j < 2; j++) {
                    List<Edge> nextEdges = new ArrayList<>(current);
                    nextEdges.add(allEdge.get(i + j));
                    stack.push(new State(nextEdges, i + 2));
                }
            }
        }

        return Solution.make(index);
    }

    private static int find(int[] parent, int i) {
        if (parent[i] == i)
            return i;
        return parent[i] = find(parent, parent[i]);
    }

    private static void union(int[] parent, int[] rank, int x, int y) {
        int xroot = find(parent, x);
        int yroot = find(parent, y);

        if (rank[xroot] < rank[yroot])
            parent[xroot] = yroot;
        else if (rank[xroot] > rank[yroot])
            parent[yroot] = xroot;
        else {
            parent[yroot] = xroot;
            rank[xroot]++;
        }
    }

    private static boolean isValidSpanningTree(List<Edge> edgeSet, int V) {
        int[] parent = new int[V];
        int[] rank = new int[V];
        for (int i = 0; i < V; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        for (Edge edge : edgeSet) {
            int x = find(parent, edge.p1);
            int y = find(parent, edge.p2);

            if (x == y)
                return false;

            union(parent, rank, x, y);
        }

        // 检查连通性
        int root = find(parent, 0);
        for (int i = 1; i < V; i++) {
            if (find(parent, i) != root)
                return false;
        }

        return true;
    }


    private static class State {
        List<Edge> current;
        int start;

        State(List<Edge> current, int start) {
            this.current = current;
            this.start = start;
        }
    }


    private static Solution[] make2ChildrenStage(Solution p1, Solution p2) {
        //return new Solution[]{new SolutionOrderBit(cPer[0], cRow[0]), new SolutionOrderBit(cPer[1], cRow[1])};
        List<Edge> edges1 = p1.getEdges();
        List<Edge> edges2 = p2.getEdges();
        List<Edge> extra1 = Graphic.findExtra(edges1, edges2);
        List<Edge> extra2 = Graphic.findExtra(edges2, edges1);

        int num = extra1.size();
        if (num == 0 || rand.nextDouble() > Simulations.cxProb) {//不进行交叉，直接对每个父代的边集进行贪婪扰动
            edges1 = p1.greedyPerturbEdgesStage(edges1);
            edges2 = p1.greedyPerturbEdgesStage(edges2);
        } else {
            int n = rand.nextInt(num) + 1;
            while (n-- > 0) {
                Edge e = extra2.remove(rand.nextInt(extra2.size()));
                Graphic.addEdge(edges1, e);
            }

            n = rand.nextInt(num) + 1;
            while (n-- > 0) {
                Edge e = extra1.remove(rand.nextInt(extra1.size()));
                Graphic.addEdge(edges2, e);
            }
            //mutation
            if (rand.nextDouble() < Simulations.mutaionProb) {
                edges1 = p1.greedyPerturbEdgesStage(edges1);
            }
            if (rand.nextDouble() < Simulations.mutaionProb) {
                edges2 = p1.greedyPerturbEdgesStage(edges2);
            }

        }




        return new Solution[]{Solution.make(edges1), Solution.make(edges2)};
    }

    private static Random rand = new Random();

}
