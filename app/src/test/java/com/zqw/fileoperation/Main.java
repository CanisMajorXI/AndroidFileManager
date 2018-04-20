package com.zqw.fileoperation;

import java.util.Scanner;

/**
 * Created by 51376 on 2018/4/20.
 */

public class Main {
    public static void main(String[] args) {
        String regex = "(\\S|\\s)*\\S+(\\S|\\s)*.zip";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.next();
            System.out.println(str.matches(regex));
        }
    }
}
