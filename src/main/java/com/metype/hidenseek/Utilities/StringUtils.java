package com.metype.hidenseek.Utilities;

public class StringUtils {
    public static String PrettyifySeconds(int secondsNum) {
        String val = "";
        if(secondsNum >= 60) {
            int minuteVal = ((int)Math.floor(secondsNum / 60.0));
            val += minuteVal + " minute" + ((minuteVal > 1)?"s":"");
            if(secondsNum % 60 > 0) {
                val += " and " + (secondsNum%60) + " seconds";
            }
        } else {
            val += (secondsNum%60) + " seconds";
        }
        return val;
    }
}
