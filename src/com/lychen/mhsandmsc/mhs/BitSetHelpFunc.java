package com.lychen.mhsandmsc.mhs;

import java.util.BitSet;

public class BitSetHelpFunc {
    public static BitSet sub(BitSet a, BitSet b) {
        BitSet re = new BitSet();
        int w = a.nextSetBit(0);
        while (w != -1) {
            if (!b.get(w)) re.set(w);
            w = a.nextSetBit(w + 1);
        }
        return re;
    }

    public static void subChange(BitSet a, BitSet b) {
        int w = a.nextSetBit(0);
        while (w != -1) {
            if (a.get(w) && b.get(w)) a.clear(w);
            w = a.nextSetBit(w + 1);
        }
    }

    public static boolean isSubsetOf(BitSet a, BitSet b) {
        int w = a.nextSetBit(0);
        while (w != -1) {
            if (!b.get(w)) return false;
            w = a.nextSetBit(w + 1);
        }
        return true;
    }
}
