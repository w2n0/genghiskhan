package cn.w2n0.genghiskhan.alarm;

import cn.w2n0.genghiskhan.alarm.dingtalk.DingdingUtils;
import cn.w2n0.genghiskhan.alarm.dingtalk.entity.MsgEntity;
import cn.w2n0.genghiskhan.alarm.mail.MailSending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 报警服务
 *
 * @author 无量
 * @date 2021/10/20 14:24
 */
public class AlarmSendingTemplate {

    /**
     * 邮件服务
     */
    @Autowired
    private MailSending mailSending;

    private ExecutorService executorService;

    public AlarmSendingTemplate() {
        ThreadFactory springThreadFactory = new CustomizableThreadFactory("springThread-pool-Alarm-");
        executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), springThreadFactory);
    }

    /**
     * 同步发送消息
     *
     * @param subject
     * @param text
     */
    public void syncsendMail(String[] tos, String subject, String text) {
        mailSending.send(tos, subject, text);
    }

    /**
     * 异步发送消息
     *
     * @param subject
     * @param text
     */
    public void asyncSendMail(String[] tos, String subject, String text) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                mailSending.send(tos, subject, text);
            }
        });
    }

    /**
     * 发送钉钉消息
     * @param msgEntity
     * @param webHook
     */
    public void sendDingTalk(MsgEntity msgEntity, String webHook) {
        DingdingUtils.sendToDingding(msgEntity.getJSONObjectString(), webHook);
    }

    /**
     * 异步发送钉钉消息
     * @param msgEntity
     * @param webHook
     */
    public void asyncSendDingTalk(MsgEntity msgEntity, String webHook) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                DingdingUtils.sendToDingding(msgEntity.getJSONObjectString(), webHook);            }
        });
    }
}
