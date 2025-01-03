package org.zzboy.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.zzboy.constant.Constant;
import org.zzboy.search.domain.Ipv6Segment;
import org.zzboy.search.domain.MergeIpv6Segment;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IpMerger {
    public static List<Ipv6Segment> mergeIpSegments(List<Ipv6Segment> segments) {
        List<Ipv6Segment> mergedSegments = new ArrayList<>();
        Ipv6Segment previous = null;

        for (Ipv6Segment current : segments) {
            if (previous == null) {
                previous = current;
            } else {
                // 检查是否可以合并
                if (canMerge(previous, current)) {
                    previous.setEndIp(current.getEndIp());  // 更新结束 IP 地址
                } else {
                    mergedSegments.add(previous);  // 如果不能合并，存储previous
                    previous = current;  // 更新previous为current
                }
            }
        }
        if (previous != null) {
            mergedSegments.add(previous);  // 添加最后一个段
        }
        return mergedSegments;
    }

    private static boolean canMerge(Ipv6Segment a, Ipv6Segment b) {
        String startIp = Ipv6Util.longToIpv6(a.getEndIp());
        long aEnd = getEndIncrement(startIp);
        return aEnd==b.getStartIp() && StrUtil.equals(a.getRegion(), b.getRegion());
    }

    // 最后的ip进行自增1
    private static long getEndIncrement(String startIp){
        String[] split = startIp.split(Constant.IPV6_SPLIT);

        int count = 1;
        for (int i = split.length-1; i > 0; i--) {
            if (!StrUtil.equals(split[i],"0")){
                // 找到第一个不为0的地方
                Long l = Long.parseLong(split[i], 16);
                String s = Long.toBinaryString(l);
                for (int i1 = s.toCharArray().length-1; i1 > 0 ; i1--) {
                    if (s.toCharArray()[i1]=='1'){
                        l = l + count;
                        break;
                    }else {
                        count = count<<1;
                    }
                }
                split[i] = Long.toHexString(l);
                break;
            }
        }
        return Ipv6Util.ipv6ToLong(split[0]+Constant.IPV6_SPLIT+split[1]+Constant.IPV6_SPLIT+split[2]+Constant.IPV6_SPLIT+split[3]);
    }

    /**
     * ipv6段自增1
     * @param startIp
     * @return
     */
    private static long getEndIncrement(long startIp){
        String[] split = Ipv6Util.longToIpv6(startIp).split(Constant.IPV6_SPLIT);

        int count = 1;
        for (int i = split.length-1; i > 0; i--) {
            if (!StrUtil.equals(split[i],"0")){
                // 找到最后一段不为0的地方
                Long l = Long.parseLong(split[i], 16);
                String s = Long.toBinaryString(l);
                for (int i1 = s.toCharArray().length-1; i1 > 0 ; i1--) {
                    if (s.toCharArray()[i1]=='1'){
                        l = l + count;
                        break;
                    }else {
                        count = count<<1;
                    }
                }
                split[i] = Long.toHexString(l);
                break;
            }
        }
        return Ipv6Util.ipv6ToLong(split[0]+Constant.IPV6_SPLIT+split[1]+Constant.IPV6_SPLIT+split[2]+Constant.IPV6_SPLIT+split[3]);
    }

    /**
     * ipv6自减1
     * @param startIp
     * @return
     */
    public static long getStarReduction(String startIp){
        String[] split = startIp.split(Constant.IPV6_SPLIT);
        int fillIndex = -1;
        for (int i = split.length-1; i > 0; i--) {
            if (!StrUtil.equals(split[i],"0")){
                // 找到最后一段不为0的地方
                Long l = Long.parseLong(split[i], 16);
                split[i] = Long.toHexString(l-1);
                fillIndex = i+1;
                break;
            }
        }
        for (int i = fillIndex; i < split.length; i++){
            if (i == -1){
                break;
            }
            split[i] = Constant.IPV6_SPLIT_MAX;
        }
        return Ipv6Util.ipv6ToLong(split[0]+Constant.IPV6_SPLIT+split[1]+Constant.IPV6_SPLIT+split[2]+Constant.IPV6_SPLIT+split[3]);
    }

    /**
     * ipv6自减1
     * @param startIp
     * @return
     */
    private static long getStarReduction(long startIp){
        String[] split = Ipv6Util.longToIpv6(startIp).split(Constant.IPV6_SPLIT);
        int fillIndex = -1;
        for (int i = split.length-1; i > 0; i--) {
            if (!StrUtil.equals(split[i],"0")){
                // 找到最后一段不为0的地方
                Long l = Long.parseLong(split[i], 16);
                split[i] = Long.toHexString(l-1);
                fillIndex = i+1;
                break;
            }
        }
        for (int i = fillIndex; i < split.length; i++){
            if (i == -1){
                break;
            }
            split[i] = Constant.IPV6_SPLIT_MAX;
        }
        return Ipv6Util.ipv6ToLong(split[0]+Constant.IPV6_SPLIT+split[1]+Constant.IPV6_SPLIT+split[2]+Constant.IPV6_SPLIT+split[3]);
    }

    /**
     * 文件合并相同范围的段，如果ip段相同，则用mergePath中数据覆盖
     * @param sourcePath 源文件
     * @param mergePath 需要更新数据的ip段文件
     * @param targetPath 合并后的文件
     */
    public static void mergerIpv6(String sourcePath,String mergePath,String targetPath){
        List<String> sourceData = FileUtil.readLines(new File(sourcePath),"UTF-8");
        List<String> mergeData = FileUtil.readLines(new File(mergePath),"UTF-8");

        //排序
        Ipv6Util.sortIpv6ForText(sourceData);
        Ipv6Util.sortIpv6ForText(mergeData);

        //转换为Ipv6Segment
        List<Ipv6Segment> sourceSegments = sourceData.stream().map(s -> new Ipv6Segment(s)).collect(Collectors.toList());
        List<Ipv6Segment> mergeSegments = mergeData.stream().map(s -> new Ipv6Segment(s)).collect(Collectors.toList());

        //合并相同段
        sourceSegments = mergeIpSegments(sourceSegments);
        mergeSegments = mergeIpSegments(mergeSegments);

        List<Ipv6Segment> result = new ArrayList<>();
        int mergeIndex = 0;
        for (int i = 0; i < sourceSegments.size(); i++) {
            Ipv6Segment sourceSegment = sourceSegments.get(i);
            if (mergeIndex >= mergeSegments.size()){
                result.add(sourceSegment);
                continue;
            }
            Ipv6Segment mergeSegment = mergeSegments.get(mergeIndex);

            if (mergeSegment.getStartIp()>sourceSegment.getEndIp()){
                //不冲突且比他大，则添加到结果中
                result.add(sourceSegment);
                continue;
            }
            //----------------------冲突了,需要进行合并---------------------------------
            MergeIpv6Segment mergeIpv6Segment = getMergeSegment(sourceSegment, mergeSegment, i, mergeIndex);
            result.add(mergeIpv6Segment);
            i = mergeIpv6Segment.getSourceIndex();
            mergeIndex = mergeIpv6Segment.getMergeIndex();
        }
        if (mergeIndex<mergeSegments.size()){
            result.addAll(mergeSegments.subList(mergeIndex,mergeSegments.size()));
        }
        List<String> newResult = result.stream().map(Ipv6Segment::toString).collect(Collectors.toList());
        FileUtil.writeLines(newResult,new File(targetPath),"UTF-8");
    }

    /**
     * 当两段ip有冲突时，需要合并
     * @param sourceSegment
     * @param mergeSegment
     * @param i 当前sourceSegment的索引 当sourceSegment 的结束地址大于mergeSegment的结束地址时，且地址不同，需要将i设置为i-1，且把开始地址和结束地址更新
     * @return
     */
    private static MergeIpv6Segment getMergeSegment(Ipv6Segment sourceSegment,Ipv6Segment mergeSegment,int i,int index){
        MergeIpv6Segment result = new MergeIpv6Segment(sourceSegment.getStartIp(),sourceSegment.getEndIp(),sourceSegment.getBit(), sourceSegment.getRegion(), i, index);
        // 地址相同
        if (StrUtil.equals(mergeSegment.getRegion(), sourceSegment.getRegion())){
            if (sourceSegment.getStartIp() <= mergeSegment.getStartIp() && sourceSegment.getEndIp() == mergeSegment.getEndIp()){
                // 完全相同，不需要合并
                result = new MergeIpv6Segment(sourceSegment.getStartIp(), sourceSegment.getEndIp(),sourceSegment.getBit(), sourceSegment.getRegion(), i, index + 1);
            }else if (sourceSegment.getStartIp() == mergeSegment.getStartIp() && sourceSegment.getEndIp() < mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(),sourceSegment.getEndIp(),null , sourceSegment.getRegion(), i, index );
                mergeSegment.setStartIp(getEndIncrement(result.getEndIp()));
            }else if (sourceSegment.getStartIp() == mergeSegment.getStartIp() && sourceSegment.getEndIp() > mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(),mergeSegment.getEndIp(),null , sourceSegment.getRegion(), i-1, index+1 );
                sourceSegment.setStartIp(getEndIncrement(mergeSegment.getEndIp()));
                sourceSegment.setBit(Ipv6Util.getBit(Ipv6Util.longToIpv6(sourceSegment.getStartIp()), Ipv6Util.longToIpv6(sourceSegment.getEndIp())));
            }else if (sourceSegment.getStartIp() < mergeSegment.getStartIp() && sourceSegment.getEndIp() < mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(),getStarReduction(mergeSegment.getStartIp()),null , sourceSegment.getRegion(), i, index );
            }else if (sourceSegment.getStartIp() < mergeSegment.getStartIp() && sourceSegment.getEndIp() > mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(), sourceSegment.getEndIp(),sourceSegment.getBit(), sourceSegment.getRegion(), i, index + 1);
            }
        }else {
        //地址不一致2408:8206:0:0|2408:8206:bff:0|36|中国|北京|北京
            if (sourceSegment.getStartIp() == mergeSegment.getStartIp() && sourceSegment.getEndIp() == mergeSegment.getEndIp()){
                // 完全相同，以mergeSegment为准
                result = new MergeIpv6Segment(mergeSegment.getStartIp(), mergeSegment.getEndIp(),mergeSegment.getBit(), mergeSegment.getRegion(), i, index + 1);
            }else if (sourceSegment.getStartIp() == mergeSegment.getStartIp() && sourceSegment.getEndIp() < mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(),sourceSegment.getEndIp(),null , mergeSegment.getRegion(), i, index );
                mergeSegment.setStartIp(getEndIncrement(sourceSegment.getEndIp()));
            }else if (sourceSegment.getStartIp() == mergeSegment.getStartIp() && sourceSegment.getEndIp() > mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(mergeSegment.getStartIp(),mergeSegment.getEndIp(),null , mergeSegment.getRegion(), i-1, index+1 );
                sourceSegment.setStartIp(getEndIncrement(mergeSegment.getEndIp()));
                sourceSegment.setBit(Ipv6Util.getBit(Ipv6Util.longToIpv6(sourceSegment.getStartIp()), Ipv6Util.longToIpv6(sourceSegment.getEndIp())));
            }else if (sourceSegment.getStartIp() < mergeSegment.getStartIp() && sourceSegment.getEndIp() <= mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(),getStarReduction(mergeSegment.getStartIp()),null , sourceSegment.getRegion(), i, index );
            }else if (sourceSegment.getStartIp() < mergeSegment.getStartIp() && sourceSegment.getEndIp() > mergeSegment.getEndIp()){
                result = new MergeIpv6Segment(sourceSegment.getStartIp(), mergeSegment.getEndIp(),sourceSegment.getBit(), mergeSegment.getRegion(), i, index + 1);
                sourceSegment.setStartIp(getEndIncrement(mergeSegment.getEndIp()));
            }
        }
        return result;
    }
}
