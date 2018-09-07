package cn.sysmaster.kline.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.adapter.ChartDataPagerAdapter;
import cn.sysmaster.kline.entity.KLineBean;
import cn.sysmaster.kline.util.DateUtil;

public class ChartDataFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView tvOpen, tvClose, tvHigh, tvLow, tvVol, tvRange, tvTime;
    private RelativeLayout rlValueLayout;

    private List<Fragment> mFragments;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    private Calendar mCalendar = Calendar.getInstance(Locale.CHINA);

    /**
     * 涨跌颜色
     */
    private int mRiseColor, mDropColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_chart_data, container, false);
        mViewPager = view.findViewById(R.id.viewpager);
        mTabLayout = view.findViewById(R.id.tablayout);
        tvOpen = view.findViewById(R.id.tv_open);
        tvClose = view.findViewById(R.id.tv_close);
        tvHigh = view.findViewById(R.id.tv_high);
        tvLow = view.findViewById(R.id.tv_low);
        tvVol = view.findViewById(R.id.tv_vol);
        tvRange = view.findViewById(R.id.tv_range);
        tvTime = view.findViewById(R.id.tv_time);
        rlValueLayout = view.findViewById(R.id.value_layout);
        init();
        return view;
    }

    private void init() {
        mFragments = new ArrayList<>();
        //        mFragments.add(TimeLineChartFragment.newInstance());
        mFragments.add(KLineChartFragment.newInstance());
        mFragments.add(KLineChartFragment.newInstance());
        mFragments.add(KLineChartFragment.newInstance());
        mFragments.add(KLineChartFragment.newInstance());

        ChartDataPagerAdapter adapter = new ChartDataPagerAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        mRiseColor = ContextCompat.getColor(getContext(), R.color.trend_rise);
        mDropColor = ContextCompat.getColor(getContext(), R.color.trend_drop);
    }

    @SuppressLint("SetTextI18n")
    public void setValue(boolean isSelected, KLineBean data) {
        if (isSelected) {
            float range = (data.c - data.o) / data.o * 100;
            rlValueLayout.setVisibility(View.VISIBLE);
            // 设置数据
            tvOpen.setText(mDecimalFormat.format(data.o));
            tvClose.setText(mDecimalFormat.format(data.c));
            tvHigh.setText(mDecimalFormat.format(data.h));
            tvLow.setText(mDecimalFormat.format(data.l));
            tvVol.setText(mDecimalFormat.format(data.a));
            tvRange.setText((range > 0 ? "+" + mDecimalFormat.format(range) : mDecimalFormat.format(range)) + "%");
            mCalendar.setTimeInMillis(data.t * 1000);
            tvTime.setText(DateUtil.timeStamp2Date(data.t));
            // 设置颜色
            tvRange.setTextColor(range >= 0 ? mRiseColor : mDropColor);
            tvClose.setTextColor(range >= 0 ? mRiseColor : mDropColor);
            tvLow.setTextColor(data.l > data.c ? mRiseColor : mDropColor);
            tvHigh.setTextColor(data.h > data.o ? mRiseColor : mDropColor);
            tvOpen.setTextColor(data.h > data.o ? mRiseColor : mDropColor);
        } else {
            rlValueLayout.setVisibility(View.GONE);
        }
    }
}
