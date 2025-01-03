package org.zzboy.search.domain;


import cn.hutool.core.date.DateUtil;
import org.junit.Test;

import java.io.IOException;

public class Ipv6MakeTest {

    @Test
    public void createDB() throws IOException {
        String yyyyMMdd = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        Ipv6Make.createDB("\\data\\ipHome\\"+yyyyMMdd+"\\ipv6_m3_split_new.txt");
    }
}