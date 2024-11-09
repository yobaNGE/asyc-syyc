package org.chiches.asycsyyc;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DeterminantCalculatorConfigurable {
    static int matrixSize;
    static int threadCount;
    static long[][] mat;
    static long[] det;

    public DeterminantCalculatorConfigurable(int matrixSize, int threadCount, long[][] mat) {
        DeterminantCalculatorConfigurable.matrixSize = matrixSize;
        DeterminantCalculatorConfigurable.threadCount = threadCount;
        DeterminantCalculatorConfigurable.mat = mat;
        det = new long[matrixSize];
    }

    public void calculateDeterminant() throws InterruptedException {
        long multiThreadStart = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < matrixSize; i++) {
            det[i] = mat[0][i];
            final int col = i; // Final variable to use inside the lambda

            Runnable task = () -> det[col] *= determinant(getSubMatrix(mat, matrixSize, col), matrixSize - 1);
            //Thread thread = new Thread(task);
            //threads.add(thread);
            executor.submit(task);  // Submit the task to the executor
        }

        // Wait for all threads to complete
//        for (Thread thread : threads) {
//            thread.join();
//        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        // Calculate the final determinant
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

    // Recursive method to find the determinant of a matrix
    public static long determinant(long[][] mat2, int s) {
        if (s == 2) {
            return mat2[0][0] * mat2[1][1] - mat2[0][1] * mat2[1][0];
        } else {
            long[][] mat1 = new long[s - 1][s - 1];
            long[][] mat3 = new long[s - 1][s - 1];
            long[][] mat4 = new long[s - 1][s - 1];

            for (int i = 1; i < s; i++) {
                int k = 0, l = 0, m = 0;
                for (int j = 0; j < s; j++) {
                    if (j != 0) mat1[i - 1][k++] = mat2[i][j];
                    if (j != 1) mat3[i - 1][l++] = mat2[i][j];
                    if (j != 2) mat4[i - 1][m++] = mat2[i][j];
                }
            }
            return (mat2[0][0] * determinant(mat1, s - 1) -
                    mat2[0][1] * determinant(mat3, s - 1) +
                    mat2[0][2] * determinant(mat4, s - 1));
        }
    }

    // Helper method to extract a submatrix
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
