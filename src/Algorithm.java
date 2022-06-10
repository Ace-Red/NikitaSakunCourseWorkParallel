import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Algorithm {

    public static void main(String[] args) {
        int sizeMatrix = 100;
        int countThreads = 14;
        Random random = new Random();
        double [][] matrixSystem = new double[sizeMatrix][sizeMatrix+1];
        // Генерація матриці
        for (int i = 0; i < sizeMatrix; i++) {
            for (int j = 0; j < sizeMatrix+1; j++) {
                matrixSystem[i][j] = random.nextDouble();
            }
        }
        showMatrix(matrixSystem);
        try {
            if (countThreads < 1) {
                System.out.println("Кількість процесорів має бути більше 1");
                System.exit(1);
            }
            if (matrixSystem.length <= 1) {
                System.err.println("Розмірність матриці повина бути більшою за [1x1]");
                System.exit(1);
            }
            final double startTime = System.nanoTime();
            double[] result = AlgoGaus(matrixSystem, countThreads);
            final double cleanTime = (System.nanoTime() - startTime) / 1000000;
            System.out.println("\n" + cleanTime + "ms");
            Arrays.stream(result).forEach(value -> System.out.printf("%.2f%n", value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double[] AlgoGaus(double[][] matrixSystem, int countOfThreadsUser) {

        int threadsJava = Runtime.getRuntime().availableProcessors()+64;
        System.out.println("Total processors: " + threadsJava);
        if (countOfThreadsUser < threadsJava) {
            threadsJava = countOfThreadsUser;
        }
        if (threadsJava > matrixSystem.length) {
            threadsJava = matrixSystem.length / 2;
        }
        System.out.println("Threads spawned for processing: " + threadsJava);
        int countRowForThead = matrixSystem.length / threadsJava;
        int rowForLastThread = matrixSystem.length % threadsJava;
        Worker[] workers = new Worker[threadsJava];
        CountDownLatch countDownLatch = new CountDownLatch(threadsJava);
        for (int p = 0; p < threadsJava; p++) {
            int lower = p * countRowForThead;
            int higher = (p + 1) * countRowForThead;
            if (p == (threadsJava - 1))
                workers[p] = new Worker(matrixSystem, lower, higher + rowForLastThread, countDownLatch);
            else
                workers[p] = new Worker(matrixSystem, lower, higher, countDownLatch);
            workers[p].start();
        }
        try {
            for (Thread t : workers) {
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Проблема з приєднанням до потоку! ");
            e.printStackTrace();
        }


        return partOfAlgoGausBack(matrixSystem);
    }

    public static void showMatrix(double[][] array) {
        for (double[] num : array) {
            for (int j = 0; j < array[0].length; j++) {
                if (j == array[0].length - 1)
                    System.out.println(" | " + num[j]);
                else
                    System.out.print(num[j] + " ");
            }
        }
    }

    private static double[] partOfAlgoGausBack(double[][] matrix) {

        int n = matrix.length;
        double[] curArr = new double[n];
        for (int x = n - 1; x >= 0; x--) {
            curArr[x] = matrix[x][n];
            for (int y = x + 1; y < n; y++) {
                curArr[x] = curArr[x] - matrix[x][y] * curArr[y];
            }
            curArr[x] = curArr[x] / matrix[x][x];
        }
        return curArr;
    }

}
