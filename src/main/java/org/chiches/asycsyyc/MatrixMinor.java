package org.chiches.asycsyyc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.chiches.asycsyyc.MatrixMultiplication.generateMatrix;
import static org.chiches.asycsyyc.MatrixMultiplication.generateMatrixLong;

public class MatrixMinor {
    static final int MAX = 5;
    static final int MAX_THREAD = 12;
    static long[][] matA = new long[MAX][MAX];
    static int step_i = 0;



    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        String fileNameA = "matrixmin.ser";
        long[][] matrixA = generateMatrixLong(MAX);
        try {
            MatrixMultiplication.serializeMatrixLong(matrixA, fileNameA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        matA = MatrixMultiplication.deserializeMatrixLong(fileNameA);
        //MatrixMultiplication.printMatrix(matA);
        long timeStartSingle = System.currentTimeMillis();
        System.out.println(determinant(matA));
        long timeEndSingle = System.currentTimeMillis();
        System.out.println("Single thread time: " + (timeEndSingle - timeStartSingle) + "ms");


        matA = MatrixMultiplication.deserializeMatrixLong(fileNameA);
        //MatrixMultiplication.printMatrix(matA);
       // long multiThreadStart = System.currentTimeMillis();
        DeterminantCalculatorConfigurable calculatorConfigurable = new DeterminantCalculatorConfigurable(MAX, MAX_THREAD, matA);
        calculatorConfigurable.calculateDeterminant();
        //System.out.println("Multi-thread time: " + (System.currentTimeMillis() - multiThreadStart) + "ms");
    }

    public static long getMinor(long[][] matrix, int row, int col) {
        long[][] minor = new long[matrix.length - 1][matrix.length - 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i != row && j != col) {
                    minor[i < row ? i : i - 1][j < col ? j : j - 1] = matrix[i][j];
                }
            }
        }
        return determinant(minor);
    }

    public static long determinant(long[][] minor) {
        if (minor.length == 1) {
            return minor[0][0];
        }
        if (minor.length == 2) {
            return (long) minor[0][0] * (long) minor[1][1] - (long) minor[0][1] * (long) minor[1][0];
        }
        long det = 0;
        for (int i = 0; i < minor.length; i++) {
            det += (i % 2 == 0 ? 1 : -1) * minor[0][i] * getMinor(minor, 0, i);
        }
        return det;
    }

    public static void begin() {

    }
}
