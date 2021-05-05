/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {
        if (wordnet == null) {
            throw new IllegalArgumentException("null argument to constructor");
        }
        this.wordNet = wordnet;
    }

    public static void main(String[] args)  // see test client below
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        if (nouns == null || nouns.length < 2) {
            throw new IllegalArgumentException();
        }
        int id = -1;
        int maxDistance = Integer.MIN_VALUE;
        int[] distance = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                if (i == j) continue;
                distance[i] += wordNet.distance(nouns[i], nouns[j]);
            }
            if (distance[i] > maxDistance) {
                maxDistance = distance[i];
                id = i;
            }
        }
        return nouns[id];
    }

}