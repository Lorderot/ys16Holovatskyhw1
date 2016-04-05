package ua.yandex.WordsCount;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Mykola Holovatsky
 */
public class TestWordsCounter {
    private Map<String, Integer> checker = new HashMap<>();
    @Test
    public void testWordsCounter_SingleWord() {
        int length = 1000000;
        String[] setOfWords = new String[length];
        String word = "wordsCounter";
        for (int i = 0; i < length; i++) {
            setOfWords[i] = word;
        }
        Map<String, Integer> wordsMap = WordsCounter.wordsCount(setOfWords);
        assertEquals(length, (int) wordsMap.get(word));
    }

    @Test
    public void testWordsCounter_ManyWords() {
        int length = 10000000;
        clear();
        String[] setOfWords = generateTheWords(length);
        Map<String, Integer> wordsMap = WordsCounter.wordsCount(setOfWords);
        for (Map.Entry<String, Integer> term : checker.entrySet()) {
            assertEquals(term.getValue(), wordsMap.get(term.getKey()));
        }
    }

    private String[] generateTheWords(int length) {
        String[] setOfWords = new String[length];
        Random generator = new Random(System.nanoTime());
        char[] alphabet = new char[]{'a', 'b'};
        for (int i = 0; i < length; i++) {
            int wordLength = generator.nextInt(4) + 1;
            char[] word = new char[wordLength];
            for (int j = 0; j < wordLength; j++) {
                word[j] = alphabet[generator.nextInt(2)];
            }
            String readyWord = new String(word);
            setOfWords[i] = readyWord;
            Integer amount = checker.get(readyWord);
            if (amount == null) {
                checker.put(readyWord, 1);
            } else {
                checker.put(readyWord, amount + 1);
            }
        }
        return setOfWords;
    }

    private void clear() {
        checker = new HashMap<>();
    }
}
