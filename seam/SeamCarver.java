/* *****************************************************************************
 *  Name: Vikram Bhatt
 *  Date: 16 April, 2021
 *  Description: Assignment
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;

public class SeamCarver {
    public static final double BORDER_PIXEL_ENERGY = 1000.0;
    private final Picture picture;
    private final int width;
    private final int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("null argument to constructor");
        }
        this.picture = new Picture(picture);
        this.width = this.picture.width();
        this.height = this.picture.height();
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        throw new IllegalArgumentException();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] cost = new double[height][width];
        int[][] backPointer = new int[height][width];
        for (int i = 0; i < width; i++) {
            cost[1][i] = energy(i, 1);
        }
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width; j++) {
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
                else if (j == width - 1) {
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
                    int x = MIN(cost[i - 1][j - 1], cost[i - 1][j], cost[i - 1][j + 1]);
                    cost[i][j] = Math
                            .min(Math.min(cost[i - 1][j - 1], cost[i - 1][j]), cost[i - 1][j + 1])
                            + energy(j, i);
                    backPointer[i][j] = j + x;
                }
            }
        }

        int minColumn = 0;
        for (int i = 0; i < width; i++) {
            if (cost[height - 1][minColumn] > cost[height - 1][i]) minColumn = i;
        }
        Stack<Integer> stack = new Stack<>();
        stack.push(minColumn);
        for (int i = height - 1; i > 0; i--) {
            stack.push(backPointer[i][minColumn]);
            minColumn = backPointer[i][minColumn];
        }

        int[] verticalSeam = new int[height];
        int i = 0;
        for (int integer : stack) {
            verticalSeam[i] = integer;
            i = i + 1;
        }
        return verticalSeam;

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

    private int MIN(double x, double y, double z) {
        double min = Math.min(Math.min(x, y), z);
        if (min == x) return -1;
        if (min == y) return 0;
        return 1;
    }

    private boolean isValidIndices(int x, int y) {
        if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) return true;
        return false;
    }

    private boolean isBorderIndex(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return true;
        return false;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkHorizontalSeam(seam);
    }

    private void checkHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (seam.length != width) throw new IllegalArgumentException("mismatch of height");
        checkValidPicture();
        for (int i = 0; i < seam.length - 1; i++) {
            if (!(seam[i] <= 0 && seam[i] <= height - 1))
                throw new IllegalArgumentException("horizontal seam invalid");
            if (!(seam[i + 1] <= 0 && seam[i + 1] <= height - 1))
                throw new IllegalArgumentException("horizontal seam invalid");
            if (!(Math.abs(seam[i] - seam[i + 1]) <= 1))
                throw new IllegalArgumentException("horizontal seam invalid");
        }
    }

    private void checkValidPicture() {
        if (height <= 1 || width <= 1) throw new IllegalArgumentException();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkVerticalSeam(seam);
    }

    private void checkVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (seam.length != height) throw new IllegalArgumentException("mismatch of height");
        checkValidPicture();
        for (int i = 0; i < seam.length - 1; i++) {
            if (!(seam[i] <= 0 && seam[i] <= width - 1))
                throw new IllegalArgumentException("vertical seam invalid");
            if (!(seam[i + 1] <= 0 && seam[i + 1] <= width - 1))
                throw new IllegalArgumentException("vertical seam invalid");
            if (!(Math.abs(seam[i] - seam[i + 1]) <= 1))
                throw new IllegalArgumentException("vertical seam invalid");
        }
    }

}