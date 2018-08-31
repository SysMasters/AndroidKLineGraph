package cn.sysmaster.kline.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.sysmaster.kline.R;

public class TimeLineChartFragment extends Fragment {


    public static Fragment newInstance() {
        return new TimeLineChartFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_time_line_chart, container, false);
        init();
        return view;
    }

    private void init() {

    }

}
