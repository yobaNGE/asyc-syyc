package org.chiches.asycsyyc;

public class MyThread extends Thread {
    public void run(int [][]result, int [][]matrixA, int [][]matrixB, int i, int j, int N) {
        for (int k = 0; k < N; k++) {
            result[i][j] += matrixA[i][k] * matrixB[k][j];
        }
    }
}