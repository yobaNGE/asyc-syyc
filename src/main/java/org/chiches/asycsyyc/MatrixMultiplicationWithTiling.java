package org.chiches.asycsyyc;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class MatrixMultiplicationWithTiling {

    private static final int TILE_SIZE = 8; // Define the tile size

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

        // Define the Aparapi kernel for matrix multiplication using tiling
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int row = getGlobalId(0);  // Row index of result
                int col = getGlobalId(1);  // Column index of result

                int sum = 0;

                // Loop over all tiles
                for (int t = 0; t < size / TILE_SIZE; t++) {

                    // Load one tile from matrix A and one tile from matrix B into shared memory
                    int[] tileA = new int[TILE_SIZE * TILE_SIZE];
                    int[] tileB = new int[TILE_SIZE * TILE_SIZE];

                    for (int i = 0; i < TILE_SIZE; i++) {
                        tileA[i * TILE_SIZE + col] = flatMatrixA[row * size + t * TILE_SIZE + i];
                        tileB[row * TILE_SIZE + i] = flatMatrixB[(t * TILE_SIZE + i) * size + col];
                    }

                    // Synchronize threads to ensure the tiles are fully loaded
                    localBarrier();

                    // Compute the partial result for this tile
                    for (int k = 0; k < TILE_SIZE; k++) {
                        sum += tileA[k * TILE_SIZE + col] * tileB[row * TILE_SIZE + k];
                    }

                    // Synchronize threads before moving to the next tile
                    localBarrier();
                }

                flatResult[row * size + col] = sum;
            }
        };

        // Define the range for the 2D grid (work-item grid)
        Range range = Range.create2D(size, size);

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
