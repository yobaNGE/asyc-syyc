package org.chiches.asycsyyc;

import java.io.IOException;

import static org.chiches.asycsyyc.MatrixMultiplication.generateMatrix;

public class MatrixMinor {
    static final int MAX = 10;
    static final int MAX_THREAD = 12;
    static int[][] matA = new int[MAX][MAX];
    static int step_i = 0;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String fileNameA = "matrix.ser";
        int[][] matrixA = generateMatrix(MAX);
        try {
            MatrixMultiplication.serializeMatrix(matrixA, fileNameA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        matA = MatrixMultiplication.deserializeMatrix(fileNameA);
        MatrixMultiplication.printMatrix(matA);
        long timeStartSingle = System.currentTimeMillis();
        System.out.println(determinant(matA));
        long timeEndSingle = System.currentTimeMillis();
        System.out.println("Single thread time: " + (timeEndSingle - timeStartSingle) + "ms");
    }
    public static long getMinor(int[][] matrix, int row, int col) {
        int[][] minor = new int[matrix.length - 1][matrix.length - 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i != row && j != col) {
                    minor[i < row ? i : i - 1][j < col ? j : j - 1] = matrix[i][j];
                }
            }
        }
        return determinant(minor);
    }
    public static long determinant(int[][] minor) {
        if (minor.length == 1) {
            return minor[0][0];
        }
        if (minor.length == 2) {
            return minor[0][0] * minor[1][1] - minor[0][1] * minor[1][0];
        }
        long det = 0;
        for (int i = 0; i < minor.length; i++) {
            det += (i % 2 == 0 ? 1 : -1) * minor[0][i] * getMinor(minor, 0, i);
        }
        return det;
    }

}
