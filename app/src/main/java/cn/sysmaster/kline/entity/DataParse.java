package cn.sysmaster.kline.entity;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataParse {

    public static final int K = 10;
    public static final int MA = 11;
    public static final int BOLL = 12;

    public static final int MACD = 20;
    public static final int KDJ = 21;
    public static final int RSI = 22;
    public static final int VOL = 23;
    /**
     * 接口原数据
     */
    private List<KLineBean> mDatas;
    /**
     * 成交量数据
     */
    private ArrayList<BarEntry> barEntries;
    /**
     * k线数据
     */
    private List<CandleEntry> mCandleEntries;
    /**
     * MA数据
     */
    private List<Entry> ma5Data, ma10Data, ma20Data;
    /**
     * BOLL数据
     */
    private List<Entry> bollDataUP, bollDataMB, bollDataDN;

    /**
     * MACD数据
     */
    private List<BarEntry> macdDatas;
    private List<Entry> difData, deaData;

    /**
     * KDJ数据
     */
    private List<Entry> kData, dData, jData;
    /**
     * RSI数据
     */
    private List<Entry> rsiData6, rsiData12, rsiData24;
    /**
     * 成交量均线
     */
    private List<Entry> ma5VolData, ma10VolData, ma20VolData;


    public DataParse(List<KLineBean> data) {
        initArrays();
        initKlineDatas(data);
    }

    private void initArrays() {
        this.mCandleEntries = new ArrayList<>();
        this.barEntries = new ArrayList<>();
    }

    /**
     * 初始化k线数据
     *
     * @param data
     */
    private void initKlineDatas(List<KLineBean> data) {
        Collections.reverse(data);
        this.mDatas = data;
        CandleEntry entry;
        BarEntry barEntry;
        KLineBean chartData;
        for (int i = 0; i < data.size(); i++) {
            chartData = data.get(i);
            float shadowHigh = chartData.h;
            float shadowLow = chartData.l;
            float close = chartData.c;
            float open = chartData.o;
            entry = new CandleEntry(i, shadowHigh, shadowLow, open, close);
            barEntry = new BarEntry(i, chartData.a);
            mCandleEntries.add(entry);
            barEntries.add(barEntry);
        }
        initKLineMA(mDatas);
        initBOLL(mDatas);
        initMACD(mDatas);
        initKDJ(mDatas);
        initRSI(mDatas);
        initMaVol(mDatas);
    }

    /**
     * 初始化K线图均线MA
     *
     * @param datas
     */
    private void initKLineMA(List<KLineBean> datas) {
        if (null == datas) {
            return;
        }
        ma5Data = new ArrayList<>();
        ma10Data = new ArrayList<>();
        ma20Data = new ArrayList<>();

        KMAEntity kmaEntity5 = new KMAEntity(datas, 5);
        KMAEntity kmaEntity10 = new KMAEntity(datas, 10);
        KMAEntity kmaEntity20 = new KMAEntity(datas, 20);
        for (int i = 0; i < kmaEntity5.getMAs().size(); i++) {
            if (i >= 5) {
                ma5Data.add(new Entry(i, kmaEntity5.getMAs().get(i)));
            }
            if (i >= 10) {
                ma10Data.add(new Entry(i, kmaEntity10.getMAs().get(i)));
            }
            if (i >= 20) {
                ma20Data.add(new Entry(i, kmaEntity20.getMAs().get(i)));
            }
        }

    }

    /**
     * 初始化BOLL
     *
     * @param datas
     */
    private void initBOLL(List<KLineBean> datas) {
        BOLLEntity bollEntity = new BOLLEntity(datas, 20);

        bollDataUP = new ArrayList<>();
        bollDataMB = new ArrayList<>();
        bollDataDN = new ArrayList<>();
        for (int i = 0; i < bollEntity.getUPs().size(); i++) {
            bollDataUP.add(new Entry(i, bollEntity.getUPs().get(i)));
            bollDataMB.add(new Entry(i, bollEntity.getMBs().get(i)));
            bollDataDN.add(new Entry(i, bollEntity.getDNs().get(i)));
        }
    }

    /**
     * 初始化MACD
     *
     * @param datas k线原始数据
     */
    private void initMACD(List<KLineBean> datas) {
        MACDEntity macdEntity = new MACDEntity(datas);

        macdDatas = new ArrayList<>();
        deaData = new ArrayList<>();
        difData = new ArrayList<>();
        for (int i = 0; i < macdEntity.getMACD().size(); i++) {
            macdDatas.add(new BarEntry(i, macdEntity.getMACD().get(i)));
            deaData.add(new Entry(i, macdEntity.getDEA().get(i)));
            difData.add(new Entry(i, macdEntity.getDIF().get(i)));
        }
    }

    /**
     * 初始化KDJ
     *
     * @param datas
     */
    private void initKDJ(List<KLineBean> datas) {
        KDJEntity kdjEntity = new KDJEntity(datas, 9);

        kData = new ArrayList<>();
        dData = new ArrayList<>();
        jData = new ArrayList<>();
        for (int i = 0; i < kdjEntity.getD().size(); i++) {
            kData.add(new Entry(i, kdjEntity.getK().get(i)));
            dData.add(new Entry(i, kdjEntity.getD().get(i)));
            jData.add(new Entry(i, kdjEntity.getJ().get(i)));
        }
    }

    /**
     * 初始化RSI
     *
     * @param datas
     */
    private void initRSI(List<KLineBean> datas) {
        RSIEntity rsiEntity6 = new RSIEntity(datas, 6);
        RSIEntity rsiEntity12 = new RSIEntity(datas, 12);
        RSIEntity rsiEntity24 = new RSIEntity(datas, 24);

        rsiData6 = new ArrayList<>();
        rsiData12 = new ArrayList<>();
        rsiData24 = new ArrayList<>();
        for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
            rsiData6.add(new Entry(i, rsiEntity6.getRSIs().get(i)));
            rsiData12.add(new Entry(i, rsiEntity12.getRSIs().get(i)));
            rsiData24.add(new Entry(i, rsiEntity24.getRSIs().get(i)));
        }
    }

    /**
     * 初始化成交量均线
     *
     * @param datas k线原数据
     */
    private void initMaVol(List<KLineBean> datas) {
        if (null == datas) {
            return;
        }
        ma5VolData = new ArrayList<>();
        ma10VolData = new ArrayList<>();
        ma20VolData = new ArrayList<>();

        VMAEntity vmaEntity5 = new VMAEntity(datas, 5);
        VMAEntity vmaEntity10 = new VMAEntity(datas, 10);
        VMAEntity vmaEntity20 = new VMAEntity(datas, 20);
        for (int i = 0; i < vmaEntity5.getMAs().size(); i++) {
            ma5VolData.add(new Entry(i, vmaEntity5.getMAs().get(i)));
            ma10VolData.add(new Entry(i, vmaEntity10.getMAs().get(i)));
            ma20VolData.add(new Entry(i, vmaEntity20.getMAs().get(i)));
        }

    }

    public List<KLineBean> getDatas() {
        return mDatas;
    }

    public List<CandleEntry> getCandleEntries() {
        return mCandleEntries;
    }

    public List<Entry> getMa5Data() {
        return ma5Data;
    }

    public List<Entry> getMa10Data() {
        return ma10Data;
    }

    public List<Entry> getMa20Data() {
        return ma20Data;
    }

    public List<Entry> getBollDataUP() {
        return bollDataUP;
    }

    public List<Entry> getBollDataMB() {
        return bollDataMB;
    }

    public List<Entry> getBollDataDN() {
        return bollDataDN;
    }

    public List<BarEntry> getMacdData() {
        return macdDatas;
    }

    public List<Entry> getDifData() {
        return difData;
    }

    public List<Entry> getDeaData() {
        return deaData;
    }

    public List<Entry> getK() {
        return kData;
    }

    public List<Entry> getD() {
        return dData;
    }

    public List<Entry> getJ() {
        return jData;
    }

    public List<Entry> getRsiData6() {
        return rsiData6;
    }

    public List<Entry> getRsiData12() {
        return rsiData12;
    }

    public List<Entry> getRsiData24() {
        return rsiData24;
    }

    public List<Entry> getMa5VolData() {
        return ma5VolData;
    }

    public List<Entry> getMa10VolData() {
        return ma10VolData;
    }

    public List<Entry> getMa20VolData() {
        return ma20VolData;
    }

    public ArrayList<BarEntry> getBarEntries() {
        return barEntries;
    }
}
