package cn.sysmaster.kline.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loro on 2017/3/7.
 */

public class RSIEntity {

    private ArrayList<Float> RSIs;

    /**
     * @param kLineBeens
     * @param n          几日
     */
    public RSIEntity(List<KLineBean> kLineBeens, float n) {
        this(kLineBeens, n, 100);
    }

    /**
     * @param list
     * @param days   几日
     * @param defult 不足N日时的默认值
     */
    public RSIEntity(List<KLineBean> list, float days, float defult) {
        RSIs = new ArrayList();

        if (list == null) {
            return;
        }
        if (days > list.size()) {
            return;
        }
        //默认0
        float smaMax = 0, smaAbs = 0;
        float lc = 0;
        float close = 0;
        float rsi = 0;
        for (int i = 1; i < list.size(); i++) {
            KLineBean entity = list.get(i);
            lc = list.get(i - 1).c;
            close = entity.c;
            smaMax = countSMA((float) Math.max(close - lc, 0d), days, 1, smaMax);
            smaAbs = countSMA(Math.abs(close - lc), days, 1, smaAbs);
            rsi = smaMax / smaAbs * 100;
            if (days >= i) {
                RSIs.add(Float.NaN);
            } else {
                if (rsi == 0) {
                    rsi = Float.NaN;
                }
                RSIs.add(rsi);
            }
        }
        int size = list.size() - RSIs.size();
        for (int i = 0; i < size; i++) {
            RSIs.add(0, Float.NaN);
        }
    }

    /**
     * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     * C=今天收盘价－昨天收盘价    N＝就是周期比如 6或者12或者24， M＝权重，其实就是1
     *
     * @param c   今天收盘价－昨天收盘价
     * @param n   周期
     * @param m   1
     * @param sma 上一个周期的sma
     * @return
     */
    public static float countSMA(float c, float n, float m, float sma) {
        return (m * c + (n - m) * sma) / n;
    }

    public ArrayList<Float> getRSIs() {
        return RSIs;
    }
}
