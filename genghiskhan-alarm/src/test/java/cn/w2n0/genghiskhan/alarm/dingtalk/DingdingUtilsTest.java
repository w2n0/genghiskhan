package cn.w2n0.genghiskhan.alarm.dingtalk;

import cn.w2n0.genghiskhan.alarm.dingtalk.entity.ActionCardEntity;
import cn.w2n0.genghiskhan.alarm.dingtalk.entity.LinkEntity;
import cn.w2n0.genghiskhan.alarm.dingtalk.entity.MarkdownEntity;
import cn.w2n0.genghiskhan.alarm.dingtalk.entity.TextEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 无量
 * @date 2021/10/22 11:33
 */
@DisplayName("钉钉消息发送")
class DingdingUtilsTest {

    String webHook="https://oapi.dingtalk.com/robot/send?access_token=304b736bc56bdaf6754a08eeb9814886a32ffb84588993d386ba02c7331ec8be";

    @Test
    @DisplayName("发送普通消息")
    void sendToDingding() {
        TextEntity textEntity=new TextEntity("星网，流水线写入SAP错误");

        boolean result=DingdingUtils.sendToDingding(textEntity.getJSONObjectString(),webHook);
        assertTrue(result);
    }
    @Test
    @DisplayName("发送链接消息")
    void sendLink() {
        String title="星网，流水线写入SAP错误";
        String content="错误详情:1这个即将发布的新版本，创始人陈航（花名“无招”）称它为“红树林”。\n" +
                "而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是“红树林”？";
        String url="http://www.baidu.com";
        String imgUrl="https://gw.alipayobjects.com/zos/skylark-tools/public/files/84111bbeba74743d2771ed4f062d1f25.png";
        LinkEntity linkEntity=new LinkEntity(title, content, url);
        linkEntity.setPicUrl(imgUrl);
        boolean result=DingdingUtils.sendToDingding(linkEntity.getJSONObjectString(),webHook);
        assertTrue(result);
    }
    @Test
    @DisplayName("发送Markdown接消息")
    void sendMarkdown() {
        String title="星网，流水线写入SAP错误";
        String content="#### 杭州天气 @156xxxx8827\n" +
                "> 9度，西北风1级，空气良89，相对温度73%\n\n" +
                "> ![screenshot](https://gw.alipayobjects.com/zos/skylark-tools/public/files/84111bbeba74743d2771ed4f062d1f25.png)\n"  +
                "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n";
        MarkdownEntity markdownEntity=new MarkdownEntity(title, content);
        boolean result=DingdingUtils.sendToDingding(markdownEntity.getJSONObjectString(),webHook);
        assertTrue(result);
    }
    @Test
    @DisplayName("发送action_card接消息")
    void sendCart() {
        String title="星网，流水线[ERP订单写入SAP错误]";
        String content="任务[SAP订单写入]:  \n <font color=\"red\">模型循环计划，得到的模型不可执行 </font>";
        ActionCardEntity actionCardEntity=new ActionCardEntity(title, content,  "查看详情", "http://www.xxx.com");
        boolean result=DingdingUtils.sendToDingding(actionCardEntity.getJSONObjectString(),webHook);
        assertTrue(result);
    }
}