package cn.sysmaster.kline.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.sysmaster.kline.R;
import cn.sysmaster.kline.entity.DataParse;
import cn.sysmaster.kline.entity.KLineBean;
import cn.sysmaster.kline.view.KLineChartView;
import okhttp3.Call;

public class KLineChartFragment extends Fragment implements KLineChartView.OnValueSelectedListener {

    private KLineChartView mChartView;

    public static Fragment newInstance() {
        return new KLineChartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_kline_chart, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mChartView = view.findViewById(R.id.kline_chart_view);
        mChartView.setOnValueSelectedListener(this);
        requestKlineData();
    }


    /**
     * 请求k线数据
     */
    private void requestKlineData() {
        OkHttpUtils.get()
                .url("https://wcf.ihuoqiu.com/quotationapi/com.GetKLine?data=oGPetA2fbQMauSryr3S__2FHwWBPhFaQTRDym54os3UzXs1HPLwvAMrTJeyKD3RDR0Gr__2FQqGXJyrMraAqOuNjsQo3ykuKIVwpg1SlQ6XmB2__2FDAOl4xRR__2F95DzGGiYnmUcISs3ICBRms3Ibp1LoJkGCvGEZV2QtN0R9IFhgYc582E01KIyhiCB4ZRfaIMkTQiQRkeJTdjidOhFTG__2B2emCoeD8eusSTdw3Vc5d0WreJ9xOSbv__2FuhUVU2zB0u8B7VajPiXmmEV__2BHUztAI__2FRJghG0R__2FVw__2C__2C")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.i("", jsonArray.toString());
                            List<KLineBean> data = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<KLineBean>>() {
                            }.getType());
                            DataParse dataParse = new DataParse(data);
                            mChartView.setDataParse(dataParse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onValueSelected(boolean isSelected, KLineBean data) {
        ((ChartDataFragment) getParentFragment()).setValue(isSelected, data);
    }
}
