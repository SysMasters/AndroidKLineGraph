package cn.sysmaster.kline.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.sysmaster.kline.entity.DataParse;

/**
 * @author wanglibo
 * @date 2018/8/30
 * @describe 主图切换k线指标TextView
 */
public class MainGraphTextView extends SwitchTextView {


    public MainGraphTextView(Context context) {
        super(context);
    }

    public MainGraphTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainGraphTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setContent("K", "MA(5,10,20)", "BOLL(20,2)");
        setOnClickListener(this);

    }

    public int getType() {
        int index = mContents.indexOf(getText());
        if (index == 0) {
            return DataParse.K;
        } else if (index == 1) {
            return DataParse.MA;
        } else if (index == 2) {
            return DataParse.BOLL;
        }
        return DataParse.K;
    }

}
