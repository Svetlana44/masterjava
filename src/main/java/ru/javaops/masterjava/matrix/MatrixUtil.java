package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    static class SubResult implements Runnable {

        final int[][] matrixA;
        final int[][] matrixB;
        final int[][] matrixC;
        int from;
        int to;
        CountDownLatch latch;
        final int matrixSize;

        public SubResult(int[][] matrixA, int[][] matrixB, int[][] matrixC, int from, int to,
            CountDownLatch latch) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.matrixC = matrixC;
            this.from = from;
            this.to = to;
            this.latch = latch;
            matrixSize = matrixA.length;
        }

        @Override
        public void run() {
            for (int i = from; i < to; i++) {

                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[i][k] * matrixB[k][j];
                    }
                    matrixC[i][j] = sum;
                }
            }
            latch.countDown();
        }
    }

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {

        final CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);

        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executor.execute(new SubResult(matrixA, matrixB, matrixC, i * 100, (i + 1) * 100, latch));
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            // handle
        }

        return matrixC;
    }

    public Boolean MultSubResult(int[][] matrixC, int[][] matrixA, int[][] matrixB, int i, int from, int to) {
        Boolean result = false;
        final int matrixSize = matrixA.length;
        for (int j = from; j < to; j++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += matrixA[i][k] * matrixB[k][j];
            }
            matrixC[i][j] = sum;
        }
        result = true;
        return result;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
