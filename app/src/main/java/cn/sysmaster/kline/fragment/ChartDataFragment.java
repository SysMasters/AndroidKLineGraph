package cn.sysmaster.kline.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.adapter.ChartDataPagerAdapter;

public class ChartDataFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private List<Fragment> mFragments;

    public Fragment newInstance() {
        return new ChartDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_chart_data, container, false);
        mViewPager = view.findViewById(R.id.viewpager);
        mTabLayout = view.findViewById(R.id.tablayout);
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
    }
}
