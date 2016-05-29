import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
/**
 * 
 * @author Bobfeng
 * 
 */

public class ProdIntroduction {
    /**
     * Train dataset filename.
     */
    static String trainName = "";
    /**
     * Test dataset filename.
     */
    static String testName = "";
    /**
     * The training data set.
     */
    private List<Product> train;
    /**
     * The test data set.
     */
    private List<Product> test;
    /**
     * The number of attributes.
     */
    private int dimension = 8;
    /**
     * The labels.
     */
    private String[] labels = { "fail", "success" };
    /**
     * The "K" of KNN.
     */
    private int k = 3;
    /**
     * The start accuracy.
     */
    private double acc = Double.MAX_VALUE;
    /**
     * The test data set in the cross validation.
     */
    ArrayList<ArrayList<Product>> testValid = new ArrayList<ArrayList<Product>>();
    /**
     * The training data set in the cross validation.
     */
    ArrayList<ArrayList<Product>> valid = new ArrayList<ArrayList<Product>>();
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Please input two file location parameters: trainFile and testFile.");
            return;
        }
        trainName = args[0];
        testName = args[1];
        ProdIntroduction p = new ProdIntroduction();
        p.findWeight();
    }


    /**
     * The main method for KNN.
     */
    public void start() {
        loadData();
        normalize(train, test);
        shuffleData(train);
        double[] weight = { 1.0, 9.5, 1.0, 1.0, 1.0, 1.0, 1.0, 5.5 };
        labelByKnn(train, test, weight);
    }

    /**
     * The main method for finding weight.
     */
    public void findWeight() {
        loadData();
        normalize(train, test);
        shuffleData(train);
        dataForValidation(train);
        MyThread thread1 = new MyThread(testValid, valid, 1);
        MyThread thread2 = new MyThread(testValid, valid, 1.5);
        MyThread thread3 = new MyThread(testValid, valid, 2);
        MyThread thread4 = new MyThread(testValid, valid, 2.5);
        MyThread thread5 = new MyThread(testValid, valid, 3);
        MyThread thread6 = new MyThread(testValid, valid, 3.5);
        MyThread thread7 = new MyThread(testValid, valid, 4);
        MyThread thread8 = new MyThread(testValid, valid, 4.5);
        MyThread thread9 = new MyThread(testValid, valid, 5);
        MyThread thread10 = new MyThread(testValid, valid, 5.5);
        MyThread thread11 = new MyThread(testValid, valid, 6);
        MyThread thread12 = new MyThread(testValid, valid, 6.5);
        MyThread thread13 = new MyThread(testValid, valid, 7);
        MyThread thread14 = new MyThread(testValid, valid, 7.5);
        MyThread thread15 = new MyThread(testValid, valid, 8);
        MyThread thread16 = new MyThread(testValid, valid, 8.5);
        MyThread thread17 = new MyThread(testValid, valid, 9);
        MyThread thread18 = new MyThread(testValid, valid, 9.5);
        MyThread thread19 = new MyThread(testValid, valid, 10);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();
        thread11.start();
        thread12.start();
        thread13.start();
        thread14.start();
        thread15.start();
        thread16.start();
        thread17.start();
        thread18.start();
        thread19.start();
    }
    
    /**
     * Following are the relationship of those non-numeric attributes.
     */

    /*
     * Loan Bank_account CD Mortgage Fund 1 0 0.1 0.3 0.2 0 1 0 0 0 0.1 0 1 0.2
     * 0.2 0.3 0 0.2 1 0.1 0.2 0 0.2 0.1 1
     * 
     * Business Professional Student Doctor Other 1 0.2 0.1 0.2 0 0.2 1 0.2 0.1
     * 0 0.1 0.2 1 0.1 0 0.2 0.1 0.1 1 0 0 0 0 0 1
     */
    private static HashMap<String, Double> serviceTypeMap = new HashMap<String, Double>();

    static {
        serviceTypeMap.put("Loan Loan", 1.0);
        serviceTypeMap.put("Loan Bank_Account", 0.0);
        serviceTypeMap.put("Loan CD", 0.1);
        serviceTypeMap.put("Loan Mortgage", 0.3);
        serviceTypeMap.put("Loan Fund", 0.2);
        serviceTypeMap.put("Bank_Account Loan", 0.0);
        serviceTypeMap.put("Bank_Account Bank_Account", 1.0);
        serviceTypeMap.put("Bank_Account CD", 0.0);
        serviceTypeMap.put("Bank_Account Mortgage", 0.0);
        serviceTypeMap.put("Bank_Account Fund", 0.0);
        serviceTypeMap.put("CD Loan", 0.1);
        serviceTypeMap.put("CD Bank_Account", 0.0);
        serviceTypeMap.put("CD CD", 1.0);
        serviceTypeMap.put("CD Mortgage", 0.2);
        serviceTypeMap.put("CD Fund", 0.2);
        serviceTypeMap.put("Mortgage Loan", 0.3);
        serviceTypeMap.put("Mortgage Bank_Account", 0.0);
        serviceTypeMap.put("Mortgage CD", 0.2);
        serviceTypeMap.put("Mortgage Mortgage", 1.0);
        serviceTypeMap.put("Mortgage Fund", 0.1);
        serviceTypeMap.put("Fund Loan", 0.2);
        serviceTypeMap.put("Fund Bank_Account", 0.0);
        serviceTypeMap.put("Fund CD", 0.2);
        serviceTypeMap.put("Fund Mortgage", 0.1);
        serviceTypeMap.put("Fund Fund", 1.0);
    }

    private static HashMap<String, Double> customerTypeMap = new HashMap<String, Double>();

    static {
        customerTypeMap.put("Business Business", 1.0);
        customerTypeMap.put("Business Professional", 1.0);
        customerTypeMap.put("Business Student", 1.0);
        customerTypeMap.put("Business Doctor", 1.0);
        customerTypeMap.put("Business Other", 1.0);
        customerTypeMap.put("Professional Business", 1.0);
        customerTypeMap.put("Professional Professional", 1.0);
        customerTypeMap.put("Professional Student", 1.0);
        customerTypeMap.put("Professional Doctor", 1.0);
        customerTypeMap.put("Professional Other", 1.0);
        customerTypeMap.put("Student Business", 1.0);
        customerTypeMap.put("Student Professional", 1.0);
        customerTypeMap.put("Student Student", 1.0);
        customerTypeMap.put("Student Doctor", 1.0);
        customerTypeMap.put("Student Other", 1.0);
        customerTypeMap.put("Doctor Business", 1.0);
        customerTypeMap.put("Doctor Professional", 1.0);
        customerTypeMap.put("Doctor Student", 1.0);
        customerTypeMap.put("Doctor Doctor", 1.0);
        customerTypeMap.put("Doctor Other", 1.0);
        customerTypeMap.put("Other Business", 1.0);
        customerTypeMap.put("Other Professional", 1.0);
        customerTypeMap.put("Other Student", 1.0);
        customerTypeMap.put("Other Doctor", 1.0);
        customerTypeMap.put("Other Other", 1.0);
    }

    /*
     * Small Medium Large 1 0.1 0 0.1 1 0.1 0 0.1 1
     */
    private static HashMap<String, Double> sizeMap = new HashMap<String, Double>();

    static {
        sizeMap.put("Small Small", 1.0);
        sizeMap.put("Small Medium", 0.1);
        sizeMap.put("Small Large", 0.0);
        sizeMap.put("Medium Small", 0.1);
        sizeMap.put("Medium Medium", 1.0);
        sizeMap.put("Medium Large", 0.1);
        sizeMap.put("Large Small", 0.0);
        sizeMap.put("Large Medium", 0.1);
        sizeMap.put("Large Large", 1.0);
    }

    /*
     * 
     * Full Web&Email Web None 1 0.8 0 0 0.8 1 0.1 0.5 0 0.1 1 0.4 0 0.5 0.4 1
     * 
     */
    private static HashMap<String, Double> promotionMap = new HashMap<String, Double>();

    static {
        promotionMap.put("Full Full", 1.0);
        promotionMap.put("Full Web&Email", 0.8);
        promotionMap.put("Full Web", 0.0);
        promotionMap.put("Full None", 0.0);
        promotionMap.put("Web&Email Full", 0.8);
        promotionMap.put("Web&Email Web&Email", 1.0);
        promotionMap.put("Web&Email Web", 0.1);
        promotionMap.put("Web&Email None", 0.5);
        promotionMap.put("Web Full", 0.0);
        promotionMap.put("Web Web&Email", 0.1);
        promotionMap.put("Web Web", 1.0);
        promotionMap.put("Web None", 0.4);
        promotionMap.put("None Full", 0.0);
        promotionMap.put("None Web&Email", 0.5);
        promotionMap.put("None Web", 0.4);
        promotionMap.put("None None", 1.0);
    }

    /**
     * 
     * @param Data set to be shuffled.
     */
    public void shuffleData(List<Product> target) {
        long seed = System.nanoTime();
        Collections.shuffle(target, new Random(seed));
    }

    /**
     * 
     * @author Bobfeng
     * The class to store data.
     *
     */
    class Product {
        String serviceType;
        String customerType;
        Double monthlyFee;
        Double budget;
        String size;
        String promotion;
        Double interestRate;
        Double period;
        Double label;

        /**
         * 
         * @param i is the ID of attributes.
         * @return the data.
         */
        public Object get(int i) {
            switch (i) {
            case 0:
                return (String) serviceType;
            case 1:
                return (String) customerType;
            case 2:
                return (Double) monthlyFee;
            case 3:
                return (Double) budget;
            case 4:
                return (String) size;
            case 5:
                return (String) promotion;
            case 6:
                return (Double) interestRate;
            case 7:
                return (Double) period;
            case 8:
                return (Double) label;
            }
            return null;
        }

        /**
         * 
         * @param i is the ID of attributes.
         * @param o is the value to be set.
         */
        public void set(int i, Object o) {
            switch (i) {
            case 0:
                serviceType = (String) o;
                return;
            case 1:
                customerType = (String) o;
                return;
            case 2:
                monthlyFee = (Double) o;
                return;
            case 3:
                budget = (Double) o;
                return;
            case 4:
                size = (String) o;
                return;
            case 5:
                promotion = (String) o;
                return;
            case 6:
                interestRate = (Double) o;
                return;
            case 7:
                period = (Double) o;
                return;
            case 8:
                label = (Double) o;
                return;
            }
        }

        /**
         * 
         * @param other is the product.
         * @param weight the weight to be use.
         * @return the distance between the two products.
         */
        public double distanceTo(Product other, double[] weight) {
            double sum = 0;
            for (int i = 0; i < dimension; i++) {
                double similarity = 0.0;
                switch (i) {
                case 0:
                    similarity = serviceTypeMap
                            .get(get(i) + " " + other.get(i));
                    sum = sum + (1.0 - similarity) * weight[i];
                    break;
                case 1:
                    similarity = customerTypeMap
                            .get(get(i) + " " + other.get(i));
                    sum = sum + (1.0 - similarity) * weight[i];
                    break;
                case 4:
                    similarity = sizeMap.get(get(i) + " " + other.get(i));
                    sum = sum + (1.0 - similarity) * weight[i];
                    break;
                case 5:
                    similarity = promotionMap.get(get(i) + " " + other.get(i));
                    sum = sum + (1.0 - similarity) * weight[i];
                    break;
                default:
                    sum = sum + Math.pow(
                            weight[i]
                                    * ((Double) get(i) - (Double) other.get(i)),
                            2);
                    break;
                }
            }
            return Math.sqrt(sum);
        }

        public String toString() {
            return serviceType + "," + customerType + "," + monthlyFee + ","
                    + budget + "," + size + "," + promotion + "," + interestRate
                    + "," + period;
        }
    }

    /**
     * 
     * @author Bobfeng
     * The class is used for vote method.
     */
    public class Element implements Comparable<Element> {
        double distance;
        Product customer;

        public Element(double d, Product c) {
            distance = d;
            customer = c;
        }

        @Override
        public int compareTo(Element o) {
            return -1 * Double.compare(distance, o.distance);
        }
    }

    /**
     * Read in the data from file.
     */
    private void loadData() {
        train = readArff(trainName);
        test = readArff(testName);
    }

    /**
     * 
     * @param fileName.
     * @return the product list.
     */
    private List<Product> readArff(String fileName) {
        List<Product> data = new ArrayList<Product>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String input = "";

            while (input != null && !input.equals("@data")) {
                input = br.readLine();
            }
            // br.readLine();

            while ((input = br.readLine()) != null) {
                String[] parts = input.split(",");
                Product c = new Product();
                for (int i = 0; i < parts.length; i++) {
                    if (i == 0 || i == 1 || i == 4 || i == 5) {
                        // System.out.println(c.get(i));
                        // System.out.println(i + parts[i]);
                        c.set(i, parts[i]);
                    } else {
                        // System.out.println(parts[i]);
                        Double d = Double.parseDouble(parts[i]);
                        c.set(i, d);
                    }
                }
                // System.out.println(c);
                data.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 
     * @param trainList
     * @param testList
     */
    private void normalize(List<Product> trainList, List<Product> testList) {
        List<Product> all = new ArrayList<Product>();
        all.addAll(trainList);
        // all.addAll(testList);

        // To store the max and min
        List<Double> minOfAttrs = new ArrayList<Double>();
        List<Double> maxOfAttrs = new ArrayList<Double>();

        // To find the maximum and minimum
        Product c = all.get(0);
        for (int i = 0; i < dimension; i++) {
            if (c.get(i) instanceof String) {
                minOfAttrs.add(null);
                maxOfAttrs.add(null);
                continue;
            } else {
                Double max = Double.MIN_VALUE;
                Double min = Double.MAX_VALUE;
                for (int j = 0; j < all.size(); j++) {
                    c = all.get(j);
                    max = Math.max((Double) c.get(i), max);
                    min = Math.min((Double) c.get(i), min);
                }
                maxOfAttrs.add(max);
                minOfAttrs.add(min);
            }
        }

        all.addAll(testList);
        c = all.get(0);
        for (int i = 0; i < dimension; i++) {
            if (c.get(i) instanceof String) {
                continue;
            } else {
                Double max = maxOfAttrs.get(i);
                Double min = minOfAttrs.get(i);
                for (int j = 0; j < all.size(); j++) {
                    c = all.get(j);
                    c.set(i, ((Double) c.get(i) - min) / (max - min));
                }
            }
        }
    }

    /**
     * The main KNN body method.
     * @param trainList
     * @param testList
     * @param weight
     */
    private void labelByKnn(List<Product> trainList, List<Product> testList,
            double[] weight) {
        System.out.println("========= Start Labeling =========");
        for (int i = 0; i < testList.size(); i++) {
            Product testData = testList.get(i);
            PriorityQueue<Element> pq = new PriorityQueue<Element>();

            for (int j = 0; j < trainList.size(); j++) {
                Product trainData = trainList.get(j);
                double distance = testData.distanceTo(trainData, weight);
                // Try to find the kth nearest distance and put them in the priority queue.
                if (pq.size() >= k) {
                    if (pq.peek().distance > distance) {
                        pq.poll();
                        pq.add(new Element(distance, trainData));
                    }
                } else {
                    pq.add(new Element(distance, trainData));
                }
            }
            voteResult tmp = vote(pq);
            System.out.print(testData);
            System.out.print(",");
            System.out.print(tmp.score);
            System.out.print(",");
            System.out.print(tmp.result);
            System.out.print("\n");
        }
        System.out.println("========= Finish Labeling =========");
    }

    class crossReturn {
        double error;
        double acc;
        crossReturn(double error, double acc) {
            this.error = error;
            this.acc = acc;
        }
    }
    /**
     * Used for cross validation accuracy calculation.
     * @param trainList
     * @param testList
     * @param weight
     * @return
     */
    public crossReturn crossByKnn(List<Product> trainList, List<Product> testList,
            double[] weight) {
        double right = 0;
        double acc = 0;
        for (int i = 0; i < testList.size(); i++) {
            Product testData = testList.get(i);
            PriorityQueue<Element> pq = new PriorityQueue<Element>();

            for (int j = 0; j < trainList.size(); j++) {
                Product trainData = trainList.get(j);
                double distance = testData.distanceTo(trainData, weight);

                if (pq.size() >= k) {
                    if (pq.peek().distance > distance) {
                        pq.poll();
                        pq.add(new Element(distance, trainData));
                    }
                } else {
                    pq.add(new Element(distance, trainData));
                }
            }
            voteResult result = vote(pq);
            right = right + Math.pow((result.score - testData.label), 2);
            if ((result.result.equals("Fail") && testData.label < 20) || (result.result.equals("Success") && testData.label > 20)) {
                acc++;
            }
        }
        
        crossReturn x = new crossReturn(right,acc / testList.size());
        return x;
    }
    
    /**
     * 
     * @author Bobfeng
     * Use this class to store the vote result.
     */
    class voteResult {
        double score;
        String result;
        public voteResult (double score, String result) {
            this.score = score;
            this.result = result;
        }
    }

    /**
     * Use score to determine the label.
     * @param pq.
     * @return the vote result.
     */
    public voteResult vote(PriorityQueue<Element> pq) {
        // Store the score the target product
        double score = 0;
        Element tmp;
        double tmpWeight = 0;
        while (!pq.isEmpty()) {
            tmp = pq.poll();
            Double label = tmp.customer.label;
            tmpWeight = tmpWeight +  Math.pow(tmp.distance, -1);
            score = score + label * Math.pow(tmp.distance, -1);
            
        }
        score = score / (tmpWeight);
        if (score < 20) {
            voteResult result = new voteResult(score, "Fail");
            return result;
        } else {
            voteResult result = new voteResult(score, "Success");
            return result;
        }
    }

    /**
     * Deal data for validation
     * 
     * @param train
     */
    public void dataForValidation(List<Product> train) {
        int divideLength = train.size() / 5;
        // Seperate the trainset to 5 parts.

        List<Product> test1 = new ArrayList<Product>();
        List<Product> test2 = new ArrayList<Product>();
        List<Product> test3 = new ArrayList<Product>();
        List<Product> test4 = new ArrayList<Product>();
        List<Product> test5 = new ArrayList<Product>();
        List<Product> valid1 = new ArrayList<Product>();
        List<Product> valid2 = new ArrayList<Product>();
        List<Product> valid3 = new ArrayList<Product>();
        List<Product> valid4 = new ArrayList<Product>();
        List<Product> valid5 = new ArrayList<Product>();
        for (int i = 0; i < divideLength; i++) {
            test1.add(train.get(i));
        }

        for (int i = divideLength; i < divideLength * 2; i++) {
            test2.add(train.get(i));
        }

        for (int i = divideLength * 2; i < divideLength * 3; i++) {
            test3.add(train.get(i));
        }

        for (int i = divideLength * 3; i < divideLength * 4; i++) {
            test4.add(train.get(i));
        }

        for (int i = divideLength * 4; i < train.size(); i++) {
            test5.add(train.get(i));
        }

        valid1.addAll(test2);
        valid1.addAll(test3);
        valid1.addAll(test4);
        valid1.addAll(test5);

        valid2.addAll(test1);
        valid2.addAll(test3);
        valid2.addAll(test4);
        valid2.addAll(test5);

        valid3.addAll(test1);
        valid3.addAll(test2);
        valid3.addAll(test4);
        valid3.addAll(test5);

        valid4.addAll(test1);
        valid4.addAll(test2);
        valid4.addAll(test3);
        valid4.addAll(test5);

        valid5.addAll(test1);
        valid5.addAll(test2);
        valid5.addAll(test3);
        valid5.addAll(test4);
        testValid.add((ArrayList<Product>) test1);
        testValid.add((ArrayList<Product>) test2);
        testValid.add((ArrayList<Product>) test3);
        testValid.add((ArrayList<Product>) test4);
        testValid.add((ArrayList<Product>) test5);

        valid.add((ArrayList<Product>) valid1);
        valid.add((ArrayList<Product>) valid2);
        valid.add((ArrayList<Product>) valid3);
        valid.add((ArrayList<Product>) valid4);
        valid.add((ArrayList<Product>) valid5);
    }

    /**
     * Use multi-thread to find the weight.
     * @author Bobfeng
     *
     */
    class MyThread extends Thread {
        /**
         * override
         */
        ArrayList<ArrayList<Product>> test;
        ArrayList<ArrayList<Product>> valid;
        double xx;

        public MyThread(ArrayList<ArrayList<Product>> test,
                ArrayList<ArrayList<Product>> valid, double xx) {
            this.test = test;
            this.valid = valid;
            this.xx = xx;
        }

        public void run() {
            double[] weight = { 4.0, 5.0, 2.0, 5.0, 1.0, 1.0, 1.0, xx };
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    for (int c = 0; c < 9; c++) {
                        for (int b = 0; b < 1; b++) {
                            for (int a = 0; a < 1; a++) {
                                for (int l = 0; l < 1; l++) {
                                    for (int i = 0; i < 1; i++) {
                                        double sum = 0;
                                        double accuracy = 0;
                                        for (int j = 0; j < 5; j++) {
                                            sum = sum + crossByKnn(valid.get(j),
                                                    test.get(j), weight).error;
                                            accuracy = accuracy + crossByKnn(valid.get(j),
                                                    test.get(j), weight).acc;
                                        }
                                        // Average accuracy
                                        double ave = sum / 5.0;
                                        double accav = accuracy / 5.0;
                                        if (ave > acc) {
                                            weight[0] = weight[0] + 0.5;
                                            continue;
                                        } else {
                                            System.out.println(
                                                    ave + " " + weight[0] + " "
                                                            + weight[1] + " "
                                                            + weight[2] + " "
                                                            + weight[3] + " "
                                                            + weight[4] + " "
                                                            + weight[5] + " "
                                                            + weight[6] + " "
                                                            + weight[7] + " " + "accuracy: " + accav);
                                            // double[] tmp = new double[8];
                                            // for (int q = 0; q < 8; q++) {
                                            // tmp[q] = weight[q];
                                            // }
                                            // hash.put(ave, tmp);
                                            acc = Math.min(acc, ave);
                                            weight[0] = weight[0] + 0.5;
                                        }
                                    }
                                    weight[1] = weight[1] + 0.5;
                                    weight[0] = 1;
                                }
                                weight[2] = weight[2] + 0.5;
                                weight[1] = 1;
                            }
                            weight[3] = weight[3] + 0.5;
                            weight[2] = 1;
                        }
                        weight[4] = weight[4] + 0.5;
                        weight[3] = 1;
                    }
                    weight[5] = weight[4] + 0.5;
                    weight[4] = 1;
                }
                weight[6] = weight[6] + 0.5;
                weight[5] = 1;
            }
        }
    }
}
