package org.chiches.asycsyyc;

import java.io.*;

public class MatrixMultiplicationGpuMain {
    static final int MAX_N = 800;
    static int[][] matA = new int[MAX_N][MAX_N];
    static int[][] matB = new int[MAX_N][MAX_N];
    static int[][] matResult = new int[MAX_N][MAX_N];

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String fileNameA = "matrixA.ser", fileNameB = "matrixB.ser";
        int[][] matrixA = generateMatrix(MAX_N);
        int[][] matrixB = generateMatrix(MAX_N);
        try {
            serializeMatrix(matrixA, fileNameA);
            serializeMatrix(matrixB, fileNameB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        matA = deserializeMatrix(fileNameA);
        matB = deserializeMatrix(fileNameB);

        long current = System.currentTimeMillis();
        MatrixMultiplicationKernely.multiply(matA, matB);
        long time = System.currentTimeMillis() - current;
        System.out.println("Time GPU: " + time + " ms");

        long timeSingle = System.currentTimeMillis();
        //int[][] result = multiplyMatrices(matA, matB, MAX_N);
        long timeSingleEnd = System.currentTimeMillis() - timeSingle;
        System.out.println("Time single thread: " + timeSingleEnd + " ms");
    }

    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB, int N) {
        int[][] result = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }

    public static int[][] generateMatrix(int N) {
        int[][] matrix = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (int) (Math.random() * 100);
            }
        }
        return matrix;
    }

    public static void serializeMatrix(int[][] matrix, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(matrix);
        }
    }

    public static int[][] deserializeMatrix(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (int[][]) ois.readObject();
        }
    }
}
