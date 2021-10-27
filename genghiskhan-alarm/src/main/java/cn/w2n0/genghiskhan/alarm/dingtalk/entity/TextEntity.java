package cn.w2n0.genghiskhan.alarm.dingtalk.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 无量
 * date 2021/10/22 11:52
 */
@Getter
public class TextEntity implements MsgEntity{

    /**
     * 显示内容
     */
    private final String content;

    /**
     * 是否at所有人
     */
    private Boolean isAtAll;

    /**
     * 被@人的手机号(在content里添加@人的手机号)
     */
    private List<String> atMobiles;

    public TextEntity(String content) {
        this.content = content;
    }


    public String getMsgType() {
        return "text";
    }

    @Override
    public String getJSONObjectString() {
        JSONObject content = new JSONObject();
        content.put("content", this.getContent());
        JSONObject atMobile = new JSONObject();
        if(this.getAtMobiles()!=null &&this.getAtMobiles().size() > 0){
            List<String> mobiles = new ArrayList<String>();
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
        json.put("text", content);
        json.put("at", atMobile);
        return json.toJSONString();
    }

}

