package org.project.bookingapi.util;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Supplier;

@Component
public class ReactiveJpaExecutor {

    public <T> Mono<T> monoBlocking(Supplier<T> supplier) {
        var mdc = MDC.getCopyOfContextMap();

        return Mono.fromSupplier(() -> {
                    if (ObjectUtils.isNotEmpty(mdc)) MDC.setContextMap(mdc);
                    try {
                        return supplier.get();
                    } finally {
                        MDC.clear();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public <T> Flux<T> fluxBlocking(Supplier<java.util.List<T>> supplier) {
        var mdc = MDC.getCopyOfContextMap();

        return Mono.fromSupplier(() -> {
                    if (ObjectUtils.isNotEmpty(mdc)) MDC.setContextMap(mdc);
                    try {
                        return supplier.get();
                    } finally {
                        MDC.clear();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }
}