package cn.sysmaster.kline.listener;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture;
import com.github.mikephil.charting.listener.OnChartGestureListener;

public class CoupleChartGestureListener implements OnChartGestureListener {

    private BarLineChartBase srcChart;
    private BarLineChartBase dstChart;

    public CoupleChartGestureListener(BarLineChartBase srcChart, BarLineChartBase dstChart) {
        this.srcChart = srcChart;
        this.dstChart = dstChart;
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            // or highlightTouch(null) for callback to onNothingSelected(…)
            srcChart.highlightValues(null);
            dstChart.highlightValues(null);
        }

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartGesture lastPerformedGesture) {
        syncCharts();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        syncCharts();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        syncCharts();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        syncCharts();
    }

    public void syncCharts() {
        float[] srcVals = new float[9];
        float[] dstVals = new float[9];
        this.srcChart.getViewPortHandler().getMatrixTouch().getValues(srcVals);
        if (dstChart.getVisibility() == View.VISIBLE) {
            Matrix dstMatrix = dstChart.getViewPortHandler().getMatrixTouch();
            dstMatrix.getValues(dstVals);
            dstVals[0] = srcVals[0];
            dstVals[2] = srcVals[2];
            dstMatrix.setValues(dstVals);
            dstChart.getViewPortHandler().refresh(dstMatrix, dstChart, true);
        }
    }
}