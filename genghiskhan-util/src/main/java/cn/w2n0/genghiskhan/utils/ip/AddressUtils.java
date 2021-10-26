package cn.w2n0.genghiskhan.utils.ip;


import com.alibaba.fastjson.JSONObject;
import cn.w2n0.genghiskhan.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 获取地址类
 *
 * @author 无量
 */
@Slf4j
public class AddressUtils {
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
    public static final String UNKNOWN = "XX XX";
    public static String getRealAddressByIp(String ip) {
        String address = UNKNOWN;
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }

        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", "GBK");
            if (StringUtils.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSONObject.parseObject(rspStr);
            String region = obj.getString("pro");
            String city = obj.getString("city");
            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return address;
    }
}
