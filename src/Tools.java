import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Tools {
    /**
     * 
     * @param arr
     * @return
     */
    public static double sum(double[] arr) {
        double sum = 0;
        for (double num : arr) {
            sum += num;
        }
        return sum;
    }

    /**
     *
     * @param arr
     * @return
     */
    public static double mean(double[] arr) {
        return sum(arr) / arr.length;
    }

    /**
     * Mode
     *
     * @param arr
     * @return
     */
    public static double mode(double[] arr) {
        Map<Double, Integer> map = new HashMap<Double, Integer>();
        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                map.put(arr[i], map.get(arr[i]) + 1);
            } else {
                map.put(arr[i], 1);
            }
        }
        int maxCount = 0;
        double mode = -1;
        Iterator<Double> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            double num = iter.next();
            int count = map.get(num);
            if (count > maxCount) {
                maxCount = count;
                mode = num;
            }
        }
        return mode;
    }

    /**
     * Median
     *
     * @param arr
     * @return
     */
    public static double median(double[] arr) {
        double[] tempArr = Arrays.copyOf(arr, arr.length);
        Arrays.sort(tempArr);
        if (tempArr.length % 2 == 0) {
            return (tempArr[tempArr.length >> 1] + tempArr[(tempArr.length >> 1) - 1]) / 2;
        } else {
            return tempArr[(tempArr.length >> 1)];
        }
    }


    /**
     * Middle range
     *
     * @param arr
     * @return
     */
    public static double midrange(double[] arr) {
        double max = arr[0], min = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return (min + max) / 2;
    }

    /**
     * ���ķ�λ��
     *
     * @param arr
     * @return ��������ķ�λ��������
     */
    public static double[] quartiles(double[] arr) {
        double[] tempArr = Arrays.copyOf(arr, arr.length);
        Arrays.sort(tempArr);
        double[] quartiles = new double[3];
        // �ڶ��ķ�λ������λ����
        quartiles[1] = median(tempArr);
        // �����������ķ�λ��
        if (tempArr.length % 2 == 0) {
            quartiles[0] = median(Arrays.copyOfRange(tempArr, 0, tempArr.length / 2));
            quartiles[2] = median(Arrays.copyOfRange(tempArr, tempArr.length / 2, tempArr.length));
        } else {
            quartiles[0] = median(Arrays.copyOfRange(tempArr, 0, tempArr.length / 2));
            quartiles[2] = median(Arrays.copyOfRange(tempArr, tempArr.length / 2 + 1, tempArr.length));
        }
        return quartiles;
    }

    /**
     * �󼫲�
     *
     * @param arr
     * @return
     */
    public static double range(double[] arr) {
        double max = arr[0], min = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return max - min;
    }

    /**
     * ���ķ�λ������
     *
     * @param arr
     * @return
     */
    public static double quartilesRange(double[] arr) {
        return range(quartiles(arr));
    }

    /**
     * ��ضϾ�ֵ
     *
     * @param arr ��ֵ����
     * @param p   �ض���p������p��ֵΪ10����ض�20%����10%����10%��
     * @return
     */
    public static double trimmedMean(double[] arr, int p) {
        int tmp = arr.length * p / 100;
        double[] tempArr = Arrays.copyOfRange(arr, tmp, arr.length + 1 - tmp);
        return mean(tempArr);
    }

    /**
     * �󷽲�
     *
     * @param arr
     * @return
     */
    public static double variance(double[] arr) {
        double variance = 0;
        double sum = 0, sum2 = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            sum2 += arr[i] * arr[i];
        }
        variance = sum2 / arr.length - (sum / arr.length) * (sum / arr.length);
        return variance;
    }

    /**
     * �����ƽ��ƫ��(AAD)
     *
     * @param arr
     * @return
     */
    public static double absoluteAverageDeviation(double[] arr) {
        double sum = 0;
        double mean = mean(arr);
        for (int i = 0; i < arr.length; i++) {
            sum += Math.abs(arr[i] - mean);
        }
        return sum / arr.length;
    }

    /**
     * ����λ������ƫ��(MAD)
     *
     * @param arr
     * @return
     */
    public static double medianAbsoluteDeviation(double[] arr) {
        double[] tempArr = new double[arr.length];
        double median = median(arr);
        for (int i = 0; i < arr.length; i++) {
            tempArr[i] = Math.abs(arr[i] - median);
        }
        return median(tempArr);
    }

    /**
     * ���׼��
     * @param arr
     * @return
     */
    public static double standardDevition(double[] arr) {
        double sum = 0;
        double mean = mean(arr);
        for (int i = 0; i < arr.length; i++) {
            sum += Math.sqrt((arr[i] - mean) * (arr[i] - mean));
        }
        return (sum / (arr.length - 1));
    }

	public static double standardDevition1(double[][] arr, double total) {
		double[] newArr = new double[20];
		double c = 0;
		for (int i = 0; i < 20; i++){
			for (int j = 0; j < arr.length; j++){
				c += arr[j][i];
			}
			newArr[i] = c;
			c = 0;
		}
		double sum = 0;
		double mean = total/20;
		for (int i = 0; i < newArr.length; i++) {
			sum += Math.sqrt((newArr[i] - mean) * (newArr[i] - mean));
		}
		return (sum / (newArr.length - 1));
	}
    
    public static int[] permutation(double[] rk, int length) {
    	int[] p = new int[length];
    	List<RandKey> list = new LinkedList<>();
    	for (int i = 0; i < length; i++) {
    		list.add(new RandKey(rk[i], i));
    	}
    	Collections.sort(list);
    	for (int i = 0; i < list.size(); i++) {
    		p[i] = list.get(i).key;
        }
    	return p;
    }
    
    public static int[] randomPermutation(int from, int to) {
    	List<Integer> list = new ArrayList<>();
     	for (int i = from; i < to; i++) {
    		list.add(i);
    	}
    	Collections.shuffle(list);
    	int[] p = new int[list.size()];
    	for (int i = 0; i < list.size(); i++) {
    		p[i] = list.get(i);
    	}
    	return p;
    }
    
    
    /**
     * The exchange mutation operator (Banzhaf 1990) randomly selects two elements and exchanges them.
     * Banzhaf, W. (1990). The ��Molecular�� Traveling Salesman. Biological Cybernetics 64: 7ÿ14.
     * 
     * @param p
     * @param times
     */
    public static void swap(int[] p, int times) {
    	for (int i = 0; i < times; i++) {
			int x = rand.nextInt(p.length);
			int y = rand.nextInt(p.length);
			while (x == y) {
				y = rand.nextInt(p.length);
			}
			int temp = p[x];
			p[x] = p[y];
			p[y] = temp;
		}    	
    }
    
    /**
     * The insertion mutation operator (Fogel 1988; Michalewicz 1992) randomly chooses a element, removes it and inserts it in a randomly selected place.
     * Fogel, D. B. (1988). An Evolutionary Approach to the Traveling Salesman Problem. Biological Cybernetics 60: 139ÿ144.
     * Michalewicz, Z. (1992). Genetic Algorithms + Data Structures = Evolution Programs. Berlin Heidelberg: Springer Verlag.
     * 
     * @param p
     * @param times
     */
   
    public static void insert(int[] p, int times) {
    	for (int i = 0; i < times; i++) {
			int x = rand.nextInt(p.length);
			int y = rand.nextInt(p.length);
			while (x == y) {
				y = rand.nextInt(p.length);
			}
			int temp = p[x];
			while (x < y) {
				p[x] = p[x+1];
				x++;
			}
			while (x > y) {
				p[x] = p[x-1];
				x--;
			}
			p[y] = temp;
		}    	
    }
    
    public static void inverse(int[] p) {
    	int x = rand.nextInt(p.length);
    	int y = rand.nextInt(p.length);
    	while (x == y) {
    		y = rand.nextInt(p.length);
    	}
    	if ( x > y ) {
    		int temp = x;
    		x = y;
    		y = temp;
    	}
     	while (x < y) {
     		int temp = p[x];
    		p[x] = p[y];
    		p[y] = temp;
    		x++;
    		y--;
    	}
     }
    
    public static void move(int[] p) {
    	int x = rand.nextInt(p.length);
    	int y = rand.nextInt(p.length);
    	while (x == y) {
    		y = rand.nextInt(p.length);
    	}
    	if ( x > y ) {
    		int temp = x;
    		x = y;
    		y = temp;
    	}
    	
    	int k = rand.nextInt(p.length);
    	int[] t = p.clone();
    	boolean[] flagIndex = new boolean[p.length];
      	for (int i = x; i <= y; i++) {
     		p[k] = t[i];
     		flagIndex[k] = true;
     		k = (k+1) % p.length;
     	}
     	
     	k = 0;
     	for (int i = 0; i < p.length; i++) {
     		if (i >= x && i <= y) continue;
     		while (flagIndex[k]) {
     			k++;
     		}
     		p[k] = t[i];
     		k++;
     	}
     }
    
    /**
     * The scramble mutation operator (Syswerda 1991) selects a random section and scrambles the elements in it
     * Syswerda, G. (1991). Schedule Optimization Using Genetic Algorithms. In Davis, L. (ed.) Handbook of Genetic Algorithms, 332ÿ349. New York: Van Nostrand Reinhold.
     * 
     * @param p
     */
    public static void scramble(int[] p) {
    	int x = rand.nextInt(p.length);
    	int y = rand.nextInt(p.length);
    	while (x == y) {
    		y = rand.nextInt(p.length);
    	}
    	
    	if (x > y) {
    		int t = x;
    		x = y;
    		y = t;
    	}
    	
    	List<Integer> s = new LinkedList<>();
    	for (int i = x; i < y+1; i++) {
    		s.add(p[i]);
    	}
    	Collections.shuffle(s);
    	for (int i = 0; i < s.size(); i++) {
    		p[x+i] = s.get(i);
    	}
    }
    
    /**
     * 
     * @param fitness
     * @param num
     * @return
     */
	public static int[] rouletteWheel(double[] fitness, int num) {
		double[] fites = new double[fitness.length];
		for (int i=0; i<fitness.length; i++) {
			fites[i] = fitness[i];
			if ( i != 0) {
				fites[i] += fites[i-1];
			}
		}
		 return rw(fites, num);
	}
	
    
    /**
     * 
     * @param fitness  accumulated fitness
     * @param num
     * @return
     */
    private static int[] rw(double[] fitness, int num) {
		int[] ids = new int[num];
		for (int id = 0; id < ids.length; id++) {
			double fit = rand.nextDouble()*fitness[fitness.length-1];
			for (int i=0; i<fitness.length; i++) {
				if (fit < fitness[i]) {
					ids[id] = i;
					break;
				}
			}
		}
		return ids;
    }
    
    /**
     * partially-mapped Crossover
     * Goldberg, D. E. & Lingle, Jr., R. (1985). Alleles, Loci and the TSP. In Grefenstette, J. J. (ed.) Proceedings of the First International Conference on Genetic Algorithms and Their Applications, 154�C159. Hillsdale, New Jersey: Lawrence Erlbaum.
     *It passes on ordering and value information from the parent tours to the offspring tours.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static int[][] partiallyMappedCrossover(int[] p1, int[] p2) {
    	
    	int[] c1 = new int[p1.length];
    	int[] c2 = new int[p2.length];
    	
    	int pos1 = rand.nextInt(p1.length);
    	int pos2 = rand.nextInt(p1.length);
    	
    	if (pos1 > pos2) {
    		int temp = pos1;
    		pos1 = pos2; 
    		pos2 = temp;
    	}
    	
    	List<Integer> inc1 = new ArrayList<>();
    	List<Integer> inc2 = new ArrayList<>();   	
    	for (int i = pos1; i <= pos2; i++) {
    		c1[i] = p2[i];
    		inc1.add(c1[i]);
    		c2[i] = p1[i];
    		inc2.add(c2[i]);
    	}
    	
        for (int i = 0; i < c1.length; i++) {
        	if (i >= pos1 && i<= pos2) {
        		continue;
        	}
        	int e = p1[i];
        	int idx = inc1.indexOf(e);
        	while (idx != -1) {//mapping
        		e = inc2.get(idx);
        		idx = inc1.indexOf(e);
        	}
        	c1[i] = e;
        }
        
        for (int i = 0; i < c2.length; i++) {
        	if (i >= pos1 && i<= pos2) {
        		continue;
        	}
        	int e = p2[i];
        	int idx = inc2.indexOf(e);
        	while (idx != -1) {//mapping
        		e = inc1.get(idx);
        		idx = inc2.indexOf(e);
        	}
        	c2[i] = e;
        }
 
    	return new int[][] {c1, c2};		
    }
    
    /**
     * Order Crossover (OX1)
     * Davis, L. (1985). Applying Adaptive Algorithms to Epistatic Domains. Proceedings of the International Joint Conference on Artificial Intelligence, 162ÿ164.
     * The OX1 exploits the property that the order of element (not their positions) is important.
     * 
     * @param p1
     * @param p2
     * @return
     */
    
    public static int[][] orderCrossover(int[] p1, int[] p2) {
    	
    	int[] c1 = new int[p1.length];
    	int[] c2 = new int[p2.length];
    	
    	int pos1 = rand.nextInt(p1.length);
    	int pos2 = rand.nextInt(p1.length);
    	
    	if (pos1 > pos2) {
    		int temp = pos1;
    		pos1 = pos2; 
    		pos2 = temp;
    	}
    	
    	for (int i = pos1; i <= pos2; i++) {
    		c1[i] = p1[i];
    		c2[i] = p2[i];
    	}
    	
    	List<Integer> restOfc1 = new ArrayList<>();
    	List<Integer> restOfc2 = new ArrayList<>();
    	for (int i = 0; i < pos1; i++) {
    		restOfc1.add(p1[i]);
    		restOfc2.add(p2[i]);
    	}
    	for (int i = pos2+1; i < p1.length; i++) {
    		restOfc1.add(p1[i]);
    		restOfc2.add(p2[i]);
    	}
    	
        int idx = (pos2+1) % c1.length;
        for (int i = 0; i < p2.length; i++) {
        	int pos = (pos2+1+i) % p2.length;
        	if (restOfc1.contains(p2[pos])) {
        		c1[idx] = p2[pos];
        		idx = (idx+1) % c1.length;
        	}
        }
        
        idx = (pos2+1) % c2.length;
        for (int i = 0; i < p1.length; i++) {
        	int pos = (pos2+1+i) % p1.length;
        	if (restOfc2.contains(p1[pos])) {
        		c2[idx] = p1[pos];
        		idx = (idx+1) % c2.length;
        	}
        }
    	return new int[][] {c1, c2};		
    }
    
    public static int[][] twoPointCrossover(int[] p1, int[] p2) {
    	int[] c1 = p1.clone();
    	int[] c2 = p2.clone();

    	int pos1 = rand.nextInt(p1.length);
    	int pos2 = rand.nextInt(p1.length);

    	if (pos1 > pos2) {
    		int temp = pos1;
    		pos1 = pos2; 
    		pos2 = temp;
    	}

    	for (int i = pos1; i < pos2; i++) {
    		c1[i] = p2[i];
    		c2[i] = p1[i];
    	}
    	return new int[][] {c1, c2};		
    }

	public static int  euclideanDistance (int[] p1, int[] p2) {
		double sum = 0.0;
		for (int i = 0; i < p1.length; i++) {
			sum += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		}
		return (int)(Math.sqrt(sum) + 0.5);
	}

    private static Random rand = new Random();
}

class RandKey implements Comparable<RandKey>{
	double value;
	int key;
	
	RandKey(double v, int k) {
		value = v;
		key = k;
	}
	@Override
	public int compareTo(RandKey arg) {
		// TODO Auto-generated method stub
		if (value - arg.value > 0) {
			return 1;
		} else if (value == arg.value) {
			return 0;
		} else {
			return -1;
		}
	}
	
	
}