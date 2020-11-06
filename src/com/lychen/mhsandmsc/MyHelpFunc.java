package com.lychen.mhsandmsc;

import com.lychen.mhsandmsc.mhs.*;

import java.io.*;
import java.util.*;

public class MyHelpFunc {
    public static void creatDataSetsForMSC(int universeSize, int numOfSets, String fileName) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName));
        osw.write(universeSize + " " + numOfSets + "\n");
        for (int i = 0; i < numOfSets; ++i) {
            osw.write(1 + " ");
            if (i != numOfSets - 1 && i != 0 && i % 10 == 0) osw.write("\n");
        }
        osw.write("\n");

        Random rmv = new Random();
        Random rms = new Random();
        for (int i = 0; i < universeSize; ++i) {
            int size = rmv.nextInt(numOfSets) - 10;
            if (size <= 0) size = 1;
            osw.write(size + " \n");
            int[] generateSet = new int[size];
            boolean[] flag = new boolean[numOfSets];
            int num = 0;
            while (num < size) {
                while (true) {
                    int newInt = rms.nextInt(numOfSets);
                    if (!flag[newInt]) {
                        flag[newInt] = true;
                        generateSet[num++] = newInt;
                        break;
                    }
                }
            }
            for (int j = 0; j < size; ++j) {
                osw.write(generateSet[j] + " ");
                if (j != size - 1 && j != 0 && j % 10 == 0) osw.write("\n");
            }
            osw.write("\n");
        }

        osw.close();
    }

    public static boolean isContentEqual(String fileName1, String fileName2) throws FileNotFoundException {
        Scanner sc1 = new Scanner(new File(fileName1));
        Scanner sc2 = new Scanner(new File(fileName2));
        ArrayList<ArrayList<Integer>> arr1 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> arr2 = new ArrayList<>();

        while (sc1.hasNext()) {
            String line = sc1.nextLine();
            String[] s = line.split(" ");
            ArrayList<Integer> temp = new ArrayList<>();
            for (String ss : s) temp.add(Integer.valueOf(ss));
            arr1.add(temp);
        }
        while (sc2.hasNext()) {
            String line = sc2.nextLine();
            String[] s = line.split(" ");
            ArrayList<Integer> temp = new ArrayList<>();
            for (String ss : s) temp.add(Integer.valueOf(ss));
            arr2.add(temp);
        }
        if (arr1.size() != arr2.size()) return false;

        arr1.sort(new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
                if (o1.size() != o2.size()) return o1.size() - o2.size();
                for (int i = 0; i < o1.size(); ++i) {
                    if (!o1.get(i).equals(o2.get(i))) return o1.get(i) - o2.get(i);
                }
                return 0;
            }
        });

        arr2.sort(new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
                if (o1.size() != o2.size()) return o1.size() - o2.size();
                for (int i = 0; i < o1.size(); ++i) {
                    if (!o1.get(i).equals(o2.get(i))) return o1.get(i) - o2.get(i);
                }
                return 0;
            }
        });

        for (int i = 0; i < arr1.size(); ++i) {
            if (arr1.get(i).size() != arr2.get(i).size()) return false;
            for (int j = 0; j < arr1.get(i).size(); ++j) {
                if (!arr1.get(i).get(j).equals(arr2.get(i).get(j))) return false;
            }
        }

        return true;
    }

    public static void writeToFile(HashSet<BitSet> printedAlready, String fileName, int type) throws IOException {

        String outFileName = "./results/";
        if (type == 1) outFileName += "msc/";
        else if (type == 2) outFileName += "mhs/dfs/";
        else outFileName += "mhs/rs/";
        outFileName += fileName;

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outFileName));
        int index = 1;
        for (BitSet bs : printedAlready) {
            for (int i = bs.nextSetBit(0); i != -1; i = bs.nextSetBit(i + 1)) {
                int temp = i;
                //if (type == 1) temp++;
                osw.write(temp + " ");
            }
            osw.write("\n");
            index++;
        }
        osw.close();

    }


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int numOfExample = sc.nextInt();
        int universeSize = sc.nextInt();
        int unmOfSets = sc.nextInt();
        String fileName = "./tests/msc/myexample/example";
        for (int i = 0; i < numOfExample; ++i) {
            String newFile = fileName + (i + 10) + ".txt";
            creatDataSetsForMSC(universeSize, unmOfSets, newFile);
            TransformData.Msc2Mhs(newFile);
        }
    }
}
