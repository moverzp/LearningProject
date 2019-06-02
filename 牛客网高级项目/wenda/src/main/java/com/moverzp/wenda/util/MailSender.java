package com.moverzp.wenda.util;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    FreeMarkerConfigurer configurer;

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("moverzp@qq.com"); //QQ邮箱账号
        mailSender.setPassword("xxxxxxxxxxxxxxxx"); //QQ邮箱16位授权码，注意不要上传到GitHub里
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        //javaMailProperties.put("mail.smtp.auth", true);
        //javaMailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String templateName, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("鹏鹏哥"); //发件人昵称
            InternetAddress from = new InternetAddress(nick + "<moverzp@qq.com>"); //发件人邮箱
            MimeMessage mimeMessage = mailSender.createMimeMessage(); //创建邮件正文对象
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage); //创建helper对象，用来设置正文

//            String result = VelocityEngineUtils
//                    .mergeTemplateIntoString(velocityEngine, templateName, "UTF-8", model); //使用Velocity渲染模板
            Template template = configurer.getConfiguration().getTemplate(templateName);
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model); //FreeMarker渲染模板

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);//设置邮件正文
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
}
