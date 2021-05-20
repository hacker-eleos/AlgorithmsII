/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;

import java.util.Arrays;

public class CircularSuffixArray {

    private final int n;
    private final Integer[] index;
    private final char[] chars;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        n = s.length();
        index = new Integer[n];
        chars = new char[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
            chars[i] = s.charAt(i);
        }
        Arrays.sort(index, (o1, o2) -> {
            for (int i = 0; i < n; i++) {
                char c1 = chars[(i + o1) % n];
                char c2 = chars[(i + o2) % n];
                if (c1 > c2) return 1;
                if (c1 < c2) return -1;
            }
            return 0;
        });
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
            System.out.print(circularSuffixArray.index(i) + " ");
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