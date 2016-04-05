package ua.yandex.WordsCount;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * @author Mykola Holovatsky
 */
public class WordsCounter {
    private static Map<String, Integer> mapOfWords;
    private static final int THRESHOLD = 10;
    private static ForkJoinPool pool = new ForkJoinPool();

    private WordsCounter() {
    }

    public static Map<String, Integer> wordsCount(String[] setOfWords) {
        mapOfWords = Collections.synchronizedMap(new HashMap<>());
        splitTheJob(setOfWords, 0, setOfWords.length);
        while (pool.getActiveThreadCount() != 0) {
            try {
                pool.awaitTermination(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mapOfWords;
    }

    private static void splitTheJob(String[] setOfWords,
                                    int leftEdge, int rightEdge) {
        if (rightEdge - leftEdge < THRESHOLD) {
            countWords(setOfWords, leftEdge, rightEdge);
        } else {
            int middle = (rightEdge + leftEdge) / 2;
            pool.submit(() -> {
                splitTheJob(setOfWords, leftEdge, middle);
            });
            splitTheJob(setOfWords, middle, rightEdge);
        }
    }

    private static void countWords(String[] setOfWords,
                                   int leftEdge, int rightEdge) {
        for (int i = leftEdge; i < rightEdge; i++) {
            String word = setOfWords[i];
            Object result = null;
            while (result == null) {
                result = mapOfWords.computeIfPresent(word,
                        (term, amount) -> amount + 1);
                if (result == null) {
                    result = putNewWords(word);
                }
            }
        }
    }

    private static Object putNewWords(String word) {
        Object result;
        synchronized (WordsCounter.class) {
            result = mapOfWords.computeIfPresent(word,
                    (term, amount) -> amount + 1);
            if (result == null) {
                result = mapOfWords.computeIfAbsent(word, term -> 1);
            }
        }
        return result;
    }
}
