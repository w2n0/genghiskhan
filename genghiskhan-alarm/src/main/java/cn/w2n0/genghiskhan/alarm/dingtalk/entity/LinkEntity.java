package cn.w2n0.genghiskhan.alarm.dingtalk.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 无量
 * date 2021/10/22 11:58
 */
@Getter
public class LinkEntity implements MsgEntity {

    /**
     * 显示标题
     */
    private final String title;
    /**
     * 显示内容
     */
    private final String content;

    /**
     * url
     */
    @Setter
    private String picUrl;

    /**
     * 内容对链接
     */
    @Setter
    private String messageUrl;

    public LinkEntity(String title, String content, String messageUrl) {
        this.title = title;
        this.content = content;
        this.messageUrl = messageUrl;
    }

    public String getMsgType() {
        return "link";
    }



    @Override
    public String getJSONObjectString() {
        // text类型
        JSONObject linkContent = new JSONObject();
        linkContent.put("title", this.getTitle());
        linkContent.put("text", this.getContent());
        linkContent.put("picUrl", this.getPicUrl());
        linkContent.put("messageUrl", this.getMessageUrl());
        JSONObject json = new JSONObject();
        json.put("msgtype", this.getMsgType());
        json.put("link", linkContent);
        return json.toJSONString();
    }

}

