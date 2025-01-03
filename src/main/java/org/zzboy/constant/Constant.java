package org.zzboy.constant;

public interface Constant {


    //2001:288:4000:0|2001:288:7fff:ffff|34|中国|台湾|0
    static final String ipTextRegex = "^([0-9a-fA-F:]{3,})([0-9a-fA-F]{1,})\\|([0-9a-fA-F:]{3,})([0-9a-fA-F]{1,})\\|(\\d+)\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)$";

    static final String ipv6Regex = "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:([0-9a-fA-F]{1,4})|([0-9a-fA-F]{1,4}:){1,5}:([0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}:([0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}:([0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}:([0-9a-fA-F]{1,4}){1,5}|([0-9a-fA-F]{1,4}:){1}:([0-9a-fA-F]{1,4}){1,6}|:((([0-9a-fA-F]{1,4}:){1,7}|:([0-9a-fA-F]{1,4}))|([0-9a-fA-F]{1,4})))$";

    static final String unknown = "0";

    static final String unknown_region = "0|0|0";

    static final String IPV6_SPLIT = ":";

    static final String IPV6_ZERO_IGNORE = "::";

    static final String IPV6_SPLIT_MAX = "ffff";

    static final String DATA_SPLIT = "|";

}
