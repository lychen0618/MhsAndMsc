package com.lychen.dataprocess;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class alphatest {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new File("./results/alphatest.txt"));
        ArrayList<Double> arr = new ArrayList<>();
        while (scanner.hasNext()) {
            arr.add(scanner.nextDouble());
        }

        double[] alphaSet = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        PrintWriter br = new PrintWriter(new FileWriter("./results/out1.csv"));
        int i = 0;
        for (int fileindex = 0; fileindex < 6; ++fileindex) {
            int alphaindex = 0;
            int kindex = 0;
            double total = 0;
            for (int eleindex = fileindex * 304; eleindex < (fileindex + 1) * 304; ++eleindex) {
                total += arr.get(eleindex);
                kindex++;
                if (kindex == 16) {
                    kindex = 0;

                    br.println(alphaSet[alphaindex] + ", " + total / 16);
                    alphaindex++;
                    total=0;
                }
            }
        }
        br.flush();
        br.close();
    }
}
