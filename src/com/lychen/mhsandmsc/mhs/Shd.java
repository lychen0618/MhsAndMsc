package com.lychen.mhsandmsc.mhs;

import java.util.BitSet;
import java.util.HashMap;

public class Shd {
    public boolean vertex_would_violate(Hypergraph crit, BitSet uncov, Hypergraph T, BitSet S, int v) {
        assert !S.get(v);
        assert crit.getEdges().get(v).isEmpty();

        BitSet testEdges = BitSetHelpFunc.sub(T.getEdges().get(v), uncov);

        int w = S.nextSetBit(0);
        while (w != -1) {
            if (BitSetHelpFunc.isSubsetOf(crit.getEdges().get(w), testEdges)) return true;
            w = S.nextSetBit(w + 1);
        }

        return false;
    }

    public HashMap<Integer, BitSet> update_crit_and_uncov(Hypergraph crit, BitSet uncov, Hypergraph T, BitSet S, int v) {
        assert !S.get(v);
        assert crit.getEdges().get(v).isEmpty();
        BitSet vEdges=(BitSet) T.getEdges().get(v);
        crit.getEdges().set(v,(BitSet) vEdges.clone());
        crit.getEdges().get(v).and(uncov);
        BitSetHelpFunc.subChange(uncov,vEdges);

        HashMap<Integer, BitSet> critMark=new HashMap<>();
        int w = S.nextSetBit(0);
        while (w != -1) {
            critMark.put(w,(BitSet) crit.getEdges().get(w).clone());
            critMark.get(w).and(vEdges);
            BitSetHelpFunc.subChange(crit.getEdges().get(w),vEdges);
            w = S.nextSetBit(w + 1);
        }
        return critMark;
    }

    public void restore_crit_and_uncov(Hypergraph crit, BitSet uncov, BitSet S, HashMap<Integer, BitSet> critMark, int v) {
        assert !S.get(v);
        assert !uncov.intersects(crit.getEdges().get(v));
        uncov.or(crit.getEdges().get(v));
        crit.getEdges().get(v).clear();
        int w = S.nextSetBit(0);
        while (w != -1) {
            crit.getEdges().get(w).or(critMark.get(w));
            w = S.nextSetBit(w + 1);
        }
    }
}
