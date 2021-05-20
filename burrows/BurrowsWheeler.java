/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LSD;

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
        while (!BinaryStdIn.isEmpty()) {
            String s = BinaryStdIn.readString();
            CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);
            for (int i = 0; i < s.length(); i++) {
                if (circularSuffixArray.index(i) == 0) {
                    BinaryStdOut.write(i);
                    break;
                }
            }
            String[] originalSuffixes = new String[s.length()];
            for (int i = 0; i < s.length(); i++) {
                originalSuffixes[i] = s.substring(i) + s.substring(0, i);
            }
            LSD.sort(originalSuffixes, s.length());
            char[] t = new char[s.length()];
            for (int i = 0; i < s.length(); i++) {
                t[i] = originalSuffixes[i].charAt(s.length() - 1);
            }
            String ouput = new String(t);
            BinaryStdOut.write(ouput);
            BinaryStdOut.close();
        }
        BinaryStdIn.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
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

            StringBuilder str = new StringBuilder();
            for (int i = first; i != 0; i = next[i]) {
                str.append(sortedChars[i]);
            }
            BinaryStdOut.write(str.toString());
            BinaryStdOut.close();
        }
        BinaryStdIn.close();

    }
}