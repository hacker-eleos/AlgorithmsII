/* *****************************************************************************
 *  Name: Vikram Bhatt
 *  Date: April 9, 2021
 *  Description: Assignment
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.List;

public class WordNet {

    // private final HashSet<String> nouns = new HashSet<String>();
    private final Digraph wordNet;
    private final SAP sap;
    private int numVertices = 0;
    private ST<String, Bag<Integer>> nouns = new ST<>();
    private ST<Integer, String> idToSynset = new ST<>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        parseSynSets(synsets);
        wordNet = new Digraph(numVertices);
        parseHypernyms(hypernyms);

        Topological topological = new Topological(wordNet);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException("Not a DAG");
        }
        int root = 0;
        int rootVertex = 0;
        for (int i = 0; i < wordNet.V(); i++) {
            if (!wordNet.adj(i).iterator().hasNext()) {
                root++;
                rootVertex = i;
            }
        }
        if (root != 1) throw new IllegalArgumentException("Not a rooted DAG");

        List<Integer> reachableVertices = new ArrayList<Integer>();
        for (int i = 0; i < wordNet.V(); i++) {
            if (i == rootVertex) continue;
            reachableVertices.add(i);
        }
        BreadthFirstDirectedPaths breadthFirstDirectedPaths = new BreadthFirstDirectedPaths(wordNet,
                                                                                            reachableVertices);
        for (int i = 0; i < wordNet.V(); i++) {
            if (i == rootVertex) continue;
            if (!breadthFirstDirectedPaths.hasPathTo(rootVertex))
                throw new IllegalArgumentException("Not a rooted DAG");
        }
        sap = new SAP(this.wordNet);
    }

    private void parseSynSets(String synsets) {
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] lineParts = in.readLine().split(",");
            int id = Integer.parseInt(lineParts[0]);
            idToSynset.put(id, lineParts[1]);
            for (String s : lineParts[1].split(" ")) {
                if (nouns.contains(s.strip())) {
                    nouns.get(s.strip()).add(id);
                }
                else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    nouns.put(s.strip(), bag);
                }
            }
            numVertices++;
        }
        in.close();
    }

    private void parseHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] parts = in.readLine().split(",");
            int synsetId = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                wordNet.addEdge(synsetId, Integer.parseInt(parts[i].strip()));
            }
        }
        in.close();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("./synsets.txt", "./hypernyms.txt");

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        if ((!isNoun(nounA)) && (!isNoun(nounB))) {
            throw new IllegalArgumentException();
        }
        Bag<Integer> bagNounAIds = nouns.get(nounA);
        Bag<Integer> bagNounBids = nouns.get(nounB);
        return this.sap.length(bagNounAIds, bagNounBids);
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nouns.contains(word);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Null arguments");
        }
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a noun in wordnet");
        Bag<Integer> bagNounAIds = nouns.get(nounA);
        Bag<Integer> bagNounBids = nouns.get(nounB);
        int ancestor = this.sap.ancestor(bagNounAIds, bagNounBids);
        return idToSynset.get(ancestor);
    }
}