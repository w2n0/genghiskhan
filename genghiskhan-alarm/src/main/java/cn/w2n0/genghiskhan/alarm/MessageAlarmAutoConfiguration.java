package cn.w2n0.genghiskhan.alarm;

import cn.w2n0.genghiskhan.alarm.mail.MailSending;
import cn.w2n0.genghiskhan.alarm.mail.impl.MailSendingImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息报警配置
 * @author 无量
 * date 2021/10/8 19:21
 */
@Configuration
public class MessageAlarmAutoConfiguration {
    @Bean
    public AlarmSendingTemplate getAlarm() {
        return new AlarmSendingTemplate();
    }

    @Bean
    public MailSending getMailSending(){
        return new MailSendingImpl();
    }

}
