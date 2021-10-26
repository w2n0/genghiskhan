package cn.w2n0.genghiskhan.alarm.mail.impl;

import cn.w2n0.genghiskhan.alarm.mail.MailSending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件发送实现类
 *
 * @author 无量
 */

public class MailSendingImpl implements MailSending {
    /**
     * Spring Boot 提供了一个发送邮件的简单抽象，使用的是下面这个接口，这里直接注入即可使用
     */
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void send(String[] to, String subject, String text) {
        //创建简单邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        //谁发的
        message.setFrom(from);
        //谁要接收
        message.setTo(to);
        //邮件标题
        message.setSubject(subject);
        //邮件内容
        message.setText(text);

        mailSender.send(message);
    }


}
