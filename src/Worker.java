import java.util.concurrent.CountDownLatch;


public class Worker extends Thread {
    private final CountDownLatch countDownLatch;
    private double[][] matrix;
    private int firstRow;
    private final int lastRow;


    public Worker(double[][] matrix, int firstRow, int lastRow, CountDownLatch countDownLatch) {
        this.matrix = matrix;
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.countDownLatch = countDownLatch;
    }

    public static void transportRows(double[][] matrix, int coefficientI, int coefficientJ) {
        for (int k = 0; k <= matrix.length; k++) {
            double temp = matrix[coefficientI][k];
            matrix[coefficientI][k] = matrix[coefficientJ][k];
            matrix[coefficientJ][k] = temp;
        }
    }
    public void run() {
        int sizeMatrix = matrix.length;
        for (; firstRow < lastRow; firstRow++) {
            int flagPointer = firstRow;
            double number = matrix[flagPointer][firstRow];

            for (int x = firstRow + 1; x < lastRow; x++) {
                if (Math.abs(matrix[x][firstRow]) > number) {
                    number = matrix[x][firstRow];
                    flagPointer = x;
                }
            }

            if (firstRow != flagPointer) {
                transportRows(matrix, firstRow, flagPointer);
            }
            countDownLatch.countDown();

            for (int x = firstRow + 1; x < lastRow; x++) {
                double f = matrix[x][firstRow] / matrix[firstRow][firstRow];
                for (int y = firstRow + 1; y <= sizeMatrix; y++)
                    matrix[x][y] -= matrix[firstRow][y] * f;
                matrix[x][firstRow] = 0;
            }
        }
    }
}




