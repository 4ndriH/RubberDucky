package services.Miscellaneous;

import java.text.DecimalFormat;

public class Format {
    public static String Number(int n) {
        return new DecimalFormat("###,###,###").format(n).replaceAll("[,,.]", "'");
    }

    public static String Time(int linesCnt) {
        int seconds = linesCnt % 60;
        int minutes = (linesCnt - seconds) / 60 % 60;
        int hours = ((linesCnt - seconds) / 60 - minutes) / 60;

        String days = "";
        if (hours > 23) {
            days = (hours - (hours %= 24)) / 24 + "";
            if (Integer.parseInt(days) == 1) {
                days += " day, ";
            } else {
                days += " days, ";
            }
        }
        return String.format(days + "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
