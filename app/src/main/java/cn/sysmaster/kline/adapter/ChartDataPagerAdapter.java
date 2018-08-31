package cn.sysmaster.kline.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author wanglibo
 * @date 2018/8/28
 * @describe
 */
public class ChartDataPagerAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> mFragments;

    public ChartDataPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "分时";
        } else if (position == 1) {
            return "日K";
        } else if (position == 2) {
            return "周K";
        } else if (position == 3) {
            return "月K";
        } else if (position == 4) {
            return "1分";
        }
        return super.getPageTitle(position);
    }
}
