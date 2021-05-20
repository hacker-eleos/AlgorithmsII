/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        int n = s.length();
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
        for (int i = 0; i < n; i++) {
            if (circularSuffixArray.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(s.charAt((circularSuffixArray.index(i) + n - 1) % n));
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int n = s.length();
        char[] t = s.toCharArray();
        char[] sortedChars = new char[n];
        int[] next = new int[n];
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++)
            count[t[i] + 1]++;
        // compute cumulates
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        // move data
        for (int i = 0; i < n; i++) {
            int p = count[t[i]]++;
            next[p] = i;
            sortedChars[p] = t[i];
        }
        for (int i = first, j = 0; j < n; j++, i = next[i]) {
            BinaryStdOut.write(sortedChars[i]);
        }
        BinaryStdOut.close();
    }
}