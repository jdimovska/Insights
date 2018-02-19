package com.example.marinaangelovska.insights.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marinaangelovska on 2/19/18.
 */

public class NormalizeNumber {
    private static final Pattern EUROPEAN_DIALING_PLAN = Pattern.compile("^\\+|(00)|(0)");
    private static String countryCode = "389";

    public static String normalizeNumber(String number) {
        // Remove all weird characters such as /, -, ...
        number = number.replaceAll("[^+0-9]", "");
        number = number.replaceAll("\\s+|(|)","").replaceAll("-","");

        Matcher match = EUROPEAN_DIALING_PLAN.matcher(number);
        if (!match.find()) {
            return number;
        } else if (match.group(1) != null) {     // Starts with "00"
            return match.replaceFirst("+");
        } else if (match.group(2) != null) {     // Starts with "0"
            return match.replaceFirst("+" + countryCode);
        } else {                                 // Starts with "+"
            return number;
        }
    }
}
