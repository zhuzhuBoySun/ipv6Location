package org.zzboy.search.domain;


import org.zzboy.constant.Constant;
import org.zzboy.util.IpDBUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author zhuzhuboy
 * @version 1.0
 */
public class Ipv6DB implements Serializable {

    private static final long serialVersionUID = 6367525174238519984L;

    //ipv6 前64位存储 startIp,endIp ,length = 文件行数*2
    protected long[] segment;

    //ipv6段存储的地理位置(数值映射的地址),下标为第n段,对应的region[n]--(segment[2n],segment[2n+1]);
    protected short[] region;

    // 地理位置数据
    protected String[] regionData;

    //地理位置数
    protected short regionNo = 0;

    protected int line = 0;

    /**
     * 初始化ip库
     * @param path ipv6段文件路径
     */
    public static Ipv6DB init(String path) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))){
            return IpDBUtil.bytestoIpv6DB(inputStream);
        }catch (IOException e){
            throw new IllegalArgumentException("ipv6段文件读取失败",e);
        }
    }

    /**
     * 初始化ip库
     *
     * @param inputStream ipv6段文件流
     */
    public static Ipv6DB init(InputStream inputStream) {
        try (InputStream inputStreamTemp = inputStream) {
            return IpDBUtil.bytestoIpv6DB(inputStreamTemp);
        } catch (IOException e) {
            throw new IllegalArgumentException("ipv6段文件读取失败", e);
        }
    }

    /**
     * 根据ip查询地理位置 国家|省|城市
     * @param ip
     */
    public IpRegion searchRegion(long ip){
        String region = binarySearch(ip);
        String[] regions = region.split("\\|");
        IpRegion ipRegion = new IpRegion();
        if (!Constant.unknown.equals(regions[0])){
            ipRegion.setCounty(regions[0]);
        }
        if (!Constant.unknown.equals(regions[1])){
            ipRegion.setProvince(regions[1]);
        }
        if (!Constant.unknown.equals(regions[2])){
            ipRegion.setCity(regions[2]);
        }
        return ipRegion;
    }

    /**
     * 根据ip查询地理位置 国家|省|城市
     * @param ip
     */
    public String searchRegionString(long ip){
        return binarySearch(ip);
    }

    /**
     * 二分查找ipv6段
     * @param ip
     */
    private String binarySearch(long ip){
        int low = 0, high = region.length; // low, high 分别为 region 数组的左右边界
        int index = -1;
        while (low <= high){ // 循环直到找到匹配的段
            int mid = (low + high) / 2; // 计算中间索引
            long startIp = segment[mid*2];
            long endIp = segment[mid*2 + 1];
            if (ip >= startIp && ip <= endIp) {
                // 找到了匹配的段
                index = mid;
                break;
            } else if (ip < startIp) {
                high = mid - 1; // 向左半边查找
            } else {
                low = mid + 1; // 向右半边查找
            }
        }
        if (index == -1){
            return Constant.unknown_region;
        }
        return this.regionData[region[index]];
    }

    public long[] getSegment() {
        return segment;
    }

    public short[] getRegion() {
        return region;
    }

    public String[] getRegionData() {
        return regionData;
    }

    public short getRegionNo() {
        return regionNo;
    }

    public int getLine() {
        return line;
    }
}
