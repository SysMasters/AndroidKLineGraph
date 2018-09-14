package cn.sysmaster.kline.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.OnDrawListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.entity.DataParse;
import cn.sysmaster.kline.entity.KLineBean;
import cn.sysmaster.kline.listener.ChartInfoViewHandler;
import cn.sysmaster.kline.listener.CoupleChartGestureListener;
import cn.sysmaster.kline.util.DateUtils;

/**
 * @author wanglibo
 * @date 2018/8/31
 * @describe
 */
public class KLineChartView extends FrameLayout implements OnChartValueSelectedListener {

    private CombinedChart mMainChart, mSubChart;
    private MainGraphTextView mMainText;
    private SubGraphTextView mSubText;
    private TextView mTvMainValue, mTvSubValue, mTvLoading;
    /**
     * 图表是否移动到最后
     */
    private boolean isMoveToLast = true;


    /**
     * 柱状图颜色
     */
    private List<Integer> mVolColos, mMacdColors;
    /**
     * 涨跌颜色
     */
    private int mRiseColor, mDropColor;

    /**
     * 平均线颜色
     */
    private int mMa5Color, mMa10Color, mMa20Color;

    private DataParse mData;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

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
        mRiseColor = ContextCompat.getColor(getContext(), R.color.trend_rise);
        mDropColor = ContextCompat.getColor(getContext(), R.color.trend_drop);
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
        mTvMainValue = findViewById(R.id.tv_main_value);
        mTvSubValue = findViewById(R.id.tv_sub_value);
        mTvLoading = findViewById(R.id.tv_loading);
        initChartStyle(mMainChart);
        initChartStyle(mSubChart);
        // 解决时间轴底部显示不全
        mSubChart.setExtraOffsets(0, 0, 0, 5);
        // 显示副图x轴时间
        mSubChart.getXAxis().setDrawLabels(true);
        // 格式化时间
        mSubChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DateUtils.timeStamp2Date(mData.getDatas().get((int) value).t);
            }
        });
        setChartListener();
    }

    /**
     * 设置监听
     */
    private void setChartListener() {
        mMainChart.setOnChartGestureListener(new CoupleChartGestureListener(mMainChart, mSubChart) {
            @Override
            public void onChartSingleTapped(MotionEvent me) {
                super.onChartSingleTapped(me);
                mMainText.performClick();
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                super.onChartGestureEnd(me, lastPerformedGesture);
                onNothingSelected();
            }
        });
        mSubChart.setOnChartGestureListener(new CoupleChartGestureListener(mSubChart, mMainChart) {
            @Override
            public void onChartSingleTapped(MotionEvent me) {
                super.onChartSingleTapped(me);
                mSubText.performClick();
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                super.onChartGestureEnd(me, lastPerformedGesture);
                onNothingSelected();
            }
        });
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
        mMainChart.setOnChartValueSelectedListener(this);
        mSubChart.setOnChartValueSelectedListener(this);
        mMainChart.setOnTouchListener(new ChartInfoViewHandler(mMainChart));
        mSubChart.setOnTouchListener(new ChartInfoViewHandler(mSubChart));
        mSubChart.setOnDrawListener(new OnDrawListener() {
            @Override
            public void onEntryAdded(Entry entry) {}
            @Override
            public void onEntryMoved(Entry entry) {}
            @Override
            public void onDrawFinished(DataSet<?> dataSet) {
                mTvLoading.setVisibility(GONE);
            }
        });
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
        // 只在第一次加载时，移动和放大图表
        isMoveToLast = false;
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
        // 设置位置,底部外侧
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置首尾的值是否自动调整，避免被遮挡
        xAxis.setAvoidFirstLastClipping(true);
        // 是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxis.setDrawAxisLine(false);
        // 在缩放时为轴设置最小间隔
        xAxis.setGranularity(5);
        // 设置x轴文字最多显示4条
        xAxis.setLabelCount(4);
        // 设置网格线颜色
        xAxis.setGridColor(gridColor);
        xAxis.setDrawLabels(false);
    }

    /**
     * 加载图表数据
     */
    private void loadChartData(CombinedChart chart, CombinedData data) {
        // 解决首尾只显示一半的问题
        // github  issues/2553,2641
        chart.getXAxis().setAxisMinimum(data.getXMin() - 0.5f);
        chart.getXAxis().setAxisMaximum(data.getXMax() + 0.5f);
        if (isMoveToLast) {
            // 移动到最后
            chart.moveViewToX(mData.getCandleEntries().size() - 1);
            // 放大
            chart.getViewPortHandler().getMatrixTouch().postScale(5f, 1f);
        }
        // 清空数据，防止图表类型不一值报空
        chart.setData(null);
        chart.setData(data);

        //        chart.setVisibleXRange(70, 0);

        chart.notifyDataSetChanged();
        chart.invalidate();
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
        lineDataSets.add(setLineDataSet(0, mData.getMa5Data()));
        lineDataSets.add(setLineDataSet(1, mData.getMa10Data()));
        lineDataSets.add(setLineDataSet(2, mData.getMa20Data()));

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
        // 绿跌，实体
        set.setDecreasingColor(mDropColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        // 红涨,实体
        set.setIncreasingColor(mRiseColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        // 不涨不跌，
        set.setNeutralColor(mDropColor);
        // 显示数值
        set.setDrawValues(true);
        set.setValueTextSize(10f);
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
        lineDataSets.add(setLineDataSet(0, mData.getBollDataUP()));
        lineDataSets.add(setLineDataSet(1, mData.getBollDataMB()));
        lineDataSets.add(setLineDataSet(2, mData.getBollDataDN()));

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
        set.setHighlightEnabled(false);
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
        sets.add(setLineDataSet(0, mData.getDifData(), false));
        sets.add(setLineDataSet(1, mData.getDeaData(), false));
        LineData lineData = new LineData(sets);

        data.setData(lineData);
        data.setData(barData);
        return data;
    }

    /**
     * 生成KDJ曲线
     *
     * @return LineData
     */
    private LineData generateKDJData() {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLineDataSet(0, mData.getK(), false));
        sets.add(setLineDataSet(1, mData.getD(), false));
        sets.add(setLineDataSet(2, mData.getJ(), false));
        return new LineData(sets);
    }

    /**
     * 生成RSI曲线
     *
     * @return LineData
     */
    private LineData generateRSIData() {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLineDataSet(0, mData.getRsiData6(), false));
        sets.add(setLineDataSet(1, mData.getRsiData12(), false));
        sets.add(setLineDataSet(2, mData.getRsiData24(), false));
        return new LineData(sets);
    }

    /**
     * 生成 成交量柱状图和均线
     *
     * @return
     */
    private CombinedData generateVolData(CombinedData data) {
        BarDataSet set = new BarDataSet(mData.getBarEntries(), "BarDataSet");
        // 高亮线关闭，用曲线联动
        set.setHighlightEnabled(false);
        // 是否绘制数值
        set.setDrawValues(false);
        // 使用左轴数据
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        // 设置颜色
        BarData barData = new BarData(set);
        set.setColors(mVolColos);
        barData.setBarWidth(0.6f);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setLineDataSet(0, mData.getMa5VolData(), false));
        sets.add(setLineDataSet(1, mData.getMa10VolData(), false));
        sets.add(setLineDataSet(2, mData.getMa20VolData(), false));
        LineData lineData = new LineData(sets);

        data.setData(lineData);
        data.setData(barData);

        return data;
    }

    /**
     * 构建 曲线
     * 主图由蜡烛图和曲线展示，曲线不显示高亮
     * <p>
     * 副图由曲线与蜡烛图高亮联动
     *
     * @param index       用于区分颜色
     * @param lineEntries data
     * @param isMainChart 判断是否为主图，主图曲线不显示高亮
     * @return LineDataSet
     */
    private LineDataSet setLineDataSet(int index, List<Entry> lineEntries, boolean... isMainChart) {
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setDrawValues(false);

        if (index == 0) {
            dataSet.setColor(mMa5Color);
        } else if (index == 1) {
            dataSet.setColor(mMa10Color);
        } else if (index == 2) {
            dataSet.setColor(mMa20Color);
        }

        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // index = 0必须，副图只添加一个高亮
        if (isMainChart.length > 0 && !isMainChart[0] && index == 0) {
            dataSet.setHighlightEnabled(true);
            dataSet.setDrawHorizontalHighlightIndicator(false);
        } else {
            dataSet.setHighlightEnabled(false);
        }

        return dataSet;
    }

    /**
     * 高亮选择时
     *
     * @param e The selected Entry
     * @param h The corresponding highlight object that contains information
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Highlight highlight = new Highlight(h.getX(), h.getY(), h.getDataSetIndex());
        mSubChart.highlightValues(new Highlight[]{highlight});
        mMainChart.highlightValues(new Highlight[]{highlight});
        updateValue(e.getX());
        if (mOnValueSelectedListener != null) {
            mOnValueSelectedListener.onValueSelected(true, mData.getDatas().get((int) e.getX()));
        }
    }

    /**
     * 高亮选择结束
     */
    @Override
    public void onNothingSelected() {
        mMainChart.highlightValues(null);
        mSubChart.highlightValues(null);
        updateValue(-1);
        if (mOnValueSelectedListener != null) {
            mOnValueSelectedListener.onValueSelected(false, null);
        }
    }


    private void updateValue(float x) {
        if (x < 0) {
            // 清空
            mTvMainValue.setText("");
            mTvSubValue.setText("");
            return;
        }
        int index = (int) x;
        String content = "";

        int mainType = mMainText.getType();
        if (mainType == DataParse.K || mainType == DataParse.BOLL) {
            String upperValue = String.valueOf(mData.getCandleEntries().get(index).getHigh());
            String lowerValue = String.valueOf(mData.getCandleEntries().get(index).getLow());
            // 高低中间值
            String midValue = (mData.getCandleEntries().get(index).getHigh() + mData.getCandleEntries().get(index).getLow()) / 2 + "";
            content = getFormatText(mMa10Color, "MID:", midValue) +
                    getFormatText(mMa5Color, "UPPER:", upperValue) +
                    getFormatText(mMa20Color, "LOWER:", lowerValue);
        } else if (mainType == DataParse.MA) {
            if (index >= mData.getMa5Data().size() || index >= mData.getMa10Data().size() || index >= mData.getMa20Data().size()) {
                mTvMainValue.setText("");
                return;
            }
            String ma5Value = String.valueOf(mData.getMa5Data().get(index).getY());
            String ma10Value = String.valueOf(mData.getMa10Data().get(index).getY());
            String ma20Value = String.valueOf(mData.getMa20Data().get(index).getY());
            content = getFormatText(mMa5Color, "ma5:", ma5Value) +
                    getFormatText(mMa10Color, "ma10:", ma10Value) +
                    getFormatText(mMa20Color, "ma20:", ma20Value);
        }
        setCompatHtmlText(content, mTvMainValue);

        // 副图
        int subType = mSubText.getType();
        if (subType == DataParse.VOL) {
            String volValue = String.valueOf(mData.getDatas().get(index).a);
            content = getFormatText(ContextCompat.getColor(getContext(), R.color.hint_value), "VOL:", volValue);
        } else if (subType == DataParse.MACD) {
            String diffValue = String.valueOf(mData.getDifData().get(index).getY());
            String deaValue = String.valueOf(mData.getDeaData().get(index).getY());
            String macdValue = String.valueOf(mData.getMacdData().get(index).getY());
            content = getFormatText(mMa5Color, "DIFF:", diffValue) +
                    getFormatText(mMa10Color, "DEA:", deaValue) +
                    getFormatText(mMa20Color, "MACD:", macdValue);
        } else if (subType == DataParse.KDJ) {
            String kValue = String.valueOf(mData.getK().get(index).getY());
            String dValue = String.valueOf(mData.getD().get(index).getY());
            String jValue = String.valueOf(mData.getJ().get(index).getY());
            content = getFormatText(mMa5Color, "K:", kValue) +
                    getFormatText(mMa10Color, "D:", dValue) +
                    getFormatText(mMa20Color, "J:", jValue);
        } else if (subType == DataParse.RSI) {
            if (index >= mData.getRsiData6().size() || index >= mData.getRsiData12().size() || index >= mData.getRsiData24().size()) {
                mTvSubValue.setText("");
                return;
            }
            String rsi6Value = String.valueOf(mData.getRsiData6().get(index).getY());
            String rsi12Value = String.valueOf(mData.getRsiData12().get(index).getY());
            String rsi24Value = String.valueOf(mData.getRsiData24().get(index).getY());
            content = getFormatText(mMa5Color, "RSI6:", rsi6Value) +
                    getFormatText(mMa10Color, "RSI12:", rsi12Value) +
                    getFormatText(mMa20Color, "RSI24:", rsi24Value);
        }
        setCompatHtmlText(content, mTvSubValue);
    }

    private void setCompatHtmlText(String content, TextView tv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv.setText(Html.fromHtml(content));
        }
    }

    private String getFormatText(int color, String hint, String value) {
        try {
            String text = mDecimalFormat.format(mDecimalFormat.parse(value));
            if ("NaN".equals(text) || "0.00".equals(text)) {
                text = "--";
            }
            return "<font color='" + color + "'>" + hint + text + "  </font>";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface OnValueSelectedListener {
        void onValueSelected(boolean isSelected, KLineBean data);
    }

    private OnValueSelectedListener mOnValueSelectedListener;

    public void setOnValueSelectedListener(OnValueSelectedListener listener) {
        this.mOnValueSelectedListener = listener;
    }
}
