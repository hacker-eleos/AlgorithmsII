/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Insertion;
import edu.princeton.cs.algs4.StdIn;

public class CircularSuffixArray {
    private final int n;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        n = s.length();
        String[] originalSuffixes = new String[n];
        for (int i = 0; i < n; i++) {
            originalSuffixes[i] = s.substring(i) + s.substring(0, i);
        }
        index = Insertion.indexSort(originalSuffixes);
    }

    // unit testing (required)
    public static void main(String[] args) {
        StringBuilder st = new StringBuilder();
        while (!StdIn.isEmpty()) {
            st.append(StdIn.readString());
        }
        String s = st.toString();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        for (int i = 0; i < s.length(); i++) {
            System.out.println(circularSuffixArray.index(i));
        }
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (!((i >= 0) && (i < length()))) throw new IllegalArgumentException();
        return index[i];

    }

    // length of s
    public int length() {
        return n;
    }

}