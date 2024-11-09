package org.chiches.asycsyyc;

import java.io.*;

public class MatrixMultiplication {
    static final int MAX_N = 1553;
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


        Thread[] threads = new Thread[MAX_THREAD];
        long current = System.currentTimeMillis();
        for (int i = 0; i < MAX_THREAD; i++) {
            threads[i] = new Thread(new Threader(offset_i++));
            threads[i].start();
        }

        for (int i = 0; i < MAX_THREAD; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long time = System.currentTimeMillis() - current;
        System.out.println("Time multi thread: " + time + " ms");

        long timeSingle = System.currentTimeMillis();
        int[][] result = multiplyMatrices(matA, matB, MAX_N);
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
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int elem : row) {
                System.out.print(elem + " ");
            }
            System.out.println();
        }
        System.out.println();
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
    public static long[][] generateMatrixLong(int N) {
        long[][] matrix = new long[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (long) (Math.random() * 100);
            }
        }
        return matrix;
    }

    public static void serializeMatrixLong(long[][] matrix, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(matrix);
        }
    }

    public static long[][] deserializeMatrixLong(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (long[][]) ois.readObject();
        }
    }
}
