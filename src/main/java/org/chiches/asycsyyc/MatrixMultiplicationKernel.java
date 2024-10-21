package org.chiches.asycsyyc;

import com.aparapi.Kernel;
import com.aparapi.Range;

// Kernel class for performing matrix multiplication on GPU

class MatrixMultiplicationKernely extends Kernel {
    private final int[] A; // Input matrix A
    private final int[] B; // Input matrix B
    private final int[] C; // Result matrix C
    private final int N;   // Matrix dimension (NxN)

    // Constructor to initialize matrices and size
    public MatrixMultiplicationKernely(int[] A, int[] B, int[] C, int N) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.N = N;
    }

    @Override
    public void run() {
        int gid = getGlobalId(0); // Get global thread ID for parallel execution
        int row = gid / N;
        int col = gid % N;

        int sum = 0;
        for (int k = 0; k < N; k++) {
            sum += A[row * N + k] * B[k * N + col];
        }
        C[row * N + col] = sum; // Set result in matrix C
    }
    public static int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int N = matrixA.length; // Size of the NxN matrices

        // Convert 2D matrix to 1D arrays for GPU processing
        int[] A = new int[N * N];
        int[] B = new int[N * N];
        int[] C = new int[N * N]; // Result matrix in 1D form

        // Flatten the matrices into 1D arrays
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i * N + j] = matrixA[i][j];
                B[i * N + j] = matrixB[i][j];
            }
        }

        // Create an instance of the kernel
        MatrixMultiplicationKernely kernel = new MatrixMultiplicationKernely(A, B, C, N);

        // Define the execution range (N * N work-items, one for each matrix element)
        Range range = Range.create(N * N);
        // Execute the kernel
        kernel.execute(range);

        // Convert the result back to 2D array
        int[][] resultMatrix = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                resultMatrix[i][j] = C[i * N + j];
            }
        }

        // Dispose of the kernel to free up GPU resources
        kernel.dispose();

        return resultMatrix;

    }
}

        // Function to multiply two square matrices using Aparapi on the GPU
