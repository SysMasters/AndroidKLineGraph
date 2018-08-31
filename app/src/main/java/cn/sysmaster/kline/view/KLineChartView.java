package cn.sysmaster.kline.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.entity.DataParse;

/**
 * @author wanglibo
 * @date 2018/8/31
 * @describe
 */
public class KLineChartView extends FrameLayout {

    private CombinedChart mMainChart, mSubChart;
    private MainGraphTextView mMainText;
    private SubGraphTextView mSubText;

    /**
     * 柱状图颜色
     */
    private List<Integer> mVolColos, mMacdColors;
    /**
     * 涨跌颜色
     */
    private int mRiseColor, mDropColor;

    /**
     * 移动平均线颜色
     */
    private int mMa5Color, mMa10Color, mMa20Color;

    private DataParse mData;

    public KLineChartView(@NonNull Context context) {
        this(context, null);
    }

    public KLineChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        // 颜色数据
        mRiseColor = ContextCompat.getColor(getContext(), R.color.trend_red);
        mDropColor = ContextCompat.getColor(getContext(), R.color.trend_green);
        mMa5Color = ContextCompat.getColor(getContext(), R.color.trend_ma5);
        mMa10Color = ContextCompat.getColor(getContext(), R.color.trend_ma10);
        mMa20Color = ContextCompat.getColor(getContext(), R.color.trend_ma20);
        initView();
    }

    /**
     * 设置交易量BarChart颜色
     * <p>
     * 这里为了不修改源码，根据集合数据判断涨跌幅颜色
     */
    private void initVolBarColor() {
        // 第一次设置为跌的颜色
        mVolColos = new ArrayList<>();
        mVolColos.add(mDropColor);
        List<BarEntry> barEntries = mData.getMacdData();
        int size = barEntries.size();
        for (int i = 0; i < size; i++) {
            float value = barEntries.get(i).getY();
            if (i < size - 1) {
                if (value < barEntries.get(i + 1).getY()) {
                    mVolColos.add(mRiseColor);
                } else {
                    mVolColos.add(mDropColor);
                }
            }
        }
    }

    /**
     * 设置MACD BarChart颜色
     */
    private void initMacdBarColor() {
        mMacdColors = new ArrayList<>();
        List<BarEntry> barEntries = mData.getMacdData();
        for (BarEntry entry : barEntries) {
            if (entry.getY() <= 0) {
                mMacdColors.add(mDropColor);
            } else {
                mMacdColors.add(mRiseColor);
            }
        }
    }

    /**
     * 初始化布局、view
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.chart_kline_view, this);
        mMainChart = findViewById(R.id.main_chart);
        mSubChart = findViewById(R.id.sub_chart);
        mMainText = findViewById(R.id.tv_main_text);
        mSubText = findViewById(R.id.tv_sub_text);
        mMainText.setOnSwitchClickListener(new SwitchTextView.OnSwitchClickListener() {
            @Override
            public void onSwitch() {
                loadMainChartData();
            }
        });
        mSubText.setOnSwitchClickListener(new SwitchTextView.OnSwitchClickListener() {
            @Override
            public void onSwitch() {
                loadSubChartData();
            }
        });

        initChartStyle(mMainChart);
        initChartStyle(mSubChart);
    }

    /**
     * 设置k线图数据
     *
     * @param dataParse 数据实体
     */
    public void setDataParse(DataParse dataParse) {
        this.mData = dataParse;
        initVolBarColor();
        initMacdBarColor();
        loadMainChartData();
        loadSubChartData();
    }

    /**
     * 初始化chart图表样式
     *
     * @param chart 图表
     */
    private void initChartStyle(CombinedChart chart) {
        int borderColor = ContextCompat.getColor(getContext(), R.color.chart_border);
        int gridColor = ContextCompat.getColor(getContext(), R.color.chart_grid);
        // 禁用图例
        chart.getLegend().setEnabled(false);
        // 边距为0
        chart.setMinOffset(0);
        // 禁用描述文本
        chart.getDescription().setEnabled(false);
        // 开启绘制边框
        chart.setDrawBorders(true);
        chart.setBorderColor(borderColor);
        chart.setBorderWidth(1);
        // 关闭双击缩放事件
        chart.setDoubleTapToZoomEnabled(false);
        // 缩放x轴时自动调整y轴
        chart.setAutoScaleMinMaxEnabled(true);
        // 拖拽滚动时，手放开是否会持续滚动
        chart.setDragDecelerationEnabled(false);
        // 禁用Y轴缩放
        chart.setScaleYEnabled(false);
        chart.setMaxVisibleValueCount(70);
        // 左侧y轴
        YAxis axisLeft = chart.getAxisLeft();
        // 不显示文本
        axisLeft.setDrawLabels(false);
        axisLeft.setGridColor(gridColor);
        // 右侧y轴
        YAxis axisRight = chart.getAxisRight();
        axisRight.setDrawLabels(false);
        axisRight.setGridColor(gridColor);

        // x轴,网格线
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(true);
        // 设置位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawLabels(false);
        // 设置网格线颜色
        xAxis.setGridColor(gridColor);
    }

    /**
     * 加载图表数据
     */
    private void loadChartData(CombinedChart chart, CombinedData data) {
        // 解决首尾只显示一半的问题
        // github  issues/2553,2641
        chart.getXAxis().setAxisMinimum(data.getXMin() - 0.5f);
        chart.getXAxis().setAxisMaximum(data.getXMax() + 0.5f);
        chart.setData(data);

        chart.notifyDataSetChanged();
        chart.invalidate();
        chart.setVisibleXRange(70, 0);
        chart.moveViewToX(mData.getCandleEntries().size() - 1);
    }

    private void loadMainChartData() {
        loadChartData(mMainChart, getMainChartData());
    }

    private void loadSubChartData() {
        loadChartData(mSubChart, getSubChartData());
    }

    /**
     * 主图数据
     *
     * @return CombinedData
     */
    private CombinedData getMainChartData() {
        int type = mMainText.getType();
        CombinedData data = new CombinedData();
        if (type == DataParse.K) {
            // 添加k线(蜡烛图)数据
            data.setData(generateCandleData());
        }
        if (type == DataParse.MA) {
            data.setData(generateCandleData());
            data.setData(generateMaData());
        }
        if (type == DataParse.BOLL) {
            data.setData(generateCandleData());
            data.setData(generateBollData());
        }
        return data;
    }

    /**
     * 副图数据
     *
     * @return CombinedData
     */
    private CombinedData getSubChartData() {
        int type = mSubText.getType();
        CombinedData data = new CombinedData();
        if (type == DataParse.VOL) {
            data = generateVolData(data);
        } else if (type == DataParse.MACD) {
            data = generateMACDData(data);
        } else if (type == DataParse.KDJ) {
            data.setData(generateKDJData());
        } else if (type == DataParse.RSI) {
            data.setData(generateRSIData());
        }
        return data;
    }


    /**
     * 平均线MA 数据 (曲线)
     *
     * @return
     */
    private LineData generateMaData() {
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(setMaLine(5, mData.getMa5Data()));
        lineDataSets.add(setMaLine(10, mData.getMa10Data()));
        lineDataSets.add(setMaLine(20, mData.getMa20Data()));

        return new LineData(lineDataSets);
    }

    /**
     * K线数据 (蜡烛图)
     *
     * @return CandleData
     */
    private CandleData generateCandleData() {
        CandleData candleData = new CandleData();

        CandleDataSet set = new CandleDataSet(mData.getCandleEntries(), "Candle DataSet");
        set.setHighlightEnabled(true);
        // 以左侧数据为准
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 影线颜色与实体一致
        set.setShadowColorSameAsCandle(true);
        // 红涨,实体
        set.setDecreasingColor(mRiseColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        // 绿跌，实体
        set.setIncreasingColor(mDropColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        // 不涨不跌，
        set.setNeutralColor(mDropColor);
        // 不显示数值
        set.setDrawValues(true);
        candleData.addDataSet(set);

        return candleData;
    }

    /**
     * Boll线数据
     *
     * @return Boll
     */
    private LineData generateBollData() {
        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(setBollLine(0, mData.getBollDataUP()));
        lineDataSets.add(setBollLine(1, mData.getBollDataMB()));
        lineDataSets.add(setBollLine(2, mData.getBollDataDN()));

        return new LineData(lineDataSets);
    }


    /**
     * 设置MACD、dif、dea曲线
     *
     * @param data chart data
     * @return chart data
     */
    private CombinedData generateMACDData(CombinedData data) {
        BarDataSet set = new BarDataSet(mData.getMacdData(), "BarDataSet");
        // 高亮线开启
        set.setHighlightEnabled(true);
        // 是否绘制数值
        set.setDrawValues(false);
        // 使用左轴数据
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 设置颜色
        BarData barData = new BarData(set);
        set.setColors(mMacdColors);
        barData.setBarWidth(0.6f);
        // 设置dif、dea曲线
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setMACDMaLine(0, mData.getDifData()));
        sets.add(setMACDMaLine(1, mData.getDeaData()));
        LineData lineData = new LineData(sets);

        data.setData(barData);
        data.setData(lineData);
        return data;
    }

    /**
     * 生成KDJ曲线
     *
     * @return LineData
     */
    private LineData generateKDJData() {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setBollLine(0, mData.getK()));
        sets.add(setBollLine(1, mData.getD()));
        sets.add(setBollLine(2, mData.getJ()));
        return new LineData(sets);
    }

    /**
     * 生成RSI曲线
     *
     * @return LineData
     */
    private LineData generateRSIData() {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setBollLine(0, mData.getRsiData6()));
        sets.add(setBollLine(1, mData.getRsiData12()));
        sets.add(setBollLine(2, mData.getRsiData24()));
        return new LineData(sets);
    }

    /**
     * 生成 成交量柱状图和均线
     *
     * @return
     */
    private CombinedData generateVolData(CombinedData data) {
        BarDataSet set = new BarDataSet(mData.getBarEntries(), "BarDataSet");
        // 高亮线开启
        set.setHighlightEnabled(true);
        // 是否绘制数值
        set.setDrawValues(false);
        // 使用左轴数据
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 设置颜色
        BarData barData = new BarData(set);
        set.setColors(mVolColos);
        barData.setBarWidth(0.6f);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setMaLine(5, mData.getMa5VolData()));
        sets.add(setMaLine(10, mData.getMa10VolData()));
        sets.add(setMaLine(20, mData.getMa20VolData()));
        LineData lineData = new LineData(sets);

        data.setData(barData);
        data.setData(lineData);

        return data;
    }

    /**
     * 构建  折线图DataSet
     *
     * @param ma          5日、10日...MA
     * @param lineEntries data
     * @return LineDataSet
     */
    private LineDataSet setMaLine(int ma, List<Entry> lineEntries) {
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        if (ma == 5) {
            dataSet.setHighlightEnabled(false);
            dataSet.setDrawHorizontalHighlightIndicator(false);
        } else {/*此处必须得写*/
            dataSet.setHighlightEnabled(false);
        }
        dataSet.setDrawValues(false);
        if (ma == 5) {
            dataSet.setColor(mMa5Color);
        } else if (ma == 10) {
            dataSet.setColor(mMa10Color);
        } else if (ma == 20) {
            dataSet.setColor(mMa20Color);
        }
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet.setHighlightEnabled(false);
        return dataSet;
    }

    /**
     * 构建 布林线DataSet
     *
     * @param type        用于区分颜色
     * @param lineEntries data
     * @return LineDataSet
     */
    private LineDataSet setBollLine(int type, List<Entry> lineEntries) {
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawValues(false);

        if (type == 0) {
            dataSet.setColor(mMa5Color);
        } else if (type == 1) {
            dataSet.setColor(mMa10Color);
        } else if (type == 2) {
            dataSet.setColor(mMa20Color);
        }

        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return dataSet;
    }

    /**
     * 构建 MACD  dif、dea曲线
     *
     * @param type        区分颜色
     * @param lineEntries 曲线数据
     * @return LineDataSet
     */
    private LineDataSet setMACDMaLine(int type, List<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "");
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawValues(false);

        if (type == 0) {
            lineDataSetMa.setColor(mMa5Color);
        } else {
            lineDataSetMa.setColor(mMa10Color);
        }

        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

        return lineDataSetMa;
    }


}
