package com.lychen.mhsandmsc.msc;

import com.lychen.mhsandmsc.MyHelpFunc;

import java.io.*;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Vector;

public class Tester {


    public static void main(String args[]) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: java Tester <tests folder> <output>");
            System.exit(-1);
        }

        File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        PrintWriter br = new PrintWriter(new FileWriter(args[1] + ".csv"));

        br.println("Test Name, Total time");

        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {

            if (listOfFiles[i].isFile()) {

                String testFile = listOfFiles[i].getName();
                String fileName = args[0] + File.separator + testFile;

                long startTime = System.nanoTime();
                SetCoverProblem problem = SetCoverProblemGenerator.generateSetCoverProblem(fileName);
                EnumerateSetCovers enumsc = new EnumerateSetCovers();
                br.print(testFile + ", " );
                System.out.println(testFile);
                HashSet<BitSet> printedAlready=new HashSet<>();
                enumsc.enumerate(printedAlready, problem);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;
                br.print(duration);
                br.println();
                br.flush();

                MyHelpFunc.writeToFile(printedAlready, testFile, 1);
            }
        }

    }

}