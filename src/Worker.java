import java.util.concurrent.CountDownLatch;


public class Worker extends Thread {
    private final CountDownLatch latch;
    private double[][] A;
    private int startRow;
    private final int endRow;


    public Worker(double[][] A, int startRow, int endRow, CountDownLatch latch) {
        this.A = A;
        this.startRow = startRow;
        this.endRow = endRow;
        this.latch = latch;
    }
    /**
     * Swaps rows
     * Accessed by the worker threads
     *
     * @param A : Coefficient Matrix
     * @param i : first row to  be swapped
     * @param j : second row to  be swapped
     */
    public static void swapRows(double[][] A, int i, int j) {
        for (int k = 0; k <= A.length; k++) {
            double temp = A[i][k];
            A[i][k] = A[j][k];
            A[j][k] = temp;
        }
    }
    public void run() {
        int n = A.length;
        for (; startRow < endRow; startRow++) {
            int pointer = startRow;
            double value = A[pointer][startRow];
            // The pivots exits on diagonal elements <ie> A[i][i]
            // Pointer starting from that element to the last element in the matrix column
            // Get maximum value in the diagonal column

            for (int x = startRow + 1; x < endRow; x++) {
                if (Math.abs(A[x][startRow]) > value) {
                    value = A[x][startRow];
                    pointer = x;
                }
            }

            // latch.countDown();
            // Make sure the current row has the max val. at the pivot position.
            // Swap the origin row with MAX row

            if (startRow != pointer) {
                swapRows(A, startRow, pointer);
            }
            latch.countDown();
            // Iterate through the rows
            // f = A[row][c] / A[pivot][c]
            // Perform subtraction with multiple on the entire row
            // Populate the bottom triangular matrix with 0's
            for (int x = startRow + 1; x < endRow; x++) { //
                // Multiplier
                double f = A[x][startRow] / A[startRow][startRow];
                for (int y = startRow + 1; y <= n; y++)
                    A[x][y] -= A[startRow][y] * f;
                A[x][startRow] = 0;
            }
        }
    }
}




