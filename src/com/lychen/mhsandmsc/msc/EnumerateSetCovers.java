package com.lychen.mhsandmsc.msc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class EnumerateSetCovers {

    // set cover problem to be solved
    private SetCoverProblem problem;

    // number of sets in the problem
    private int numOfSets;

    // size of universe
    private int universeSize;

    // starting time
    private long startingTime;

    // priority queue used in the algorithm
    // different from the pseudocode in that we use a single queue for elements of both Q1 and Q2
    private PriorityQueue<QueueEntry> pq = new PriorityQueue<QueueEntry>();

    // adds an entry to the queue
    private void addToQueue(QueueEntry entry, PriorityQueue<QueueEntry> pq) {

        pq.add(entry);
    }

    private void addAllSets(Solution sol, BitSet sets) {

        for (int i = sets.nextSetBit(0); i != -1; i = sets.nextSetBit(i + 1)) {
            sol.addSet(i, problem.getWeight(i));
        }
    }


    // process an entry that has been removed from Q1
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


    /**
     * enumerate set covers
     *
     * @param printedAlready contains the set covers
     * @param problem is the weighted set cover problem
     */
    public void enumerate(HashSet<BitSet> printedAlready, SetCoverProblem problem) throws IOException {

        startingTime = System.nanoTime();
        this.problem = problem;
        numOfSets = problem.getNumberOfSets();
        universeSize = problem.getUniverseSize();

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
                printedAlready.add((BitSet) newSolution.getSolution().clone());
            }

            dealWithQueueEntry(entry, pq);
        }

    }


    public static void main(String[] inp) throws IOException {

        if (inp.length != 1) {

            System.out.println("Usage: java EnumerateSetCovers <input file>");
            System.exit(-1);
        }

        EnumerateSetCovers enumsc = new EnumerateSetCovers();
        HashSet<BitSet> printedAlready=new HashSet<>();
        enumsc.enumerate(printedAlready, SetCoverProblemGenerator.generateSetCoverProblem(inp[0]));
    }
}


/**
 * Class QueueEntry is used as entry values for the priority queue
 * Each entry contains 4 fields:
 * - sol: the solution represented by the entry
 * - second: a bit set indicating which sets are allowed in the "second" part of the queue entry as in the paper
 * - third: a bit set indicating which sets are allowed in the "third" part of the queue entry as in the paper
 */
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