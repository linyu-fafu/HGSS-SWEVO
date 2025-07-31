import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author kai Yang
 */
public class Simulations {


    public static void main(String[] args) {
        String filePath = "";//(new File("")).getAbsolutePath() + "/../";
        filePath = (new File("")).getAbsolutePath() + "\\..\\datas\\GEO\\";

        System.out.println("\n" + Simulations.getParaSetting());
        testPerformance(filePath, Simulations.TIMES);

    }

    private static double[] testPerformance(String filePath, final int TIMES) {
        File dir = new File(filePath);
        File[] files = dir.listFiles();
        String pathName = filePath.substring(filePath.lastIndexOf("\\", filePath.length() - 2)).substring(1);
        pathName = pathName.substring(0, pathName.length() - 1);
        System.out.println(pathName);
        String fileName = (new File("")).getAbsolutePath() + "/results/Performance/" + pathName + "-";
        fileName += Simulations.getParaSetting();
        fileName += " results.csv";
        List<double[]> results = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            System.out.print(file.getName() + "\n");
            double[] result;
            if (pathName.equals("GEO")) {
                result = run(file.getAbsolutePath(), TIMES);
            } else {
                result = runISPD98(file.getAbsolutePath(), TIMES);
            }
            results.add(result);
            System.out.println();
            System.out.print(file.getName() + "\t");
            for (double d : result) {
                System.out.print(d + "\t");
            }
            System.out.println();
            Simulations.saveFinalResults(fileName, files, results);
        }

        //calculate statistics results
        double[] totals = new double[results.get(0).length];
        for (int i = 0; i < files.length; i++) {
            System.out.println();
            System.out.print(files[i].getName() + "\t");
            for (int j = 0; j < results.get(i).length; j++) {
                System.out.print(results.get(i)[j] + "\t");
                totals[j] += results.get(i)[j];
            }
        }
        System.out.println("\t");
        for (int j = 0; j < totals.length; j++) {
            totals[j] = Math.round(totals[j] / files.length * 1000) / 1000.0;
            System.out.print(totals[j] + "\t");
        }
        return totals; //average data for all files
    }


    private static void saveFinalResults(String fileName, File[] files, List<double[]> results) {
        if (!Simulations.SAVING_FINAL_RESULTS) return;

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
            for (int i = 0; i < results.size(); i++) {
                printWriter.println();
                printWriter.print(files[i].getName());
                for (int j = 0; j < results.get(i).length; j++) {
                    printWriter.print("," + results.get(i)[j]);
                }
            }
            printWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static double[] runISPD98(String fileName, final int TIMES) {
        Problem.readProblem(fileName);
        Problem prob = Problem.get();
        double duration = (new Date()).getTime();//开始时间
        Solution s = null;
        double[][] makespans = new double[prob.getInstanceNum()][Simulations.TIMES];
        int[][] iterations = new int[prob.getInstanceNum()][Simulations.TIMES]; //last improving iteration
        int maxIter = 0;
        int index = 0;
        for (int i = 0; i < Simulations.TIMES; i++) {
            int instNum = prob.getInstanceNum();

            for (int inst = 0; inst < instNum; inst++) {
                prob.set(inst);
                if (prob.getNodeNum() <= 1) {
                    continue;
                } else if (prob.getNodeNum() <= 4) {
                    Solution.init();
                    s = Methods.searchMin(prob.getAllEdge(), prob.getDists());
                    //System.out.println("1");
                } else {
                    if (Simulations.methodType == EMethodType.SS) {
                        s = Methods.scatterSearch();
                        maxIter += s.getLastImprove();
                        index++;

                    } else {
                        System.out.println("Cannot reach here!");
                    }
                }

                makespans[inst][i] += s.getCost(); // - bValue;



            }
        }
        double totals = 0;

        double[] lastArr = new double[Simulations.TIMES];
        double c = 0;
        double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
        double totalIterations = 0;
        for (int i = 0; i < Simulations.TIMES; i++) {
            for (int j = 0; j < makespans.length; j++) {
                c += makespans[j][i];
                //totalIterations += iterations[j][i];
            }
            if (c < min) {
                min = c;
            }
            if (c > max) {
                max = c;
            }
            lastArr[i] = c;
            totals += c;
            c = 0;
        }
        int idx = fileName.lastIndexOf("\\");
        double STD = Tools.standardDevition(lastArr);
        fileName = fileName.substring(idx + 1);
        duration = (new Date()).getTime() - duration;
        duration /= TIMES;
        duration = Math.round(duration / 1000 * 1000) / 1000.0;

        double ave = totals / Simulations.TIMES;
        double median = Tools.median(lastArr);

        double itr = Math.round(maxIter / index); //average last improving iteration
        double[] stat = new double[]{min, max, ave, median, STD, itr, duration};
        return stat;
    }



    private static double[] run(String fileName, final int TIMES) {
        Problem.readProblem(fileName);

        Problem prob = Problem.get();
        double duration = (new Date()).getTime();
        double bValue = Problem.get().getRBestCost();
        if (Simulations.STRU_TYPE == EStructType.OCTAGONAL) {
            bValue = Problem.get().getXBestCost();
        }
        Solution s = null;
        Solution best = null;
        double[] makespans = new double[Simulations.TIMES];
        int stCount = 0;
        int[] iterations = new int[Simulations.TIMES]; //last improving iteration
        for (int i = 0; i < Simulations.TIMES; i++) {
            int instNum = prob.getInstanceNum();
            int maxIter = 0;
            for (int inst = 0; inst < instNum; inst++) {

                //prob.set(inst);
                if (prob.getNodeNum() <= 4) {
                    Solution.init();
                    s = Solution.make();
                    if (prob.getNodeNum() > 2) s = s.hillClimbFlip();
                } else {
                    if (Simulations.methodType == EMethodType.SS) {
                        s = Methods.scatterSearch();
                    } else {
                        System.out.println("Cannot reach here!");

                    }
                }
                //System.out.println(test.toString());
//                if (s.isValid()) System.out.print(" Valid, ");
                makespans[i] += s.getCost(); // - bValue;
                Set<Point> set = new HashSet<>(s.stNodes);
                stCount += (set.size() - prob.getNodeNum());
                if (s.getLastImprove() > maxIter) maxIter = s.getLastImprove();
//                System.out.println(inst + "--" + s.getCost() + "--" + makespans[i]);

            }

//            System.out.print((s instanceof XSolution)?"X-":"R-");
            iterations[i] = maxIter;
            if (Simulations.OUT_INDIVIDUAL_RUNNING_DATA) {
                //System.out.println( i + " -- " + bValue + "," + makespans[i] + "," + iterations[i]);
            }
            if (best == null || s.compareTo(best) > 0) {
                best = s;
            }

        }
        int idx = fileName.lastIndexOf("\\");
        fileName = fileName.substring(idx + 1);
        idx = fileName.indexOf(".");
        fileName = fileName.substring(0, idx);
        best.saveSteiner((new File("")).getAbsolutePath() + "/results/" + methodType + "_" + ((best instanceof XSolution) ? "X_" : "R_") + fileName + ".txt");

        duration = (new Date()).getTime() - duration;
        duration /= TIMES;
        duration = Math.round(duration / 1000 * 1000) / 1000.0;

        double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
        double total = 0;
        double totalIterations = 0;
        for (int i = 0; i < Simulations.TIMES; i++) {
            double mk = makespans[i];
            total += mk;
            if (Math.abs((mk - bValue)) * (1.0 / bValue) * 100 < 1) {
                count++;
            }
            if (mk < min) {
                min = mk;
            }
            if (mk > max) {
                max = mk;
            }
            totalIterations += iterations[i];
        }
        double stNumber = stCount / Simulations.TIMES;
        double ave = total / Simulations.TIMES;
        double median = Tools.median(makespans);
        double STD = Tools.standardDevition(makespans);
        double bpd = Math.abs(Math.round((min - bValue)) * (1.0 / bValue) * 100 * 1000) / 1000.0;
        double wpd = Math.abs(Math.round((max - bValue)) * (1.0 / bValue) * 100 * 1000) / 1000.0;
        double apd = Math.abs(Math.round((ave - bValue)) * (1.0 / bValue) * 100 * 1000) / 1000.0;
        double itr = Math.round(totalIterations / iterations.length * 10) / 10; //average last improving iteration
        //return new double[] {bValue, min, max, ave, bpd, wpd, apd, count, itr, duration};
        double[] stat = new double[]{bValue, min, max, ave, median, STD, bpd, wpd, apd, count / makespans.length, itr, duration, stNumber};
        double[] results = new double[makespans.length + stat.length];
        for (int i = 0; i < stat.length; i++) {
            results[i] = stat[i];
        }
        for (int i = 0; i < makespans.length; i++) {
            results[i + stat.length] = makespans[i];
        }

        return results;
    }

    public static String getParaSetting() {
        String str = (Simulations.STRU_TYPE == EStructType.RECTILINEAR) ? "R-" : "X-";
        str += methodType + "-" + Simulations.PERTURB_TYPE;
        if (Simulations.PERTURB_TYPE == EPerturbType.HYBRID) {
            str += " epsilon=" + epsilonAddRemove;
        } else {
            str += " epsilon=" + epsilonAddRemove;
        }
        str += "-" + Simulations.initType + "-" + Simulations.iniProb + "-" + greedyAddProb + "--";
        if (methodType == EMethodType.SS) {
            str += popSize + "-" + ARCHIVE_SIZE + " cxProb=" + cxProb + " mutaionProb=" + mutaionProb + "MAX_FET=" + MAX_FET + "cxProb=" + cxProb;
        } else {
            System.out.println("Not supported type");
        }

        return str;
    }

    private static EMethodType methodType = EMethodType.SS;
    static final EStructType STRU_TYPE = EStructType.OCTAGONAL;//.OCTAGONAL;//.RECTILINEAR;
    static final EPerturbType PERTURB_TYPE = EPerturbType.HYBRID;//.REMOVE_ADD;//.ADD_REMOVE;
    public static double greedyAddProb = 0.5;//0.5
    public static double epsilonAddRemove = 0.2;//PROBABILITY OF RANDOMLY REMOVE A EDGE WHEN ADDING A NEW EDGE, 0.5
    static final EInitializeType initType = EInitializeType.RANDOM;
    static double iniProb = 0.9;
    static final int TIMES = 20;

    public static final boolean OUT_INDIVIDUAL_RUNNING_DATA = true;
    public static final boolean SAVING_PROCESS_DATA = false;
    public static final boolean SAVING_FINAL_RESULTS = true;
    public static final int MAX_FET = 100 * 1000;//100 * 1000;
    public static int popSize = 6;//5;
    public static int ARCHIVE_SIZE = 2; //
    public static double cxProb = 0.1;//1.0 FOR SS
    public static double mutaionProb = 0.5;//0.5 FOR SS
    public static final double FLIP_PROB = 0.3;//

}
