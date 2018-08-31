package cn.sysmaster.kline.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.List;

/**
 * @author wanglibo
 * @date 2018/8/30
 * @describe 循环切换文本TextView
 */
public class SwitchTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    /**
     * 所有内容
     */
    protected List<String> mContents;
    private OnSwitchClickListener mListener;

    public SwitchTextView(Context context) {
        super(context);
    }

    public SwitchTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置内容
     *
     * @param contents 内容集合
     */
    public void setContent(String... contents) {
        this.mContents = Arrays.asList(contents);
        if (contents.length > 0) {
            setText(contents[0]);
        }
    }

    @Override
    public void onClick(View v) {
        setText(getNextContent());
        if (mListener != null) {
            mListener.onSwitch();
        }
    }

    /**
     * 获取下一个内容
     *
     * @return
     */
    public String getNextContent() {
        String content = "";
        if (mContents != null && mContents.size() > 0) {
            int index = mContents.indexOf(getText());
            if (index == mContents.size() - 1) {
                index = 0;
            } else {
                index += 1;
            }
            content = mContents.get(index);
        }
        return content;
    }


    public interface OnSwitchClickListener {
        void onSwitch();
    }

    public void setOnSwitchClickListener(OnSwitchClickListener listener) {
        mListener = listener;
    }
}
