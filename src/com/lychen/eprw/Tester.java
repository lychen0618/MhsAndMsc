package com.lychen.eprw;

import com.lychen.mhsandmsc.MyHelpFunc;
import com.lychen.eprw.EnumK;
import com.lychen.mhsandmsc.msc.SetCoverProblem;
import com.lychen.mhsandmsc.msc.SetCoverProblemGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Tester {

    private static void ctwHelpFunc(int[] ls, int nowK, double tempDis1,
                                    double tempDis2, double[][] distances, ArrayList<Double> resDis) {
        if (nowK == ls.length - 1) {
            resDis.add(EnumK.calWeight(tempDis1, tempDis2));
            return;
        }
        if ((distances[0].length - ls[nowK]) < (ls.length - nowK)) return;

        for (int index = ls[nowK] + 1; index < distances[0].length; ++index) {
            double minDis = tempDis1;
            double sumDis = 0;
            for (int i : ls) {
                minDis = Math.min(minDis, distances[i][index]);
                sumDis += distances[i][index];
            }

            nowK++;
            ls[nowK] = index;
            ctwHelpFunc(ls, nowK, minDis, sumDis + tempDis2, distances, resDis);
            nowK--;
        }
    }

    // min, average, max
    public static void computeThreeWeights(HashSet<BitSet> printedAlready, double[] res, int k) {
        res[0] = 1.0;
        res[2] = 0.0;

        int size = printedAlready.size();
        double[][] distances = new double[size][size];
        BitSet[] temp = new BitSet[size];
        int index = 0;
        for (BitSet bs : printedAlready) {
            for (int i = 0; i < index; ++i) {
                distances[i][index] = EnumK.calDistance(bs, temp[i]);
            }
            temp[index] = bs;
            index++;
        }

        ArrayList<Double> resDis = new ArrayList<>();
        for (int i = 0; i < size - k + 1; ++i) {
            int[] ls = new int[k];
            ls[0] = i;
            double tempDis1 = 1, tempDis2 = 0;
            ctwHelpFunc(ls, 0, tempDis1, tempDis2, distances, resDis);
        }

        for (double db : resDis) {
            res[0] = Math.min(res[0], db);
            res[1] += db;
            res[2] = Math.max(res[2], db);
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java Tester <tests folder> <output>");
            System.exit(-1);
        }

        File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        PrintWriter br = new PrintWriter(new FileWriter(args[1] + ".csv"));

        // br.println("Dataset, K, Total time, Diversity, Alpha");

        br.println("Test Name, K, Total time, Diversity, Min Diversity, Average Diversity, Max Diversity");

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {

            if (listOfFiles[i].isFile()) {

                String testFile = listOfFiles[i].getName();
                String fileName = args[0] + File.separator + testFile;
                SetCoverProblem problem = SetCoverProblemGenerator.generateSetCoverProblem(fileName);

//                double[] alphaSet = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//                for (int alphai = 0; alphai < alphaSet.length; ++alphai) {
//                    for (int k = 5; k <= 20; ++k) {
//                        long startTime = System.nanoTime();
//                        EnumK enumsc = new EnumK(k);
//                        EnumK.alpha = alphaSet[alphai];
//                        System.out.println(testFile + " k = " + k);
//                        enumsc.enumerate(problem);
//                        long endTime = System.nanoTime();
//                        long duration = (endTime - startTime) / 1000000;
//
//                        br.println(testFile + ", " + k + ", " + duration + ", " + enumsc.weightDis + ", " + alphaSet[alphai]);
//                        br.flush();
//                    }
//                }

                for (int k = 3; k <= 20; ++k) {
                    long startTime = System.nanoTime();
                    EnumK enumsc = new EnumK(k);
                    System.out.println(testFile + " k = " + k);
                    enumsc.enumerate(problem);
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;

//                    double[] minAvgMaxWeightDis = new double[3];
//                    computeThreeWeights(enumsc.printedAlready, minAvgMaxWeightDis, k);
//                    for (int j = 0; j < k; ++j) {
//                        int temp = enumsc.printedAlready.size() - j;
//                        minAvgMaxWeightDis[1] /= temp;
//                        minAvgMaxWeightDis[1] *= (j+1);
//                    }
//
//                    br.println(testFile + ", " + k + ", " + duration + ", " + enumsc.weightDis + ", " +
//                            minAvgMaxWeightDis[0] + ", " + minAvgMaxWeightDis[1] + ", " + minAvgMaxWeightDis[2]);
                    br.println(testFile + ", " + k + ", " + duration + ", " + String.format("%.4f",enumsc.weightDis));
                    br.flush();
                }


                //MyHelpFunc.writeToFile(printedAlready, testFile, 1);
            }
        }
        br.close();

    }
}
