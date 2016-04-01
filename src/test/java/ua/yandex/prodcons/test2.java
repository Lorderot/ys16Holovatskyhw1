package ua.yandex.prodcons;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Mykola Holovatsky
 */
public class test2 {
    public static Long expected = new Long(20);
    public static Long element = new Long(30);
    public static void main(String[] args) {
        expected = apply(expected, element, (x,y) -> (x + y));
        System.out.println(expected + " " + element);
    }
    public static Long apply(Long exp, Long el, BiFunction<Long, Long, Long> func) {
        return func.apply(exp, el);
    }
}
