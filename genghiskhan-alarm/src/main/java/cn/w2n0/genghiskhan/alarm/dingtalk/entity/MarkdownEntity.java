package cn.w2n0.genghiskhan.alarm.dingtalk.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 无量
 * date 2021/10/22 14:26
 */
@Getter
public class MarkdownEntity implements MsgEntity{


    /**
     * 显示标题
     */
    private final String title;

    /**
     * 显示内容
     */
    private final String content;

    /**
     * 是否at所有人
     */
    private Boolean isAtAll;

    private List<String> atMobiles;

    public MarkdownEntity(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public String getMsgType() {
        return "markdown";
    }

    @Override
    public String getJSONObjectString() {
        JSONObject markdownContent = new JSONObject();
        markdownContent.put("title", this.getTitle());
        markdownContent.put("text", this.getContent());
        JSONObject atMobile = new JSONObject();
        if(this.getAtMobiles()!=null){
            List<String> mobiles = new ArrayList<>();
            for (int i=0;i<this.getAtMobiles().size();i++){
                mobiles.add(this.getAtMobiles().get(i));
            }
            if(mobiles.size()>0){
                atMobile.put("atMobiles", mobiles);
            }
            atMobile.put("isAtAll", this.getIsAtAll());
        }
        JSONObject json = new JSONObject();
        json.put("msgtype", this.getMsgType());
        json.put("markdown", markdownContent);
        json.put("at", atMobile);
        return json.toJSONString();
    }
}
