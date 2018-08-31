package cn.sysmaster.kline.entity;

/**
 * @author wanglibo
 * @date 2018/8/29
 * @describe
 */
public class KLineBean {


    /**
     * a : 49929.268476828405136
     * c : 6469.53
     * h : 7140.01
     * l : 6395.71
     * o : 7114.96
     * symble : null
     * t : 1533657600
     */

    public float a;
    /**
     * 收收盘价
     */
    public float c;
    /**
     * 当天最高价（蜡烛图上影线）
     */
    public float h;
    /**
     * 当天最低价（蜡烛图下影线）
     */
    public float l;
    /**
     * 开盘价
     */
    public float o;
    public String symble;
    public long t;
}
