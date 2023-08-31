package com.xxl.job.alarm.email;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @autoor 龙眼
 * @date 2019-04-10
 * @description TODO
 */
public class EmailUtils {

    public static void send(String subject, String content, List<String> targets){
        Properties prop = new Properties();
        //协议
        prop.setProperty("mail.transport.protocol", "smtp");
        //服务器
        prop.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        //端口
        prop.setProperty("mail.smtp.port", "465");
        //使用smtp身份验证
        prop.setProperty("mail.smtp.auth", "true");

        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;

        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }

        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(prop,new MyAuthenricator());

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress("system@sinoxk.com","系统邮件"));
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(new Date());
            mimeMessage.saveChanges();
            mimeMessage.setContent(content, "text/html;charset=UTF-8");
            InternetAddress[] addresses = new InternetAddress[targets.size()];
            for (int i=0;i<targets.size();i++){
                addresses[i] = new InternetAddress(targets.get(i));
            }
            Transport.send(mimeMessage,addresses);
        } catch (MessagingException var0) {
            var0.printStackTrace();
        } catch (UnsupportedEncodingException var1) {
            var1.printStackTrace();
        }
    }

    static class MyAuthenricator extends Authenticator {
        String u;
        String p;

        public MyAuthenricator() {
            this.u = "system@sinoxk.com";
            this.p = "Sinoxinkang@123";
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(u, p);
        }
    }
}
