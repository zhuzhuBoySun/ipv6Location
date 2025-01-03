package org.zzboy.search.domain;


import org.zzboy.util.Ipv6Util;

public class MergeIpv6Segment extends Ipv6Segment{

    private int sourceIndex;

    private int mergeIndex;

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public int getMergeIndex() {
        return mergeIndex;
    }

    public void setMergeIndex(int mergeIndex) {
        this.mergeIndex = mergeIndex;
    }

    public MergeIpv6Segment(long start, long end,Integer bit, String region, int sourceIndex, int mergeIndex) {
        super.setStartIp(start);
        super.setEndIp(end);
        if (bit !=null){
            super.setBit(bit);
        }else {
            Ipv6Util.getBit(Ipv6Util.longToIpv6(super.getStartIp()), Ipv6Util.longToIpv6(super.getEndIp()));
        }

        super.setRegion(region);
        this.sourceIndex = sourceIndex;
        this.mergeIndex = mergeIndex;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
