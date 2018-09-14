package cn.sysmaster.kline.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.adapter.MinuteAdapter;
import cn.sysmaster.kline.entity.KLineBean;
import cn.sysmaster.kline.util.DateUtils;
import cn.sysmaster.kline.util.NumberUtils;

public class ChartDataFragment extends Fragment {

    private FrameLayout mContainer;
    private TextView tvOpen, tvClose, tvHigh, tvLow, tvVol, tvRange, tvTime;
    private RelativeLayout rlValueLayout;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    private Calendar mCalendar = Calendar.getInstance(Locale.CHINA);

    private TimeLineChartFragment mLineChartFragment;

    private PopupWindow mPopupWindow;

    /**
     * 涨跌颜色
     */
    private int mRiseColor, mDropColor;
    private int mButtonWidth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_chart_data, container, false);
        mContainer = view.findViewById(R.id.fragment_container);
        tvOpen = view.findViewById(R.id.tv_open);
        tvClose = view.findViewById(R.id.tv_close);
        tvHigh = view.findViewById(R.id.tv_high);
        tvLow = view.findViewById(R.id.tv_low);
        tvVol = view.findViewById(R.id.tv_vol);
        tvRange = view.findViewById(R.id.tv_range);
        tvTime = view.findViewById(R.id.tv_time);
        rlValueLayout = view.findViewById(R.id.value_layout);
        mRadioGroup = view.findViewById(R.id.radio_group);
        mRadioButton = view.findViewById(R.id.rb_minute);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                if (checkedId == R.id.rb_time) {
                    transaction.replace(R.id.fragment_container, mLineChartFragment);
                } else {
                    transaction.replace(R.id.fragment_container, KLineChartFragment.newInstance());
                }
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        init();
        return view;
    }

    private void init() {
        mDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        mRiseColor = ContextCompat.getColor(getContext(), R.color.trend_rise);
        mDropColor = ContextCompat.getColor(getContext(), R.color.trend_drop);
        mRadioButton.post(new Runnable() {
            @Override
            public void run() {
                mButtonWidth = mRadioButton.getWidth();
            }
        });
        mLineChartFragment = TimeLineChartFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.fragment_container, mLineChartFragment).commit();
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
            tvVol.setText(NumberUtils.amountConversion(data.a));
            tvRange.setText((range > 0 ? "+" + mDecimalFormat.format(range) : mDecimalFormat.format(range)) + "%");
            mCalendar.setTimeInMillis(data.t * 1000);
            tvTime.setText(DateUtils.timeStamp2Date(data.t));
            // 设置颜色
            tvRange.setTextColor(range >= 0 ? mRiseColor : mDropColor);
            tvClose.setTextColor(range >= 0 ? mRiseColor : mDropColor);
            tvLow.setTextColor(data.l > data.c ? mRiseColor : mDropColor);
            tvHigh.setTextColor(data.h > data.o ? mRiseColor : mDropColor);
            tvOpen.setTextColor(data.o > data.c ? mRiseColor : mDropColor);
        } else {
            rlValueLayout.setVisibility(View.GONE);
        }
    }

    private void showPopupWindow() {
        if (mPopupWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.popup_show_list, null);
            ListView listView = view.findViewById(R.id.listview);
            final MinuteAdapter adapter = new MinuteAdapter(getContext());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setSelectPosition(position);
                }
            });

            listView.setAdapter(adapter);
            mPopupWindow = new PopupWindow(mButtonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setContentView(view);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(true);
//            mPopupWindow.setTouchable(true);
        }
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(mRadioButton);
        }
    }
}
