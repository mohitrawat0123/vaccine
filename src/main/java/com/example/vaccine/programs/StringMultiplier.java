package com.example.vaccine.programs;

import java.util.Arrays;

public class Demo {

    public static void main(String...args) {

        String s1 = "123456789";
        String s2 = "123456789";

        long l1 = Long.parseLong(s1);
        long l2 = Long.parseLong(s2);

        System.out.println("Result: " + multiplyStrings(s1, s2));

        System.out.println("Actual: " + l1 * l2);


    }


    private static String multiplyStrings(String multiplicand, String multiplier) {

        int c;
        int place = 0;
        String prev = "";

        for(int i=multiplier.length() - 1; i>=0; i--) {

            char[] c1 = new char[place];
            Arrays.fill(c1, '0');

            StringBuilder sb = new StringBuilder(new String(c1));

            int n1 = Integer.parseInt(String.valueOf(multiplier.charAt(i)));
            c = 0;

            for(int j = multiplicand.length() - 1 ; j>= 0 ;j--) {
                int n2 = Integer.parseInt(String.valueOf(multiplicand.charAt(j)));
                int n3 = (n2 * n1) + c;
                sb.insert(0, (n3 % 10));
                c = n3 / 10;
            }

            if(c != 0){
                sb.insert(0, c);
            }

            place ++;
            prev = addStrings(new StringBuilder(prev), sb);
        }

        return prev;
    }

    private static String addStrings(StringBuilder s1, StringBuilder s2) {

        StringBuilder sb = new StringBuilder();

        s1.reverse();
        s2.reverse();

        int i = 0;
        int j = 0;
        int c = 0;

        while(i < s1.length() && j < s2.length()){
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(j);
            int n1 = Integer.parseInt(String.valueOf(c1));
            int n2 = Integer.parseInt(String.valueOf(c2));
            int n3 = n1 + n2 + c;
            sb.append(n3 % 10);
            c = n3 / 10;
            i++;
            j++;
        }

        while(i < s1.length()) {
            char c1 = s1.charAt(i);
            int n1 = Integer.parseInt(String.valueOf(c1));
            int n2 = n1 + c;
            sb.append(n2 % 10);
            c = n2 / 10;
            i++;
        }

        while(j < s2.length()) {
            char c1 = s2.charAt(j);
            int n1 = Integer.parseInt(String.valueOf(c1));
            int n2 = n1 + c;
            sb.append(n2 % 10);
            c = n2 / 10;
            j++;
        }

        if(c != 0) {
            sb.append(c);
        }

        return new StringBuilder(sb.toString()).reverse().toString();

    }


}
