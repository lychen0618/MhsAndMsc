package com.lychen.mhsandmsc.mhs;

import com.lychen.mhsandmsc.MyHelpFunc;

import java.io.*;
import java.util.BitSet;
import java.util.Objects;

public class Tester {
    private static void testEqual(String fileName) throws FileNotFoundException {
        String fileName1="./results/msc/"+fileName;
        String fileName2="./results/mhs/dfs/"+fileName;
        String fileName3="./results/mhs/rs/"+fileName;
        if(!MyHelpFunc.isContentEqual(fileName1,fileName2)) System.out.println("msc!=dfs");
        if(!MyHelpFunc.isContentEqual(fileName1,fileName3)) System.out.println("msc!=rs");
        if(!MyHelpFunc.isContentEqual(fileName2,fileName3)) System.out.println("dfs!=rs");
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        if (args.length != 3) {
            System.out.println("Usage: java Tester <tests folder> <output> <search method>");
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
                Hypergraph H = new Hypergraph(fileName);
                Hypergraph crit = new Hypergraph(H.num_edges(), H.num_verts());
                BitSet uncov = new BitSet(H.num_edges());
                BitSet S = new BitSet(H.num_verts());
                uncov.flip(0, H.num_edges());
                TwoMethods sol=new TwoMethods();
                if(args[2].equals("dfs")){
                    BitSet cand = new BitSet(H.num_verts());
                    cand.flip(0, H.num_verts());
                    sol.dfs(crit.clone(), (BitSet)uncov.clone(), H, H.transpose(), (BitSet)S.clone(), cand);
                }
                else{
                    sol=new TwoMethods();
                    sol.rs(crit.clone(), (BitSet)uncov.clone(), H, H.transpose(), (BitSet)S.clone());
                }
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;

                br.print(testFile + ", " + duration);
                br.println();
                br.flush();

                System.out.println(testFile);
                if(args[2].equals("dfs")) MyHelpFunc.writeToFile(sol.printedAlready,testFile,2);
                else{
                    MyHelpFunc.writeToFile(sol.printedAlready,testFile,3);
                    testEqual(testFile);
                }
            }
        }
        br.close();
    }
}
