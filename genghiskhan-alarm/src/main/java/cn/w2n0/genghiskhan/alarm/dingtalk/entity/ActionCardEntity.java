package cn.w2n0.genghiskhan.alarm.dingtalk.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 无量
 * date 2021/10/22 14:30
 */
@Getter
public class ActionCardEntity implements MsgEntity{

    /**
     * 显示标题
     */
    private final String title;

    /**
     * 显示内容markdown格式的消息
     */
    private final String content;

    /**
     * url
     */
    @Setter
    private String picUrl;

    /**
     * 单个按钮的方案。(设置此项和singleURL后btns无效)
     */
    private final String singleTitle;

    /**
     * 点击singleTitle按钮触发的URL
     */
    private final String singleURL;


    public ActionCardEntity(String title, String content, String singleTitle, String singleURL) {
        this.title = title;
        this.content = content;
        this.singleTitle = singleTitle;
        this.singleURL = singleURL;
    }

    public String getMsgType() {
        return "actionCard";
    }


    @Override
    public String getJSONObjectString() {
        StringBuilder stringBuilder=new StringBuilder();
        if(StringUtils.isNotEmpty(this.picUrl)) {
            stringBuilder.append("![screenshot](");
            stringBuilder.append("https://gw.alipayobjects.com/zos/skylark-tools/public/files/84111bbeba74743d2771ed4f062d1f25.png");
            stringBuilder.append(") \n");
        }
        if(StringUtils.isNotEmpty(title))
        {
            stringBuilder.append("#### ");
            stringBuilder.append(title);
            stringBuilder.append(" \n");
        }
        stringBuilder.append(this.getContent());
        JSONObject actionCardContent = new JSONObject();
        actionCardContent.put("title", this.getTitle());
        actionCardContent.put("text", stringBuilder.toString());
        actionCardContent.put("hideAvatar", "0");
        actionCardContent.put("btnOrientation", "0");
        actionCardContent.put("singleTitle", this.getSingleTitle());
        actionCardContent.put("singleURL", this.getSingleURL());
        JSONObject json = new JSONObject();
        json.put("msgtype", this.getMsgType());
        json.put("actionCard", actionCardContent);
        return json.toJSONString();
    }


}

