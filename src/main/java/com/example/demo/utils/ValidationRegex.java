package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
//    public static boolean isRegexEmail(String target) {
//        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(target);
//        return matcher.find();
//
//    }
public static Boolean isRegexPhoneNumber(String number) {
    return Pattern.matches("^\\d{3}-\\d{3,4}-\\d{4}$", number);
}


}

