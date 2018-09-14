package cn.sysmaster.kline.util;

import java.util.Calendar;

/**
 * @author wanglibo
 * @date 2018/9/3
 * @describe
 */
public class DateUtils {

    public static String timeStamp2Date(long timeStamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "-" + (month >= 10 ? month : "0" + month) + "-" + (day >= 10 ? day : "0" + day);
    }
}
