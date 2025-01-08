# ipv6Location
ipv6地址查询工具
## 简介:
    ipv6Location是一个基于java语言实现的ipv6地址查询工具，可以查询ipv6地址的地理位置信息。其查询是使用二分查询，时间复杂度为O(log n)，查询效率较高。
## 功能:
    1. 支持ipv6地址查询地域
## 环境要求:
    java 8 运行环境
## 使用方法:
    1. 下载项目源码
    2. 导入到IDE中
    3. 以下代码块为查询方式:
        SearchIpDBService searchIpDBService = new SearchIpDBService();
        IpRegion ipRegion = searchIpDBService.searchIpv6Region("240e:47f:2200:32e8::");
