package com.zqw.fileoperation.functions;

/**
 * Created by 51376 on 2018/3/16.
 */

import java.util.Random;

public class RandomNameGenerater {
    static String none_after_a = "yko";
    static char[] vowels = {'a', 'e', 'i', 'o', 'u'};
    static char[] cosonants = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};

     public  String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int digits = Math.abs(random.nextInt()) % 11 + 3;
        int vowelflag = 0, consonantflag = 0;
        char lastletter = '.';
        for (int i = 0; i < digits; i++) {
            int type = -1;
            boolean ok = false;
            char letter = ' ';
            while (!ok) {
                if (vowelflag != 2 &&(consonantflag == 2  || Math.abs(random.nextInt(100)) < 38)) {
                    letter = vowels[random.nextInt(5)];
                    type = 0;
                } else {
                    letter = cosonants[random.nextInt(21)];
                    type = 1;
                }
                if (lastletter == 'a') {
                    if(none_after_a.contains(String.valueOf(letter))){
                        break;
                    }
                }
                lastletter = letter;
                ok = true;
                if(type == 0) vowelflag++;
                if(type == 1) consonantflag++;
            }

            stringBuilder.append(letter);
        }
        return stringBuilder.toString();
    }

    private static int judge(char s) {
        for (char i : vowels) {
            if (s == i) {
                return 1;
            }
        }
        for (char i : cosonants) {
            if (s == i) {
                return 0;
            }
        }
        return -1;
    }

    boolean isVowelLetter(char s) {
        String vowels = "aeiou";
        String str = String.valueOf(s);
        if (str.contains(vowels)) return true;
        return false;
    }
}

