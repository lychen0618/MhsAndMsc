package com.lychen.mhsandmsc.mhs;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

public class TransformData {

    public static String getOutFileName(String fileName) {
        return "./tests/mhs/" + fileName.substring(fileName.indexOf("msc") + 4);
    }

    private static void outputToFile(String fileName, ArrayList<ArrayList<Integer>> arr) throws IOException {
        String outFileName = getOutFileName(fileName);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outFileName));
        for (int i = 0; i < arr.size(); i++) {
            int numOfSetsForElem = arr.get(i).size();
            for (int j = 0; j < numOfSetsForElem; j++) {
                if (j != 0) osw.write(" ");
                String set = String.valueOf(arr.get(i).get(j));
                osw.write(set);
            }
            osw.write("\n");
        }
        osw.close();
    }

    private static void transformRail(String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(fileName));
        int universeSize = scanner.nextInt();
        int numOfSets = scanner.nextInt();
        ArrayList<ArrayList<Integer>> arr = new ArrayList<>(universeSize);
        for (int i = 0; i < universeSize; ++i) arr.set(i, new ArrayList<>());

        for (int i = 0; i < numOfSets; i++) {

            scanner.nextInt();
            int setSize = scanner.nextInt();

            for (int j = 0; j < setSize; j++) {
                int element = scanner.nextInt();
                arr.get(element - 1).add(j + 1);
            }
        }

        outputToFile(fileName, arr);
    }

    private static void transformDblp(String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(fileName));
        int universeSize = scanner.nextInt();
        int numOfSets = scanner.nextInt();
        ArrayList<ArrayList<Integer>> arr = new ArrayList<>(universeSize);
        for (int i = 0; i < universeSize; ++i) arr.set(i, new ArrayList<>());

        for (int i = 0; i < numOfSets; i++) {

            Scanner lineScanner = new Scanner(scanner.nextLine());
            while (lineScanner.hasNext()) {
                int element = lineScanner.nextInt();
                arr.get(element - 1).add(i + 1);

            }
        }

        outputToFile(fileName, arr);
    }

    private static void transformDefault(String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(fileName));
        int universeSize = scanner.nextInt();
        int numOfSets = scanner.nextInt();

        for (int i = 0; i < numOfSets; i++) {
            scanner.nextInt();
        }

        String outFileName = getOutFileName(fileName);

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outFileName));
        for (int i = 0; i < universeSize; i++) {
            int numOfSetsForElem = scanner.nextInt();
            for (int j = 0; j < numOfSetsForElem; j++) {
                if (j != 0) osw.write(" ");
                String set = String.valueOf(scanner.nextInt());
                osw.write(set);
            }
            osw.write("\n");
        }
        osw.close();
    }

    public static void Msc2Mhs(String fileName) throws IOException {
        if (fileName.contains("rail")) transformRail(fileName);
        else if (fileName.contains("dblp")) transformDblp(fileName);
        else transformDefault(fileName);
    }

    public static void main(String[] args) throws IOException {
        String fileName = "D:\\enumerateWeightedSetCovers\\tests\\paper\\example1.txt";
        Msc2Mhs(fileName);
    }
}
