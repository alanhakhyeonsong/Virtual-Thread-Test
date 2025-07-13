package me.ramos;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ReactiveMonoBenchmark {

    private static final int TASK_COUNT = 10_000;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Flux.range(0, TASK_COUNT)
                .flatMap(i -> Mono.delay(Duration.ofSeconds(1))
                .doOnNext(ignore -> System.out.println("작업 완료: " + Thread.currentThread()))
                .subscribeOn(Schedulers.parallel()), 1000)
                .blockLast(); // 모든 작업 완료 대기

        long end = System.currentTimeMillis();
        System.out.println("총 소요 시간 : " + (end - start) + "ms");
    }
}
