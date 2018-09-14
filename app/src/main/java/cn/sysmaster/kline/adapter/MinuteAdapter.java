package cn.sysmaster.kline.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.sysmaster.kline.R;

/**
 * @author wanglibo
 * @date 2018/9/14
 * @describe
 */
public class MinuteAdapter extends BaseAdapter {

    private String[] datas;
    private int selectPosition = -1;
    private Context mContext;

    public MinuteAdapter(@NonNull Context context) {
        super();
        this.mContext = context;
        this.datas = context.getResources().getStringArray(R.array.times);
    }


    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public String getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = this.datas[position];
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.show_list_item, null);
        TextView tv = convertView.findViewById(R.id.text1);
        tv.setText(item);
        if (selectPosition == position) {
            tv.setSelected(true);
        }
        return convertView;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
