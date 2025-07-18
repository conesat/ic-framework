package cn.icframework.core.utils;

/**
 * 地图工具类
 * 提供经纬度距离计算等功能。
 *
 * @author hzl
 * @since 2023/5/11
 */
public class MapUtils {
    /**
     * 默认构造方法
     */
    public MapUtils() {}
    /**
     * 默认地球半径
     */
    private static double EARTH_RADIUS = 6371000;//赤道半径(单位m)

    /**
     * 转化为弧度(rad)
     * @param d 角度
     * @return 弧度
     */
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * 计算两点间距离
     * @param lon1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的经度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     */
    public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000.0;
        return s;
    }
}
