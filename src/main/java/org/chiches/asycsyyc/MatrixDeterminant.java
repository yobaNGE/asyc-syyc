package org.chiches.asycsyyc;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MatrixDeterminant {
    // Recursive Task for determinant calculation
    static class DeterminantTask extends RecursiveTask<Long> {
        private final long[][] matrix;
        private final int size;

        public DeterminantTask(long[][] matrix) {
            this.matrix = matrix;
            this.size = matrix.length;
        }

        @Override
        protected Long compute() {
            if (size == 1) {
                return matrix[0][0];
            } else if (size == 2) {
                // Determinant formula for 2x2 matrix
                return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
            } else {
                // Laplace expansion with ForkJoinPool for parallelism
                long determinant = 0;
                DeterminantTask[] subtasks = new DeterminantTask[size];
                for (int j = 0; j < size; j++) {
                    long[][] minor = removeRowAndColumn(matrix, 0, j);
                    subtasks[j] = new DeterminantTask(minor);
                    subtasks[j].fork(); // Start task asynchronously
                }

                for (int j = 0; j < size; j++) {
                    long coefficient = matrix[0][j] * ((j % 2 == 0) ? 1 : -1);
                    determinant += coefficient * subtasks[j].join(); // Wait for result
                }

                return determinant;
            }
        }
    }

    // Public method to calculate determinant using ForkJoinPool
    public static long calculateDeterminant(long[][] matrix) {
        ForkJoinPool pool = new ForkJoinPool();
        DeterminantTask task = new DeterminantTask(matrix);
        return pool.invoke(task);
    }

    // Helper method to create a minor matrix (removing one row and column)
    private static long[][] removeRowAndColumn(long[][] matrix, int row, int col) {
        int n = matrix.length;
        long[][] minor = new long[n - 1][n - 1];
        for (int i = 0, mi = 0; i < n; i++) {
            if (i == row) continue;
            for (int j = 0, mj = 0; j < n; j++) {
                if (j == col) continue;
                minor[mi][mj++] = matrix[i][j];
            }
            mi++;
        }
        return minor;
    }
}

