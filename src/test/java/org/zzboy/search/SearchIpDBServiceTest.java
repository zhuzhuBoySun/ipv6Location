package org.zzboy.search;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import org.junit.Test;
import org.zzboy.search.domain.IpRegion;
import org.zzboy.search.domain.Ipv6DB;
import org.zzboy.search.domain.Ipv6Make;
import org.zzboy.search.domain.Ipv6Segment;
import org.zzboy.util.Ipv6Util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class SearchIpDBServiceTest {

    @Test
    public void searchIpv6Region() {

    }

    @Test
    public void searchIpv6RegionStr() {
        SearchIpDBService searchIpDBService = new SearchIpDBService();
        IpRegion ipRegion = searchIpDBService.searchIpv6Region("240e:47f:2200:32e8::");
        System.out.println(ipRegion);
    }

    /**
     * 校验文件是否存在冲突段，并生成db文件，校验db数据是否存在冲突段
     * true 无冲突，false 有冲突
     */
    @Test
    public void verify() throws IOException {
        String yyyyMMdd = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        List<String> ipv6List = FileUtil.readLines("\\data\\ipHome\\"+yyyyMMdd+"\\ipv6_m3_split_new_sort.txt", "utf-8");
        Ipv6Util.sortIpv6ForText(ipv6List);
        for (int i = 0; i < ipv6List.size(); i++) {
            if (i>0 && i<ipv6List.size()-1){
                Ipv6Segment ipv6Segment = new Ipv6Segment(ipv6List.get(i));
                Ipv6Segment ipv6Segment_1 = new Ipv6Segment(ipv6List.get(i-1));
                if (ipv6Segment.getStartIp()>=ipv6Segment_1.getStartIp() && ipv6Segment.getStartIp()<=ipv6Segment_1.getEndIp()){
                    System.out.println(ipv6Segment_1.toString());
                    System.out.println(ipv6Segment.toString());
                    System.out.println("-----------");
                }
            }
        }
        Ipv6Make.createDB("\\data\\ipHome\\"+yyyyMMdd+"\\ipv6_m2_split.txt");
        Ipv6DB ipv6DB =Ipv6DB.init("\\data\\ipHome\\"+yyyyMMdd+"\\ipv6_m3_split_new_sort.db");
        System.out.println(verifyDB(ipv6DB, "\\data\\ipHome\\" + yyyyMMdd + "\\ipv6_m3_split_new_sort.txt"));
    }

    /**
     * 数据校验
     * @param ipv6DB 待校验的ipv6DB
     * @param sourcePath 原始数据源路径
     * @return
     */
    public boolean verifyDB(Ipv6DB ipv6DB,String sourcePath){
        File file = new File(sourcePath);
        if (!file.exists()){
            return false;
        }
        if(ipv6DB.getSegment().length != ipv6DB.getRegion().length*2 ){
            return false;
        }
        List<String> ipv6List = FileUtil.readLines(sourcePath, "utf-8");
        if (ipv6List.size()!= ipv6DB.getLine()){
            return false;
        }
        Ipv6Util.sortIpv6ForText(ipv6List);
        for (int i = 0; i < ipv6List.size(); i++) {
            if (i>0 && i<ipv6List.size()-1){
                Ipv6Segment ipv6Segment = new Ipv6Segment(ipv6List.get(i));
                Ipv6Segment ipv6Segment_1 = new Ipv6Segment(ipv6List.get(i-1));
                if (ipv6Segment.getStartIp()>=ipv6Segment_1.getStartIp() && ipv6Segment.getStartIp()<=ipv6Segment_1.getEndIp()){
                    return false;
                }
            }
        }

        return true;
    }

}