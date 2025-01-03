package org.zzboy.search.domain;


import org.zzboy.constant.Constant;
import org.zzboy.util.Ipv6Util;

public class Ipv6Segment {

    private long startIp;

    private long endIp;

    private int bit;

    private String region;

    public Ipv6Segment() {
    }

    public Ipv6Segment(String regionData) {
        String[] ipv6Region = regionData.split("\\|");
        this.startIp = Ipv6Util.ipv6ToLong(ipv6Region[0]);;
        this.endIp = Ipv6Util.ipv6ToLong(ipv6Region[1]);
        this.bit = Integer.parseInt(ipv6Region[2]);
        this.region = ipv6Region[3] + Constant.DATA_SPLIT + ipv6Region[4] + Constant.DATA_SPLIT + ipv6Region[5];
    }

    public long getStartIp() {
        return startIp;
    }

    public void setStartIp(long startIp) {
        this.startIp = startIp;
    }

    public long getEndIp() {
        return endIp;
    }

    public void setEndIp(long endIp) {
        this.endIp = endIp;
    }

    public int getBit() {
        return bit;
    }

    public void setBit(int bit) {
        this.bit = bit;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return Ipv6Util.longToIpv6(startIp)+ Constant.DATA_SPLIT +Ipv6Util.longToIpv6(endIp)+ Constant.DATA_SPLIT +bit+ Constant.DATA_SPLIT +region;
    }
}
