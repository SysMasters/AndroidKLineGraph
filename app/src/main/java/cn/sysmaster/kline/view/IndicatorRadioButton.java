package cn.sysmaster.kline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;

import cn.sysmaster.kline.R;

/**
 * @author wanglibo
 * @date 2018/9/12
 * @describe
 */
public class IndicatorRadioButton extends android.support.v7.widget.AppCompatRadioButton implements CompoundButton.OnCheckedChangeListener {

    private Paint mPaint;
    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    public IndicatorRadioButton(Context context) {
        super(context);
        init();
    }

    public IndicatorRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(dip2px(5));
        mPaint.setTextSize(getTextSize());
        setOnCheckedChangeListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = mPaint.measureText(getText().toString());
        float x = (getWidth() - width) / 2;
        if (isChecked()) {
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.theme_color));
        } else {
            mPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawLine(x, getHeight(), x + width, getHeight(), mPaint);
    }


    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isChecked()) {
            return false;
        }
        return true;
    }

}
