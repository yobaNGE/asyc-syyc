package org.chiches.asycsyyc;

import java.io.IOException;

import static org.chiches.asycsyyc.MatrixMultiplication.generateMatrixLong;

public class MatrixMinor {
    static final int MAX = 11;
    static final int MAX_THREAD = 12;
    static long[][] matA = new long[MAX][MAX];
    static int step_i = 0;


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String fileNameA = "matrixmin.ser";
        boolean generateMatrix = true;
        if (generateMatrix) {
            long[][] matrixA = generateMatrixLong(MAX, 100);
            try {
                MatrixMultiplication.serializeMatrixLong(matrixA, fileNameA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        matA = MatrixMultiplication.deserializeMatrixLong(fileNameA);
        MatrixMultiplication.printMatrix(matA);
        long timeStartSingle = System.currentTimeMillis();
        System.out.println(STR."Determinant: \{calculateDeterminant(matA)}");
        long timeEndSingle = System.currentTimeMillis();
        System.out.println(STR."Single thread time: \{timeEndSingle - timeStartSingle}ms");

        matA = MatrixMultiplication.deserializeMatrixLong(fileNameA);

        long startTime = System.currentTimeMillis();
        long determinant = MatrixDeterminant.calculateDeterminant(matA);
        long endTime = System.currentTimeMillis();
        System.out.println(STR."Determinant: \{determinant}");
        System.out.println(STR."Execution Time: \{(endTime - startTime)} ms");
    }

    public static long calculateDeterminant(long[][] matrix) {
        int size = matrix.length;
        if (size == 1) {
            return matrix[0][0];
        } else if (size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            long determinant = 0;
            for (int col = 0; col < size; col++) {
                long[][] minor = getMinor(matrix, 0, col);
                long minorDeterminant = calculateDeterminant(minor);
                long cofactor = ((col % 2 == 0) ? 1 : -1) * matrix[0][col] * minorDeterminant;
                determinant += cofactor;
            }
            return determinant;
        }
    }

    private static long[][] getMinor(long[][] matrix, int excludeRow, int excludeCol) {
        int size = matrix.length;
        long[][] minor = new long[size - 1][size - 1];
        int rowOffset = 0;

        for (int i = 0; i < size; i++) {
            if (i == excludeRow) {
                rowOffset = -1;
                continue;
            }
            int colOffset = 0;
            for (int j = 0; j < size; j++) {
                if (j == excludeCol) {
                    colOffset = -1;
                    continue;
                }
                minor[i + rowOffset][j + colOffset] = matrix[i][j];
            }
        }
        return minor;
    }
}
