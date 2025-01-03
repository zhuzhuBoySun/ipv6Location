package org.zzboy.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.log.level.Level;
import org.zzboy.constant.Constant;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ipv6转换long
 */
public class Ipv6Util {


    /**
     * ipv6转换long 取前64位
     **/
    public static long ipv6ToLong(String ipv6){
        //todo 检查是否为有效的IPv6地址
        String[] ipv6Split = ipv6.split(Constant.IPV6_SPLIT);
        long result = 0;
        // 只需要前四组
        for (int i = 0; i < 4; i++) {
            // 将每组16位的十六进制字符串转换为long
            long part = Long.parseLong(ipv6Split[i], 16);
            // 左移以合并到结果中
            result = (result << 16) + part;
        }
        return result;
    }

    /**
     * ipv6的前64位long转string 保留前64位
     **/
    public static String longToIpv6(long ipv6Long) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            // 右移以获取每组16位
            long part = (ipv6Long >> (i * 16)) & 0xFFFF;
            // 将每组16位的long转换为十六进制字符串
            String hexPart = Long.toHexString(part);
            // 如果不是第一组，则在前面加上冒号
            if (i < 3) {
                sb.append(":");
            }
            sb.append(hexPart);
        }
        return sb.toString();
    }

    /**
     * 解析ipv6将短的ipv6补全
     * @param ipv6Short
     * @return
     */
    public static String parseIpv6(String ipv6Short){
        String[] split = ipv6Short.split(Constant.IPV6_SPLIT);
        StringBuilder sb = new StringBuilder(ipv6Short);
        if (split.length<8){
            for (int i = 0; i < 8-split.length; i++) {
                sb.append(":0");
            }
        }
        return sb.toString();
    }

    /**
     * 解析ipv6 将::转换 取前64位
     * @param ipv6
     * @return
     */
    public static String parseIpv6Short(String ipv6){
        if (!ipv6.contains(Constant.IPV6_ZERO_IGNORE)){
            ipv6 = Ipv6Util.parseIpv6(ipv6);
            String[] split = ipv6.split(Constant.IPV6_SPLIT);
            String.join(Constant.IPV6_SPLIT,Arrays.asList(split[0],split[1],split[2],split[3]));
            return ipv6;
        }
        String[] split = ipv6.split(Constant.IPV6_ZERO_IGNORE);
        StringBuilder sb = new StringBuilder(split[0]);
        while (StrUtil.count(sb.toString(),Constant.IPV6_SPLIT)<7){
            sb.append(":0");
        }
        String[] strings = sb.toString().split(Constant.IPV6_SPLIT);
        return String.join(Constant.IPV6_SPLIT,Arrays.asList(strings[0],strings[1],strings[2],strings[3]));
    }

    /**
     * 获取IPv6结束地址
     * @param startIpv6 整段的ipv6
     * @param bit 网络位数
     * @return 结束地址
     */
    public static String getEndIpv6Short(String startIpv6, int bit) { //2001:7fa:0:1 64
        if (startIpv6.contains(Constant.IPV6_ZERO_IGNORE)) {
            startIpv6 = parseIpv6Short(startIpv6);
        }
        //获取当前分割的段 0第一段
        int index = bit / 16; // 4
        List<String> ipSplit = new ArrayList<>();
        String[] split = startIpv6.split(Constant.IPV6_SPLIT);
        for (int i = 0; i < 4; i++) {
            if (index == i ) {
                int endNum = Integer.parseInt(split[i], 16) + (1 << (16 - bit % 16)) - 1;
                ipSplit.add(Integer.toHexString(endNum));
            } else if (Integer.parseInt(split[i], 16) == 0 && i > index) {
                ipSplit.add(Constant.IPV6_SPLIT_MAX);
            } else {
                ipSplit.add(split[i]);
            }
        }
        return StrUtil.join(Constant.IPV6_SPLIT,ipSplit);
    }

    /**
     * 将ipv6地址group分割成多个段
     * @param startIpv6 开始地址
     * @param endIpv6 结束地址
     * @param bit 网络位数
     * @param splitBit 划分截至的位数
     * @return
     */
    public static List<String> ipv6SplitToList(String startIpv6,String endIpv6,int bit,int splitBit){
        List<String> ipv6s = new ArrayList<>();
        //加入开始地址
        ipv6s.add(startIpv6);
        String[] startIpSplit = startIpv6.split(Constant.IPV6_SPLIT);
        String[] endIpSplit = endIpv6.split(Constant.IPV6_SPLIT);
        // 1.计算开始地址和结束地址的差值 每段是16位 每段的最大值是65535
        // 0001 ->> ffff 65535
        // 计算相差位数
        int diffCount = (splitBit - bit);

        // 计算最后一段分割位数 如网络号：48 ，划分位：50 即2 --> 最后一段前2位进行划分 00... -> 11...
        int tailBit = splitBit % 16;

        if(diffCount>16 || 16-(bit % 16) < diffCount){
            //跨段 --> 跨段了要考虑分割的数量 * 65535 如ip网络位是32 划分的位数是50 即 0xffff * 0xff 次分割

        }else {
            //同段
            String startTailIpSplit = startIpSplit[bit/16];
            String endTailIpSplit = endIpSplit[bit/16];
            int startBit = Integer.parseInt(startTailIpSplit,16) >> (16-tailBit);
            int endBit = Integer.parseInt(endTailIpSplit,16) >> (16-tailBit);
            while (startBit < endBit){
                //计算出当前分割段 + 1 位的数值
                int thisSplitBit = (startBit+1) << (16-tailBit);
                for (int i = 0; i < startIpSplit.length; i++) {
                    if (bit/16 == i){
                        startIpSplit[i] = Integer.toHexString(thisSplitBit);
                    }
                }
                ipv6s.add(String.join(Constant.IPV6_SPLIT,startIpSplit));
                startBit++;
            }
        }
        //加入结束地址
        ipv6s.add(endIpv6);
        return ipv6s;
    }


    /**
     * 获取ipv6的网络位数
     * @param startIpv6 开始地址
     * @param endIpv6 结束地址
     * @return
     */
    public static int getBit(String startIpv6, String endIpv6) {
        try {
            startIpv6 = Ipv6Util.parseIpv6(startIpv6);
            endIpv6 = Ipv6Util.parseIpv6(endIpv6);
            // 将开始和结束的IPv6地址转换为InetAddress对象
            InetAddress startAddress = InetAddress.getByName(startIpv6);
            InetAddress endAddress = InetAddress.getByName(endIpv6);

            // 确保都是IPv6地址
            if (!(startAddress instanceof Inet6Address) || !(endAddress instanceof Inet6Address)) {
                throw new IllegalArgumentException("Not valid IPv6 addresses");
            }

            // 将InetAddress对象转换为BigInteger
            BigInteger startBigInt = new BigInteger(1, startAddress.getAddress());
            BigInteger endBigInt = new BigInteger(1, endAddress.getAddress());

            // 计算两个BigInteger之间的异或值
            BigInteger xor = startBigInt.xor(endBigInt);

            // 计算异或值中第一个为1的位的位置，然后用128减去这个位置得到共同前缀的长度
            int networkPrefixLength = 128 - xor.bitLength();
            return networkPrefixLength;
        } catch (UnknownHostException e) {
            LogFactory.get().log(Level.ERROR,"startIpv6 = {}, endIpv6 = {}",startIpv6,endIpv6);
            throw new IllegalArgumentException("Invalid IPv6 address format", e);
        }
    }


    /**
     * 对ipv6进行排序
     * @param texts startIp|endIp|....
     */
    public static void sortIpv6ForText(List<String> texts){
        Collections.sort(texts, new Comparator<String>() {
            @Override
            public int compare(String ip1, String ip2) {
                try {
                    return Long.compare(ipv6ToLong(ip1.split("\\|")[0]),ipv6ToLong(ip2.split("\\|")[0]));
                } catch (Exception e) {
                    // 如果IPv6地址格式不正确，抛出异常或进行其他处理
                    throw new RuntimeException(StrUtil.format("Invalid IPv6 address ip1 = {%s}, ip2 = {%s}",ip1,ip2), e);
                }
            }
        });
    }


    /**
     * 验证ipv6文本是否有效 2001:288:4000:0|2001:288:7fff:ffff|34|中国|台湾|0
     * @param ipv6Text
     * @return
     */
    public static boolean validateIpv6Text(String ipv6Text) {
        if (StrUtil.isBlank(ipv6Text)) {
            return false;
        }
        try {
            Pattern pattern = Pattern.compile(Constant.ipTextRegex);
            if (pattern.matcher(ipv6Text).matches()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
