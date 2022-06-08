import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Algorithm {

    public static void main(String[] args) {
        int matrixSize = 7000;
        int threads = 14;
        Random r = new Random();
        double [][] matrixA = new double[matrixSize][matrixSize+1];
        // Генерація матриці
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize+1; j++) {
                matrixA[i][j] = r.nextDouble();
            }
        }
        //printMatrix(matrixA);
        try {
            if (threads < 1) {
                System.out.println("Кількість процесорів має бути більше 1");
                System.exit(1);
            }
            if (matrixA.length <= 1) {
                System.err.println("Розмірність матриці повина бути більшою за [1x1]");
                System.exit(1);
            }
            final double begin = System.nanoTime();
            double[] result = doParallelGaussianElimination(matrixA, threads);
            final double duration = (System.nanoTime() - begin) / 1000000;
            System.out.println("\n" + duration + "ms");
            // Rounding up the answers
            Arrays.stream(result)
                    .forEach(value -> System.out.printf("%.2f%n", value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double[] doParallelGaussianElimination(double[][] A, int numOfThreadsToUse) {
        //printMatrix(A);

        int availableProcessors = Runtime.getRuntime().availableProcessors()+64;
        System.out.println("Total processors: " + availableProcessors);
        if (numOfThreadsToUse < availableProcessors) {
            availableProcessors = numOfThreadsToUse;
        }
        if (availableProcessors > A.length) {
            availableProcessors = A.length / 2;
        }
        // availableProcessors = 1;
        System.out.println("Threads spawned for processing: " + availableProcessors);
        int div = A.length / availableProcessors;
        int remainder = A.length % availableProcessors;
        Worker[] threads = new Worker[availableProcessors];
        CountDownLatch latch = new CountDownLatch(availableProcessors);
        for (int p = 0; p < availableProcessors; p++) {
            int lower = p * div;
            int higher = (p + 1) * div;
            if (p == (availableProcessors - 1))
                threads[p] = new Worker(A, lower, higher + remainder, latch);
            else
                threads[p] = new Worker(A, lower, higher, latch);
            threads[p].start();
        }
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Issue with Thread join! ");
            e.printStackTrace();
        }

        //printMatrix(A);

        return backSubstitution(A);
    }

    public static void printMatrix(double[][] answer) {
        for (double[] doubles : answer) {
            for (int j = 0; j < answer[0].length; j++) {
                if (j == answer[0].length - 1)
                    System.out.println(" || " + doubles[j]);
                else
                    System.out.print(doubles[j] + " ");
            }
            //System.out.println();
        }
        //System.out.println();
    }

    private static double[] backSubstitution(double[][] A) {

        int n = A.length;
        double[] currentArray = new double[n];
        for (int x = n - 1; x >= 0; x--) {
            currentArray[x] = A[x][n];
            for (int y = x + 1; y < n; y++) {
                currentArray[x] = currentArray[x] - A[x][y] * currentArray[y];
            }
            currentArray[x] = currentArray[x] / A[x][x];
        }
        return currentArray;
    }

}
