/* *****************************************************************************
 *  Name: Vikram Bhatt
 *  Date: 16 April, 2021
 *  Description: Assignment
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;

public class SeamCarver {
    private static final double BORDER_PIXEL_ENERGY = 1000.0;
    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("null argument to constructor");
        }
        this.picture = new Picture(picture);

    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // unit tests
    }

    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (height() == 1) return new int[width()];

        if (width() == 1) {
            int minRow = 0;
            for (int i = 0; i < height(); i++) {
                if (energy(0, minRow) > energy(0, i)) minRow = i;
            }
            return new int[] { minRow };
        }

        double[][] cost = new double[height()][width()];
        int[][] backPointer = new int[height()][width()];
        for (int j = 0; j < height(); j++) {
            cost[j][0] = energy(0, j);
        }
        for (int j = 1; j < width(); j++) {
            for (int i = 0; i < height(); i++) {
                if (i == 0) {
                    if (cost[i][j - 1] < cost[i + 1][j - 1]) {
                        cost[i][j] = cost[i][j - 1] + energy(j, i);
                        backPointer[i][j] = i;
                    }
                    else {
                        cost[i][j] = cost[i + 1][j - 1] + energy(j, i);
                        backPointer[i][j] = i + 1;
                    }
                }
                else if (i == height() - 1) {
                    if (cost[i][j - 1] < cost[i - 1][j - 1]) {
                        cost[i][j] = cost[i][j - 1] + energy(j, i);
                        backPointer[i][j] = i;
                    }
                    else {
                        cost[i][j] = cost[i - 1][j - 1] + energy(j, i);
                        backPointer[i][j] = i - 1;
                    }
                }
                else {
                    cost[i][j] = Math
                            .min(Math.min(cost[i - 1][j - 1], cost[i][j - 1]), cost[i + 1][j - 1])
                            + energy(j, i);
                    int x = minOf3(cost[i - 1][j - 1], cost[i][j - 1], cost[i + 1][j - 1]);
                    backPointer[i][j] = i + x;
                }
            }
        }

        int minRow = 0;
        for (int i = 0; i < height(); i++) {
            if (cost[minRow][width() - 1] > cost[i][width() - 1]) minRow = i;
        }
        Stack<Integer> stack = new Stack<>();
        stack.push(minRow);
        for (int i = width() - 1; i > 0; i--) {
            stack.push(backPointer[minRow][i]);
            minRow = backPointer[minRow][i];
        }
        assert stack.size() == width() : "invalid seam width()";
        int i = 0;
        int[] horizontalSeam = new int[width()];
        for (int integer : stack) {
            horizontalSeam[i++] = integer;
        }
        return horizontalSeam;
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!isValidIndices(x, y))
            throw new IllegalArgumentException("Illegal indices: (" + x + "," + y + ")");
        if (isBorderIndex(x, y)) return BORDER_PIXEL_ENERGY;
        Color leftNeighbour = picture.get(x - 1, y);
        Color rightNeighbour = picture.get(x + 1, y);
        double deltaXSquared = Math.pow(leftNeighbour.getRed() - rightNeighbour.getRed(), 2.0) +
                Math.pow(leftNeighbour.getGreen() - rightNeighbour.getGreen(), 2.0) +
                Math.pow(leftNeighbour.getBlue() - rightNeighbour.getBlue(), 2.0);
        Color upperNeighbour = picture.get(x, y - 1);
        Color bottomNeighbour = picture.get(x, y + 1);
        double deltaYSquared = Math.pow(upperNeighbour.getRed() - bottomNeighbour.getRed(), 2.0) +
                Math.pow(upperNeighbour.getGreen() - bottomNeighbour.getGreen(), 2.0) +
                Math.pow(upperNeighbour.getBlue() - bottomNeighbour.getBlue(), 2.0);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }

    private int minOf3(double x, double y, double z) {
        double min = Math.min(Math.min(x, y), z);
        if (min == x) return -1;
        if (min == y) return 0;
        return 1;
    }

    private boolean isValidIndices(int x, int y) {
        if ((x >= 0) && (x < width()) && (y >= 0) && (y < height())) return true;
        return false;
    }

    private boolean isBorderIndex(int x, int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) return true;
        return false;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (width() == 1) return new int[height()];
        if (height() == 1) {
            int minCol = 0;
            for (int i = 0; i < width(); i++) {
                if (energy(minCol, 0) > energy(i, 0)) minCol = i;
            }
            return new int[] { minCol };
        }
        double[][] cost = new double[height()][width()];
        int[][] backPointer = new int[height()][width()];
        for (int i = 0; i < width(); i++) {
            cost[0][i] = energy(i, 0);
        }
        for (int i = 1; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (j == 0) {
                    if (cost[i - 1][j] < cost[i - 1][j + 1]) {
                        backPointer[i][j] = j;
                        cost[i][j] = cost[i - 1][j] + energy(j, i);
                    }
                    else {
                        backPointer[i][j] = j + 1;
                        cost[i][j] = cost[i - 1][j + 1] + energy(j, i);
                    }
                }
                else if (j == width() - 1) {
                    if (cost[i - 1][j] < cost[i - 1][j - 1]) {
                        backPointer[i][j] = j;
                        cost[i][j] = cost[i - 1][j] + energy(j, i);
                    }
                    else {
                        backPointer[i][j] = j - 1;
                        cost[i][j] = cost[i - 1][j - 1] + energy(j, i);
                    }
                }
                else {
                    int x = minOf3(cost[i - 1][j - 1], cost[i - 1][j], cost[i - 1][j + 1]);
                    cost[i][j] = Math
                            .min(Math.min(cost[i - 1][j - 1], cost[i - 1][j]), cost[i - 1][j + 1])
                            + energy(j, i);
                    backPointer[i][j] = j + x;
                }
            }
        }

        int minColumn = 0;
        for (int i = 0; i < width(); i++) {
            if (cost[height() - 1][minColumn] > cost[height() - 1][i]) minColumn = i;
        }
        Stack<Integer> stack = new Stack<>();
        stack.push(minColumn);
        for (int i = height() - 1; i > 0; i--) {
            stack.push(backPointer[i][minColumn]);
            minColumn = backPointer[i][minColumn];
        }
        assert stack.size() == height() : "seam height() invalid";
        int[] verticalSeam = new int[height()];
        int i = 0;
        for (int integer : stack) {
            verticalSeam[i] = integer;
            i = i + 1;
        }
        return verticalSeam;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkHorizontalSeam(seam);
        Picture newPicture = new Picture(width(), height() - 1);
        for (int row = 0; row < height() - 1; row++) {
            for (int col = 0; col < width(); col++) {
                if (row < seam[col]) newPicture.set(col, row, this.picture.get(col, row));
                else if (row >= seam[col]) newPicture.set(col, row, this.picture.get(col, row + 1));
            }
        }
        this.picture = newPicture;
    }

    private void checkHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (seam.length != width())
            throw new IllegalArgumentException("horizontal seam width mismatch");
        if (height() <= 1) throw new IllegalArgumentException("picture height <= 1");
        for (int i = 0; i < seam.length; i++) {
            if (!(seam[i] >= 0 && seam[i] <= height() - 1))
                throw new IllegalArgumentException("horizontal seam invalid");
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (!(Math.abs(seam[i] - seam[i + 1]) <= 1))
                throw new IllegalArgumentException("horizontal seam invalid");
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkVerticalSeam(seam);
        Picture newPicture = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() - 1; col++) {
                if (col < seam[row]) newPicture.set(col, row, this.picture.get(col, row));
                else newPicture.set(col, row, this.picture.get(col + 1, row));
            }
        }
        this.picture = newPicture;
    }

    private void checkVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (seam.length != height())
            throw new IllegalArgumentException("vertical seam height mismatch");
        if (width() <= 1) throw new IllegalArgumentException("picture width <= 1");
        for (int i = 0; i < seam.length; i++) {
            if (!(seam[i] >= 0 && seam[i] <= width() - 1))
                throw new IllegalArgumentException("vertical seam invalid");
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (!(Math.abs(seam[i] - seam[i + 1]) <= 1))
                throw new IllegalArgumentException("vertical seam invalid");
        }
    }
}