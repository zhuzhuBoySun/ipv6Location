package org.zzboy.constant;

public enum Province {

    HEBEI("河北", 130000,1),
    SHANXI("山西", 140000, 1),
    INNER_MONGOLIA("内蒙古",150000, 1),
    LIAONING("辽宁", 210000, 1),
    JILIN("吉林", 220000, 1),
    HEILONGJIANG("黑龙江", 230000,1),
    ZHEJIANG("浙江", 330000,1),
    ANHUI("安徽", 340000,1),
    FUJIAN("福建", 350000,1),
    JIANGSU("江苏", 320000, 1),
    SHANDONG("山东", 370000,1),
    HENAN("河南", 410000,1),
    HUBEI("湖北", 420000,1),
    HUNAN("湖南", 430000,1),
    GUANGDONG("广东", 440000,1),
    GUANGXI("广西", 450000,1),
    HAINAN("海南", 460000,1),
    SICHUAN("四川", 510000,1),
    GUIZHOU("贵州",520000,1),
    YUNNAN("云南", 530000,1),
    XIZANG("西藏", 540000,1),
    SHANXI_("陕西", 610000,1),
    GANSU("甘肃", 620000,1),
    QINGHAI("青海", 630000,1),
    XINJIANG("新疆", 650000,1),
    NINGXIA("宁夏", 640000,1),
    JIANGXI("江西", 360000,1),
    CHONGQING("重庆", 500000,0),
    SHANGHAI("上海", 310000,0),
    BEIJING("北京", 110000,0),
    TIANJIN("天津",120000, 0),
    TAIWAN("台湾", 710000,0),
    HONGKONG("香港", 810000,0),
    MACAO("澳门", 820000,0);

    private final String fullName;

    private final int code;

    private final int type;

    Province(String fullName,int code, int type) {
        this.fullName = fullName;
        this.code = code;
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public int getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public static Province getProvinceName(String fullName) {
        for (Province province : Province.values()) {
            if (fullName.contains(province.getFullName())) {
                return province;
            }
        }
        return null;
    }
}
