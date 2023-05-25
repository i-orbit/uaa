package com.inmaytide.orbit.uaa;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author inmaytide
 * @since 2023/5/24
 */
public class SpringInternLockTester {

    public static void main(String... args) {
        Object value = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96).generateKey();
        final AtomicInteger counter = new AtomicInteger();
        new Thread(new TestRunnable(value, 5000, counter)).start();
        new Thread(new TestRunnable(value, 1000, counter)).start();
        new Thread(new TestRunnable(value, 1000, counter)).start();
        new Thread(new TestRunnable(value, 1000, counter)).start();
        new Thread(new TestRunnable(value, 900, counter)).start();
        new Thread(new TestRunnable(value, 1200, counter)).start();
    }


    public static class TestRunnable implements Runnable {

        private final String value;

        private final long sleep;

        private final AtomicInteger counter;

        public TestRunnable(Object value, long sleep, AtomicInteger counter) {
            this.value = String.valueOf(value);
            this.sleep = sleep;
            this.counter = counter;
        }

        @Override
        public void run() {
            synchronized (value.intern()) {
                long start = Instant.now().toEpochMilli();
                try {
                    Thread.sleep(sleep);
                } catch (Exception ignored) {

                }
                long end = Instant.now().toEpochMilli();
                System.out.println(counter.incrementAndGet() + "---" + sleep);
                System.out.printf("%s - %s = %d \n", Instant.ofEpochMilli(end), Instant.ofEpochMilli(start), end - start);
            }
        }
    }

}
