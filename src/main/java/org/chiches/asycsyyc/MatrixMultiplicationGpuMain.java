package org.chiches.asycsyyc;

import java.io.*;

public class MatrixMultiplicationGpuMain {
    static final int MAX_N = 4096;
    static final int MAX_THREAD = 12;
    static int[][] matA = new int[MAX_N][MAX_N];
    static int[][] matB = new int[MAX_N][MAX_N];
    static int[][] matResult = new int[MAX_N][MAX_N];
    static int offset_i = 0;
    static class Threader implements Runnable {
        int i;

        Threader(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            for (int i = this.i; i < MAX_N; i += MAX_THREAD) {
                for (int j = 0; j < MAX_N; j++) {
                    for (int k = 0; k < MAX_N; k++) {
                        matResult[i][j] += matA[i][k] * matB[k][j];
                    }
                }
            }
        }
    }
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
        MatrixMultiplication1 matrixMultiplication1 = new MatrixMultiplication1();
        //int[][] result = matrixMultiplication1.multiply(matA, matB);
        //MatrixMultiplicationKernely.multiply(matA, matB);
        long time = System.currentTimeMillis() - current;
        System.out.println("Time GPU: " + time + " ms");


        long timetiled = System.currentTimeMillis();
        MatrixMultiplicationWithTiling matrixMultiplicationTiled = new MatrixMultiplicationWithTiling();
        int[][] resultTiled = matrixMultiplicationTiled.multiply(matA, matB);
        long timeTiled = System.currentTimeMillis() - timetiled;
        System.out.println("Time GPU Tiled: " + timeTiled + " ms");



        Thread[] threads = new Thread[MAX_THREAD];
        long current1 = System.currentTimeMillis();
        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i] = new Thread(new MatrixMultiplication.Threader(offset_i++));
            threads[i].start();
        }

        for (int i = 0; i < MAX_THREAD; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long time1 = System.currentTimeMillis() - current1;
        System.out.println("Time multi thread: " + time1 + " ms");
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
