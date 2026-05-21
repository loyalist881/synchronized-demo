package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] routs = new String[1000];
        List<Future<?>> futures = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < routs.length; i++) {
            routs[i] = generateRoute("RLRFR", 100);
        }

        long startTs = System.currentTimeMillis(); // start time
        for (String rout : routs) {
            Runnable runnable = () -> {
                int R = 0;
                for (int i = 0; i < rout.length(); i++) {
                    char c = rout.charAt(i);
                    if (c == 'R') {
                        R++;
                    }
                }

                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(R)) {
                        sizeToFreq.put(R, sizeToFreq.get(R) + 1);
                    } else {
                        sizeToFreq.put(R, 1);
                    }
                }
            };
            Future<?> task = threadPool.submit(runnable);
            futures.add(task);
        }

        for (Future<?> future : futures) {
            future.get();
        }

        long endTs = System.currentTimeMillis(); // end time
        threadPool.shutdown();

        System.out.println("Time: " + (endTs - startTs) + "ms");

        int maxFreq = 0;
        int mostFreqSize = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                mostFreqSize = entry.getKey();
            }
        }

        System.out.println("Самое частое количество повторений " + mostFreqSize + " (встретилось " + maxFreq + " раз)");

        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != mostFreqSize) {
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}

