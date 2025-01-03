package org.zzboy.search.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;
import org.zzboy.util.IpDBUtil;
import org.zzboy.util.Ipv6Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ipv6Make {

    /**
     * 排序ipv6文件 并返回排序后的文件路径
     * @param path
     * @return
     */
    public static String sort(String path){
        List<String> readLines = FileUtil.readLines(new File(path), StandardCharsets.UTF_8);
        Ipv6Util.sortIpv6ForText(readLines);
        path = path.replace(".txt","_sort.txt");
        return FileUtil.writeLines(readLines,path, StandardCharsets.UTF_8).getPath();
    }

    /**
     * 创建ipv6db文件
     *
     * @param path ipv6数据源文件 格式：startIp|endIp|bit|国家|省|市
     */
    public static void createDB(String path) throws IOException {
        Map<String, Short> regionMap = new HashMap<>();

        Map<Short,String> regionMapData = new HashMap<>();


        Ipv6DB ipv6DB = new Ipv6DB();
        //排序
        path = sort(path);
        if (ipv6DB.line == 0){
            int length = (int) Files.lines(new File(path).toPath()).count();
            ipv6DB.line = length;
        }
        ipv6DB.segment = new long[ipv6DB.line*2];
        ipv6DB.region= new short[ipv6DB.line];

        int offset = 0;
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            //创建ipv6db文件
            String line = FileUtil.readLine(file, StandardCharsets.UTF_8);
            while (line != null && !line.isEmpty()) {
                Ipv6Segment ipv6Segment = new Ipv6Segment(line);
                String region = ipv6Segment.getRegion();

                ipv6DB.segment[offset++] = ipv6Segment.getStartIp();
                ipv6DB.segment[offset] = ipv6Segment.getEndIp();
                short index;
                //不存在地址集合中
                if (!regionMapData.containsValue(region)) {
                    short andIncrement = ipv6DB.regionNo++;
                    regionMapData.put(andIncrement, region);
                    regionMap.put(region, andIncrement);
                    index = andIncrement;
                } else {
                    //存在地址集合中 赋值索引
                    index = regionMap.get(region);
                }
                ipv6DB.region[offset / 2] = index;
                offset ++;
                line = FileUtil.readLine(file, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LogFactory.get().log(Level.ERROR,"createDB error:",e);
            throw new IOException("createDB error:",e);
        }
        ipv6DB.regionData = new String[regionMapData.entrySet().size()];
        for (Map.Entry<Short, String> entry : regionMapData.entrySet()) {
            ipv6DB.regionData[entry.getKey()] = entry.getValue();
        }
        FileUtil.writeBytes(IpDBUtil.toBytes(ipv6DB), new File(path.replace(".txt", ".db")));
    }


}
