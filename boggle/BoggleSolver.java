/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {

    private final FastPrefixTrieSET dictionary = new FastPrefixTrieSET();


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String s : dictionary) {
            this.dictionary.add(s);
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        SET<String> validWords = new SET<>();

        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                boolean[][] visited = new boolean[board.rows()][board.cols()];
                dfs(board, i, j, visited, "", validWords);
            }
        }
        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("null argument to scoreOf");
        if (!dictionary.contains(word)) return 0;
        int len = word.length();
        if (len < 3) return 0;
        if ((len == 3) || (len == 4)) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        return 11;
    }

    private void dfs(BoggleBoard board, int row, int col, boolean[][] visited, String prefix,
                     SET<String> validWords) {
        if (visited[row][col]) return;
        char c = board.getLetter(row, col);
        String word = prefix;
        if (c == 'Q') word += "QU";
        else word += c;
        if (!dictionary.hasPrefix(word)) return;
        if (word.length() > 2 && dictionary.contains(word)) validWords.add(word);
        visited[row][col] = true;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isValidPosition(board, row + i, col + j))
                    dfs(board, row + i, col + j, visited, word, validWords);
            }
        }
        visited[row][col] = false;
    }

    private boolean isValidPosition(BoggleBoard board, int i, int j) {
        if ((i >= 0) && (i <= board.rows() - 1) && (j >= 0) && (j <= board.cols() - 1)) return true;
        return false;
    }
}
