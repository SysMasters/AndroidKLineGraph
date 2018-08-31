package cn.sysmaster.kline.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.sysmaster.kline.entity.DataParse;

/**
 * @author wanglibo
 * @date 2018/8/30
 * @describe 副图切换k线指标TextView
 */
public class SubGraphTextView extends SwitchTextView {

    public SubGraphTextView(Context context) {
        this(context, null);
    }

    public SubGraphTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubGraphTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContent("VOL", "MACD(12,26,9)", "KDJ(9,3,3)", "RSI(6,12,24)");
        setOnClickListener(this);
    }

    public int getType() {
        int index = mContents.indexOf(getText());
        if (index == 0) {
            return DataParse.VOL;
        } else if (index == 1) {
            return DataParse.MACD;
        } else if (index == 2) {
            return DataParse.KDJ;
        } else if (index == 3) {
            return DataParse.RSI;
        }
        return DataParse.VOL;
    }
}
