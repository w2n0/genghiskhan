package cn.w2n0.genghiskhan.alarm.dingtalk;


import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author 无量
 * date 2021/10/21 18:03
 */
public class DingdingUtils {

    private static RestTemplate restTemplate;
    static{
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
         restTemplate = new RestTemplate(factory);
    }
    /**
     * 发送钉钉消息
     * @param jsonString 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return 发送成功:true
     */
    public static boolean sendToDingding(String jsonString, String webhook) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try{
            HttpEntity<String> request = new HttpEntity<>(jsonString, headers);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(webhook);
            URI uri = uriComponentsBuilder.build().encode().toUri();
            ResponseEntity<String> responseEntity=restTemplate.postForEntity(uri, request, String.class);
            String body = responseEntity.getBody();
            JSONObject res = JSONObject.parseObject(body);
            return res.getIntValue("errcode") == 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
