package org.chiches.asycsyyc;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeterminantCalculatorLong {
    static int matrixSize;
    static int threadCount;
    static long[][] mat;
    static long[] det;

    public DeterminantCalculatorLong(int matrixSize, int threadCount, long[][] mat) {
        DeterminantCalculatorLong.matrixSize = matrixSize;
        DeterminantCalculatorLong.threadCount = threadCount;
        DeterminantCalculatorLong.mat = mat;
        det = new long[matrixSize];
    }

    public void calculateDeterminant() throws InterruptedException {
        long multiThreadStart = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < matrixSize; i++) {
            det[i] = mat[0][i];
            final int col = i;
            Runnable task = () -> det[col] *= determinant(getSubMatrix(mat, matrixSize, col), matrixSize - 1);
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        long detFin = 0;
        for (int i = 0; i < matrixSize; i++) {
            if (i % 2 == 0) {
                detFin += det[i];
            } else {
                detFin -= det[i];
            }
        }
        System.out.println("Determinant: " + detFin);
        System.out.println("Multi-thread time: " + (System.currentTimeMillis() - multiThreadStart) + "ms");
    }

    public static long determinant(long[][] matrix, int size) {
        if (size == 1) {
            return matrix[0][0];
        }
        if (size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }

        long det = 0;
        for (int col = 0; col < size; col++) {
            long[][] subMatrix = getSubMatrix(matrix, size, col);
            det += ((col % 2 == 0 ? 1 : -1) * matrix[0][col] * determinant(subMatrix, size - 1));
        }
        return det;
    }

    public static long[][] getSubMatrix(long[][] matrix, int size, int excludeCol) {
        long[][] subMatrix = new long[size - 1][size - 1];
        for (int i = 1; i < size; i++) {
            int k = 0;
            for (int j = 0; j < size; j++) {
                if (j != excludeCol) {
                    subMatrix[i - 1][k++] = matrix[i][j];
                }
            }
        }
        return subMatrix;
    }
}
