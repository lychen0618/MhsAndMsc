package com.lychen.mhsandmsc.mhs;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

public class Hypergraph implements Cloneable{
    private int numOfVertex;
    private ArrayList<BitSet> edges;

    public Hypergraph(int vertexNum, int edgeNum) {
        numOfVertex = vertexNum;
        edges = new ArrayList<>();
        for (int i = 0; i < edgeNum; ++i) {
            BitSet bs = new BitSet(vertexNum);
            edges.add(bs);
        }
    }


    public Hypergraph(ArrayList<BitSet> edges) {
        if (edges.size() > 0) {
            numOfVertex = edges.get(0).length();
        } else {
            numOfVertex = 0;
        }
        this.edges = edges;
    }


    public Hypergraph(String pathName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathName));

        edges = new ArrayList<>();
        ArrayList<ArrayList<Integer>> edgeByIndices = new ArrayList<>();
        int maxVertext = 0;
        int numOfEdges = 0;

        while (true) {
            String line = br.readLine();
            if (line == null) break;
            numOfEdges++;
            ArrayList<Integer> arr = new ArrayList<>();
            String[] s = line.split(" ");
            for (String ss : s) {
                int vertexIndex = Integer.parseInt(ss);
                arr.add(vertexIndex);
                maxVertext = Math.max(maxVertext, vertexIndex);
            }
            edgeByIndices.add(arr);
        }

        numOfVertex = maxVertext + 1;
        for (int i = 0; i < numOfEdges; ++i) {
            BitSet edge = new BitSet(numOfVertex);
            edges.add(edge);
        }

        for (int e = 0; e < numOfEdges; ++e) {
            for (Integer i : edgeByIndices.get(e)) {
                edges.get(e).set(i);
            }
        }
    }

    public Hypergraph clone() throws CloneNotSupportedException {
        Hypergraph cloned=(Hypergraph)super.clone();
        cloned.edges=(ArrayList)edges.clone();
        return cloned;
    }

    public int num_verts() {
        return numOfVertex;
    }

    public int num_edges() {
        return edges.size();
    }

    public ArrayList<BitSet> getEdges() {
        return edges;
    }

    private void add_edge(BitSet edge,int v, boolean testSimplicity) {
        if (testSimplicity) {
            ;
        }

        edges.set(v,edge);

        if (numOfVertex == 0) numOfVertex = edges.size();
//        else if (numOfVertex != edge.size()) {
//            System.out.println("Attempted to add edge of invalid size!");
//        }
    }

    private void reserve_edge_capacity(int numOfEdge) {

    }

    private BitSet edges_containing_vertex(int v) {
        int n = num_edges();
        BitSet re = new BitSet(n);
        for (int edgeIndex = 0; edgeIndex < n; ++edgeIndex) {
            if (edges.get(edgeIndex).get(v)) re.set(edgeIndex);
        }
        return re;
    }

    public Hypergraph transpose() {
        Hypergraph T = new Hypergraph(num_edges(),num_verts());

        for (int v = 0; v < numOfVertex; ++v) {
            T.add_edge(edges_containing_vertex(v),v, false);
        }

        return T;
    }


    public static void main(String[] args) throws IOException {
        Hypergraph H = new Hypergraph("src/example.dat");
        System.out.println(H.num_verts() + " " + H.num_edges());
    }
}
