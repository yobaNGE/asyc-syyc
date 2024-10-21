package org.chiches.asycsyyc;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class MatrixMultiplication1 {

    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int size = matrixA.length;

        // Validate that both matrices are square and have the same dimensions
        if (size != matrixB.length || size != matrixA[0].length || size != matrixB[0].length) {
            throw new IllegalArgumentException("Both matrices must be square and have the same dimensions.");
        }

        // Prepare the result matrix
        int[][] resultMatrix = new int[size][size];

        // Convert 2D matrices to 1D arrays for Aparapi processing
        int[] flatMatrixA = flattenMatrix(matrixA, size);
        int[] flatMatrixB = flattenMatrix(matrixB, size);
        int[] flatResult = new int[size * size];

        // Define the Aparapi kernel for matrix multiplication
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId(); // Get the global thread ID
                int row = gid / size;
                int col = gid % size;
                int sum = 0;

                for (int k = 0; k < size; k++) {
                    sum += flatMatrixA[row * size + k] * flatMatrixB[k * size + col];
                }
                flatResult[gid] = sum;
            }
        };

        // Define the range (total number of work items, which is size * size)
        Range range = Range.create(size * size);

        // Execute the kernel
        kernel.execute(range);

        // Convert the flattened result back to a 2D matrix
        resultMatrix = unflattenMatrix(flatResult, size);

        // Clean up the kernel resources
        kernel.dispose();

        return resultMatrix;
    }

    // Utility method to flatten a 2D matrix into a 1D array
    private int[] flattenMatrix(int[][] matrix, int size) {
        int[] flatMatrix = new int[size * size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrix[i], 0, flatMatrix, i * size, size);
        }
        return flatMatrix;
    }

    // Utility method to convert a flattened 1D array back to a 2D matrix
    private int[][] unflattenMatrix(int[] flatMatrix, int size) {
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(flatMatrix, i * size, matrix[i], 0, size);
        }
        return matrix;
    }
}
