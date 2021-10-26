package cn.w2n0.genghiskhan.alarm.mail;


/**
 * 邮件发送
 * @author 无量
 */
public interface MailSending {
    /**
     * 发送邮件
     * @param to 接收方
     * @param subject 主题
     * @param text 内容
     */
     void send(String[] to, String subject, String text);

}
