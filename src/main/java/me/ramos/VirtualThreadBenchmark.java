package me.ramos;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadBenchmark {

    private static final int TASK_COUNT = 10_000;
    private static final Duration TASK_DURATION = Duration.ofSeconds(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("=== Virtual Thread Benchmark ===");
        runBenchmark(true);

        System.out.println("=== Platform Thread Benchmark ===");
        runBenchmark(false);
    }

    private static void runBenchmark(boolean useVirtualThread)
            throws InterruptedException, ExecutionException {
        try (ExecutorService executor = useVirtualThread ?
                Executors.newVirtualThreadPerTaskExecutor() :
                Executors.newFixedThreadPool(200)) // OOM를 방지하기 위해 200개로 제한
        {

            Instant start = Instant.now();

            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        Thread.sleep(TASK_DURATION.toMillis());
                         System.out.println("작업 완료: " + Thread.currentThread());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get(); // 완료 대기
            }

            Instant end = Instant.now();
            System.out.println("총 소요 시간: " + Duration.between(start, end).toMillis() + "ms");

            executor.shutdown();
        }
    }
}
