package com.lychen.mhsandmsc.mhs;

import java.io.IOException;
import java.util.*;

public class TwoMethods {
    private static Shd shd = new Shd();
    public HashSet<BitSet> printedAlready=new HashSet<>();

    private void mhsPrint(BitSet edge) {
        int w = edge.nextSetBit(0);
        while (w != -1) {
            System.out.print(w);
            w = edge.nextSetBit(w);
            if (w != -1) System.out.print(" ");
        }
        System.out.println();
    }

    //dfs version of the website:http://research.nii.ac.jp/~uno/code/shd.html
//    public static void dfs(Hypergraph crit, BitSet uncov, Hypergraph H, Hypergraph T, BitSet S, int tail) {
//        if (uncov.isEmpty()) {
//            mhsPrint(S);
//            return;
//        }
//        // 和论文实现不同
//        for (int i = tail + 1; i < H.num_verts(); ++i) {
//            if (!shd.vertex_would_violate(crit, uncov, H, T, S, i)) {
//                HashMap<Integer, BitSet> temp = shd.update_crit_and_uncov(crit, uncov, H, T, S, i);
//                BitSet ss = (BitSet) S.clone();
//                ss.flip(i);
//                dfs(crit, uncov, H, T, ss, i);
//                shd.restore_crit_and_uncov(crit, uncov, S, temp, i);
//            }
//        }
//    }

    //dfs version of paper
    public void dfs(Hypergraph crit, BitSet uncov, Hypergraph H, Hypergraph T, BitSet S, BitSet cand) {
        if (uncov.isEmpty()) {
            printedAlready.add((BitSet)S.clone());
            return;
        }

        int i = uncov.nextSetBit(0);
        BitSet candCopy = (BitSet) cand.clone();
        BitSet c = (BitSet) candCopy.clone();
        c.and(H.getEdges().get(i));
        BitSetHelpFunc.subChange(candCopy, c);
        for (int j = c.nextSetBit(0); j != -1; j = c.nextSetBit(j + 1)) {
            if (!shd.vertex_would_violate(crit, uncov, T, S, j)) {
                HashMap<Integer, BitSet> temp = shd.update_crit_and_uncov(crit, uncov, T, S, j);
                BitSet ss = (BitSet) S.clone();
                ss.flip(j);
                dfs(crit, uncov, H, T, ss, candCopy);
                candCopy.set(j);
                shd.restore_crit_and_uncov(crit, uncov, S, temp, j);
            }
        }
    }

    private boolean any_edge_critical_after_i(Hypergraph crit, BitSet S, int i) {
        int w = S.nextSetBit(1);
        while (w != -1) {
            int fc = crit.getEdges().get(w).nextSetBit(0);
            if (fc >= i || fc == -1) return true;
            w = S.nextSetBit(w + 1);
        }
        return false;
    }

    public void rs(Hypergraph crit, BitSet uncov, Hypergraph H, Hypergraph T, BitSet S) {
        if (uncov.isEmpty()) {
            printedAlready.add((BitSet)S.clone());
            return;
        }

        int i = uncov.nextSetBit(0);
        BitSet edge = H.getEdges().get(i);
        int w = edge.nextSetBit(0);
        while (w != -1) {
            //这里使用vertex_would_violate是paper提到的剪枝策略(可以去掉这个if判断)，可以避免改变crit和uncov等操作
            if (!shd.vertex_would_violate(crit, uncov, T, S, w)) {
                HashMap<Integer, BitSet> hs = shd.update_crit_and_uncov(crit, uncov, T, S, w);
                //判断是不是s+v是不是s的孩子
                if (!any_edge_critical_after_i(crit, S, i)) {
                    S.set(w);
                    rs(crit, uncov, H, T, S);
                    S.clear(w);
                }
                shd.restore_crit_and_uncov(crit, uncov, S, hs, w);
            }
            w = edge.nextSetBit(w + 1);
        }

    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        /*"src/com/lychen/SHD/data/paper/example1.txt"
        1 2 8 10 14 17*/

        /*"src/example.dat"
        1 3
        1 4
        1 2
        2 3
        3 5*/

        Hypergraph H = new Hypergraph("src/com/lychen/SHD/data/paper/example1.txt");
        Hypergraph crit = new Hypergraph(H.num_edges(), H.num_verts());
        BitSet uncov = new BitSet(H.num_edges());
        BitSet S = new BitSet(H.num_verts());
        uncov.flip(0, H.num_edges());
        BitSet cand = new BitSet(H.num_verts());
        cand.flip(1, H.num_verts());

        TwoMethods sol=new TwoMethods();
        sol.dfs(crit.clone(), (BitSet)uncov.clone(), H, H.transpose(), (BitSet)S.clone(), cand);
        System.out.println("-----------------------");
        sol.rs(crit.clone(), (BitSet)uncov.clone(), H, H.transpose(), (BitSet)S.clone());
    }
}
