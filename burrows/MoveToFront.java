/* *****************************************************************************
 *  Name: Vikram Bhatt
 *  Date: May 20, 2021
 *  Description: Burrow-Wheeler Transformation
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] chars = new char[R];
        for (int i = 0; i < R; i++) {
            chars[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int position = 0;
            for (int i = 0; i < R; i++) {
                if (chars[i] == c) {
                    position = i;
                    BinaryStdOut.write(i, 8);
                    break;
                }
            }

            for (int i = position; i >= 1; i--) {
                chars[i] = chars[i - 1];
            }
            chars[0] = c;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] chars = new char[R];
        for (int i = 0; i < R; i++) {
            chars[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            char ch = chars[c];
            BinaryStdOut.write(ch);
            for (int i = c; i >= 1; i--) {
                chars[i] = chars[i - 1];
            }
            chars[0] = ch;
        }
        BinaryStdOut.close();
    }
}