/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("null argument to SAP constructor");
        }
        this.G = new Digraph(G);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if ((!isValid(v)) || (!isValid(w))) throw new IllegalArgumentException();
        int ancestor = ancestor(v, w);
        int pathLength = -1;
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(this.G, w);
        if (ancestor == -1) return pathLength;
        pathLength = bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
        return pathLength;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if ((!isValid(v)) || (!isValid(w))) throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(this.G, w);
        int shortestAncestor = -1;
        int shortestAncestorDistance = Integer.MAX_VALUE;
        Bag<Integer> ancestors = new Bag<Integer>();
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) ancestors.add(i);
        }
        for (Integer ancestor : ancestors) {
            int distanceToAncestor = bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
            if (distanceToAncestor < shortestAncestorDistance) {
                shortestAncestor = ancestor;
                shortestAncestorDistance = distanceToAncestor;
            }
        }
        return shortestAncestor;
    }

    private boolean isValid(int v) {
        return (v >= 0) && (v <= this.G.V() - 1);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if ((!isValid(v)) || (!isValid(w))) throw new IllegalArgumentException();
        int ancestor = ancestor(v, w);
        int pathLength = -1;
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(this.G, w);
        if (ancestor == -1) return pathLength;
        pathLength = bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
        return pathLength;
    }

    private boolean isValid(Iterable<Integer> v) {
        for (Integer integer : v) {
            if (integer == null) return false;
        }
        return true;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null && w == null) throw new IllegalArgumentException();
        if ((!isValid(v)) || (!isValid(w))) throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(this.G, w);
        int shortestAncestor = -1;
        int shortestAncestorDistance = Integer.MAX_VALUE;
        Bag<Integer> ancestors = new Bag<Integer>();
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) ancestors.add(i);
        }
        for (Integer ancestor : ancestors) {
            int distanceToAncestor = bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
            if (distanceToAncestor < shortestAncestorDistance) {
                shortestAncestor = ancestor;
                shortestAncestorDistance = distanceToAncestor;
            }
        }
        return shortestAncestor;
    }
}
