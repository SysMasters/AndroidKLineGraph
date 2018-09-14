package cn.sysmaster.kline.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author wanglibo
 * @date 2018/9/10
 * @describe
 */
public class NumberUtils {
    private static final Double MILLION = 10000.0;
    private static final Double BILLION = 100000000.0;
    private static final String MILLION_UNIT = "万";
    private static final String BILLION_UNIT = "亿";

    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     *
     * @param amount 报销金额
     * @return
     * @author
     * @version 1.00.00
     * @date 2018年1月18日
     */
    public static String amountConversion(double amount) {
        //最终返回的结果值
        String result;
        //转换后的值
        double value;

        //金额大于1百万小于1亿
        if (amount > MILLION && amount < BILLION) {
            value = amount / MILLION;
            result = formatNumber(value) + MILLION_UNIT;
        }
        //金额大于1亿
        else if (amount >= BILLION) {
            value = amount / BILLION;
            result = formatNumber(value) + BILLION_UNIT;
        } else {
            result = formatNumber(amount);
        }
        return result;
    }


    /**
     * 对数字进行四舍五入，保留2位小数,保留两位小数
     *
     * @param number 要四舍五入的数字
     * @return
     * @author
     * @version 1.00.00
     * @date 2018年1月18日
     */
    public static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(number);
    }

}
