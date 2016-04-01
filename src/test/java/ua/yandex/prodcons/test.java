package ua.yandex.prodcons;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * @author Mykola Holovatsky
 */
public class test {
    public static void main(String[] args) {
        int count = 0;
        int count1 = 0;
        int[] values = new int[454];
        try {
            StreamTokenizer parser = new StreamTokenizer(new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File("test.txt")))));


            try {
                while (true) {
                    parser.nextToken();
                    /*count1 += parser.nval;*/
                    String val = parser.sval;
                    /*if (val == null) {
                        values[count1] = (int)parser.nval;
                        count1++;
                    }*/
                    if (val != null && parser.sval.equals("Producer")) {
                        count++;
                    }
                    if (val != null && parser.sval.equals("Consumer")) {
                        count1++;
                    }
                    if (val != null && parser.sval.equals("EOF")) {
                        System.out.println(count + " " + count1);
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println(count);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

       /* boolean flag;
        count1 = 1;
        for (int i = 0; i < 454; i++) {
            flag = false;
            System.out.print(i + " ");
            for (int j = 0; j < 454; j++) {
                if (i != j && values[j] == values[i]) {
                    System.out.print(j + " ");
                    count1++;
                }
            }
            System.out.println();
        }*/
        System.out.println(count + " " + count1);
    }
}
