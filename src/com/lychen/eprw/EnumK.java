package com.lychen.eprw;

import com.lychen.mhsandmsc.msc.GreedyMinSetCover;
import com.lychen.mhsandmsc.msc.SetCoverProblem;
import com.lychen.mhsandmsc.msc.SetCoverProblemGenerator;
import com.lychen.mhsandmsc.msc.Solution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class EnumK {

    // k diverse sets
    private static int k;

    public static double alpha = 1.5;

    public double weightDis = 0.0;

    private final ArrayList<SetEntry> pinvs;

    // set cover problem to be solved
    private SetCoverProblem problem;

    public final HashSet<BitSet> printedAlready = new HashSet<>();

    private final PriorityQueue<QueueEntry> pq = new PriorityQueue<>();


    public EnumK(Integer k) {
        EnumK.k = k;
        pinvs = new ArrayList<>();
    }


    public static double calWeight(double minDis, double sumDis) {
        return (alpha * minDis + sumDis * 2 / ((k - 1) * k * 1.0)) / (1 + alpha);
    }

    public static double calDistance(BitSet a, BitSet b) {
        BitSet ab = (BitSet) a.clone();
        ab.xor(b);
        return ab.cardinality() * 2.0 / (a.cardinality() + b.cardinality() + ab.cardinality());
    }


    public double calMinDis(int exc) {
        double res = 100000000;
        for (int i = 0; i < k; ++i) {
            if (i == exc) continue;
            for (int j = i + 1; j < k; ++j) {
                if (j == exc) continue;
                res = Math.min(res, pinvs.get(i).getDistances()[j]);
            }
        }
        return res;
    }


    private void addToQueue(QueueEntry entry, PriorityQueue<QueueEntry> pq) {

        pq.add(entry);
    }


    private void addAllSets(Solution sol, BitSet sets) {

        for (int i = sets.nextSetBit(0); i != -1; i = sets.nextSetBit(i + 1)) {
            sol.addSet(i, problem.getWeight(i));
        }
    }


    private void dealWithQueueEntry(QueueEntry entry, PriorityQueue<QueueEntry> pq) {

        BitSet canBeRemoved = (BitSet) entry.sol.getSolution().clone();
        canBeRemoved.andNot(entry.second);

        BitSet third = entry.third;
        BitSet second = entry.second;
        second = (BitSet) second.clone();
        int i = 0;
        for (int si = canBeRemoved.nextSetBit(0); si != -1; si = canBeRemoved.nextSetBit(si + 1)) {

            third = (BitSet) third.clone();
            third.set(si, false);
            BitSet covered = problem.getCoveredBy(second);
            Solution newSolution = (new GreedyMinSetCover()).approxSetCover(problem, covered, third); //, nonRedundant);

            if (newSolution != null) {

                addAllSets(newSolution, second);
                QueueEntry newEntry = new QueueEntry(newSolution, second, third);
                addToQueue(newEntry, pq);
            }

            second = (BitSet) second.clone();
            second.set(si);
        }
    }


    public void enumerate(SetCoverProblem problem) throws IOException {

        this.problem = problem;
        // number of sets in the problem
        int numOfSets = problem.getNumberOfSets();

        // 0:mindis 1:totaldis
        double[] storedVals = new double[2];
        storedVals[0] = 100000000.0;


        Solution s = (new GreedyMinSetCover()).approxSetCover(problem);

        if (s != null) {
            BitSet none = new BitSet(numOfSets);
            BitSet all = new BitSet(numOfSets);
            all.set(0, numOfSets);
            addToQueue(new QueueEntry(s, none, all), pq);
        }

        while (!pq.isEmpty()) {

            QueueEntry entry = pq.poll();

            Solution newSolution = problem.makeNonRedundant(entry.sol, new BitSet());
            if (!printedAlready.contains(newSolution.getSolution())) {
                BitSet setCover = (BitSet) newSolution.getSolution().clone();
                printedAlready.add(setCover);

                // ----------------------------------------------------------------------------------------------------
                if (pinvs.size() < k - 1) pinvs.add(new SetEntry(k, setCover));
                else if (pinvs.size() == k - 1) {
                    pinvs.add(new SetEntry(k, setCover));
                    for (int i = 0; i < k; ++i) {
                        for (int j = i + 1; j < k; ++j) {
                            double dis = calDistance(pinvs.get(i).getSetCover(), pinvs.get(j).getSetCover());
                            pinvs.get(i).getDistances()[j] = dis;
                            pinvs.get(j).getDistances()[i] = dis;
                            storedVals[1] += dis;
                            storedVals[0] = Math.min(storedVals[0], dis);
                        }
                    }
                    weightDis = calWeight(storedVals[0], storedVals[1]);
                    for (int i = 0; i < k; ++i) {
                        pinvs.get(i).setExcludedMin(calMinDis(i));
                        pinvs.get(i).addDis();
                    }
                } else {
                    SetEntry newSt = new SetEntry(k, setCover);
                    for (int i = 0; i < k; ++i) {
                        newSt.getDistances()[i] = calDistance(setCover, pinvs.get(i).getSetCover());
                    }
                    newSt.addDis();
                    int reIndex = 0;
                    double reDis = 0;
                    for (int i = 0; i < k; ++i) {
                        double a = pinvs.get(i).getExcludedMin();
                        for (int j = 0; j < k; ++j) {
                            if (j != i) a = Math.min(a, newSt.getDistances()[j]);
                        }
                        double tempDis = calWeight(a, storedVals[1] - pinvs.get(i).getDisSum()
                                + newSt.getDisSum() - newSt.getDistances()[i]);
                        if (tempDis > reDis) {
                            reIndex = i;
                            reDis = tempDis;
                        }
                    }

                    //更新SetEntry的数据
                    if (reDis > weightDis) {
                        newSt.setExcludedMin(pinvs.get(reIndex).getExcludedMin());
                        newSt.setDisSum(newSt.getDisSum() - newSt.getDistances()[reIndex]);
                        newSt.getDistances()[reIndex] = 0.0;

                        for (int i = 0; i < k; ++i) {
                            if (i == reIndex) continue;
                            pinvs.get(i).setDisSum(pinvs.get(i).getDisSum() - pinvs.get(i).getDistances()[reIndex]
                                    + newSt.getDistances()[i]);
                            pinvs.get(i).getDistances()[reIndex] = newSt.getDistances()[i];
                        }

                        pinvs.set(reIndex, newSt);
                        for (int i = 0; i < k; ++i) {
                            if (i == reIndex) continue;
                            pinvs.get(i).setExcludedMin(calMinDis(i));
                        }
                    }

                }
                // ----------------------------------------------------------------------------------------------------

            }

            if (printedAlready.size() == 5000) {
                System.out.println("finish");
                break;
            }
            dealWithQueueEntry(entry, pq);
        }

    }


    public static void main(String[] inp) throws IOException {

        if (inp.length != 2) {

            System.out.println("Usage: java EnumerateSetCovers <input file> <number of k>");
            System.exit(-1);
        }

        EnumK enumsc = new EnumK(Integer.valueOf(inp[1]));

        enumsc.enumerate(SetCoverProblemGenerator.generateSetCoverProblem(inp[0]));

        System.out.println("...");
    }
}


class QueueEntry implements Comparable<QueueEntry> {

    Solution sol;
    BitSet second;
    BitSet third;

    public QueueEntry(Solution sol, BitSet second, BitSet third) {

        this.sol = sol;
        this.second = second;
        this.third = third;
    }

    public int compareTo(QueueEntry other) {
        return sol.getWeight() - other.sol.getWeight();
    }

    static String bitSetString(BitSet bs) {

        String s = "{";
        for (int i = bs.nextSetBit(0); i != -1; i = bs.nextSetBit(i + 1)) {
            s += "" + i + " ";
        }
        return s + "}";
    }

    public String toString() {

        String queue = "QueueEntry: ";
        return queue + sol.toString() + " | " + bitSetString(second) + " | " + bitSetString(third);
    }

}


class SetEntry {

    private final double[] distances;
    private final BitSet setCover;
    private double excludedMin;
    private double disSum = 0;

    public SetEntry(int k, BitSet setCover) {
        this.setCover = setCover;
        distances = new double[k];
        excludedMin = 100000000.0;
    }

    public double[] getDistances() {
        return distances;
    }

    public BitSet getSetCover() {
        return setCover;
    }

    public double getExcludedMin() {
        return excludedMin;
    }

    public void setExcludedMin(double excludedMin) {
        this.excludedMin = excludedMin;
    }

    public double getDisSum() {
        return disSum;
    }

    public void setDisSum(double disSum) {
        this.disSum = disSum;
    }

    public void addDis() {
        for (double distance : distances) disSum += distance;
    }
}