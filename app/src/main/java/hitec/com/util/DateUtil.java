package hitec.com.util;

import java.util.Calendar;

public class DateUtil {

    public static String getDateFromFullTime(String value) {
        String date = value.split(" ")[0];
        return date;
    }

    public static String  getTimeFromFullTime(String value) {
        String time = value.split(" ")[1];
        return time;
    }

    public static String getCurDateTime() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));

        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        if (month.length() != 2)
            month = "0" + month;

        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        if (day.length() != 2)
            day = "0" + day;

        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        if (hour.length() != 2)
            hour = "0" + hour;

        String mins = String.valueOf(c.get(Calendar.MINUTE));
        if (mins.length() != 2)
            mins = "0" + mins;

        String secs = String.valueOf(c.get(Calendar.SECOND));
        if (secs.length() != 2)
            secs = "0" + secs;

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins + ":" + secs);

        return sbBuffer.toString();
    }

}
