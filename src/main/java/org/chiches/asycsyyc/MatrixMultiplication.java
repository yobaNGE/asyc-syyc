package org.chiches.asycsyyc;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {
    //static final int N = 997;
    static final int MAX = 1553;
    static final int MAX_THREAD = 12;
    static int[][] matA = new int[MAX][MAX];
    static int[][] matB = new int[MAX][MAX];
    static int[][] matC = new int[MAX][MAX];
    static int step_i = 0;


    static class Worker implements Runnable {
        int i;

        Worker(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            for (int i = this.i; i < MAX; i += MAX_THREAD) {
                for (int j = 0; j < MAX; j++) {
                    for (int k = 0; k < MAX; k++) {
                        matC[i][j] += matA[i][k] * matB[k][j];
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        //int N = 641;
        String fileNameA = "matrixA.ser", fileNameB = "matrixB.ser";
        int[][] matrixA = generateMatrix(MAX);
        int[][] matrixB = generateMatrix(MAX);
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
            threads[i] = new Thread(new Worker(step_i++));
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
        int[][] result = multiplyMatrices(matA, matB, MAX);
        long timeSingleEnd = System.currentTimeMillis() - timeSingle;
        System.out.println("Time single thread: " + timeSingleEnd + " ms");

    }

    // Метод для умножения матриц
    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB, int N) {
        int[][] result = new int[N][N];

        // Основной алгоритм умножения
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }

    public static int[][] multiplyMatricesThread(int[][] matrixA, int[][] matrixB, int N) {
        int[][] result = new int[N][N];

        // Основной алгоритм умножения
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                MyThread myThread = new MyThread();
                //System.out.println(result[i][j]);
                myThread.run(result, matrixA, matrixB, i, j, N);
                //System.out.println(result[i][j]);


//                final int row = i;
//                final int col = j;
//                Thread virtualThread = Thread.startVirtualThread(() -> {
//                    //System.out.println("Running task in a virtual thread: " + Thread.currentThread().getName());
//                    for (int k = 0; k < N; k++) {
//                        result[row][col] += matrixA[row][k] * matrixB[k][col];
//                    }
//                });
            }
        }
        return result;
    }

    // Метод для вывода матрицы
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int elem : row) {
                System.out.print(elem + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Метод для генерации матрицы NxN
    public static int[][] generateMatrix(int N) {
        int[][] matrix = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (int) (Math.random() * 100); // Заполнение случайными числами от 0 до 99
            }
        }
        return matrix;
    }

    // Метод для сериализации матрицы в файл
    public static void serializeMatrix(int[][] matrix, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(matrix);
        }
    }

    // Метод для десериализации матрицы из файла
    public static int[][] deserializeMatrix(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (int[][]) ois.readObject();
        }
    }

}
