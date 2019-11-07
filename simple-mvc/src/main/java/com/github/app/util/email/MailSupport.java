package com.github.app.util.email;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 邮件发送支持类
 */
public class MailSupport implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(MailSupport.class);

    /**
     * 单测 模式下
     */
    public static final String LEVEL_UNIT_TEST = "UNIT_TEST";

    /**
     * DEV模式下会把所有邮件发送给{@link #devToEmails}
     */
    public static final String LEVEL_DEV = "DEV";

    /**
     * TEST模式下会把所有邮件都发送给{@link #testToEmails}
     */
    public static final String LEVEL_TEST = "TEST";

    /**
     * ONLINE模式下邮件正常发送
     */
    public static final String LEVEL_ONLINE = "ONLINE";

    /**
     * 默认级别为DEV
     */
    private String level = LEVEL_DEV;

    /**
     * 开发模式下邮件发送的邮箱地址，多个邮箱以逗号分割
     */
    private String devToEmails;

    /**
     * 测试模式下邮件发送的邮箱地址，多个邮箱以逗号分割
     */
    private String testToEmails;

    /**
     * 系统名称
     */
    private String systemName;

    private JavaMailSender mailSender;

    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (LEVEL_ONLINE.equals(level)) {
            logger.warn("使用的邮件发送级别为【线上】,邮件将直接发送给目标对象，请确认当前是否在线上环境下运行!");
        } else if (LEVEL_DEV.equals(level)) {
            Assert.hasText(devToEmails, "开发模式下开发人员邮箱地址不能为空!");
            logger.warn("使用的邮件发送级别为【开发】,邮件将发送给 <{}>，请确认当前是否在开发环境下运行!", devToEmails);
        } else if (LEVEL_TEST.equals(level)) {
            Assert.hasText(testToEmails, "测试模式下测试人员邮箱地址不能为空!");
            logger.warn("使用的邮件发送级别为【测试】,邮件将发送给测试人员 <{}>，请确认当前是否在测试环境下运行!", testToEmails);
        } else {
            logger.warn("使用的邮件发送级别为【单元测试】，邮件发送功能将屏蔽，请确认当前是否在单元测试环境下运行!");
        }
    }

    /**
     * 发送普通邮件
     *
     * @param sendTo
     * @param sendFrom
     * @param subject
     * @param emailText
     */
    public void sendEmail(String sendTo, String sendFrom, String subject, String emailText) {
        sendEmail(sendTo, sendFrom, null, subject, emailText);
    }

    public void sendEmail(String sendTo, String sendFrom, String sendCc, String subject, String emailText) {
        logger.debug("send email to {}<{}>{}", sendTo, subject, emailText);
        if (LEVEL_UNIT_TEST.equals(level)) {
            logger.warn("接收到邮件发送动作：from {} to {}：{}-[{}]，鉴于单测环境，不发送邮件，请悉知！", sendFrom, sendTo, subject, emailText);
            return;
        }
        subject = getSubjectOfLevel(subject, sendTo, sendCc);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sendFrom);
        if (level.equalsIgnoreCase(LEVEL_ONLINE)) {
            msg.setTo(sendTo.split(","));
            if (StringUtils.hasText(sendCc)) {
                msg.setCc(sendCc.split(","));
            }
        } else if (level.equalsIgnoreCase(LEVEL_DEV)) {
            String toEmails = LEVEL_DEV.equalsIgnoreCase(level) ? devToEmails : testToEmails;
            msg.setTo(toEmails.split(","));
            if (StringUtils.hasText(sendCc)) {
                msg.setCc(toEmails.split(","));
            }
        }
        msg.setSubject(subject);
        msg.setText(emailText);
        mailSender.send(msg);
        logger.debug("send email to " + sendTo + " success!");
    }

    /**
     * 发送html邮件
     *
     * @param sendTo
     * @param sendFrom
     * @param subject
     * @param htmlMsg
     *
     * @throws MessagingException
     */
    public void sendMimeEmail(String sendTo, String sendFrom, String subject, String htmlMsg) throws MessagingException {
        sendMimeEmail(sendTo, sendFrom, null, subject, htmlMsg);
    }

    public void sendMimeEmail(String sendTo, String sendFrom, String sendCc, String subject, String htmlMsg, Map<String, String> attachments) throws MessagingException, UnsupportedEncodingException {
        logger.debug("send email to {}<{}>", sendTo, subject);
        if (LEVEL_UNIT_TEST.equals(level)) {
            logger.warn("接收到邮件发送动作：from {} to {}：{}-[{}]，鉴于单测环境，不发送邮件，请悉知！", sendFrom, sendTo, subject, htmlMsg);
        } else {
            MimeMessage mimeMessage = getMimeMessage(sendFrom, sendTo, sendCc, subject, htmlMsg, attachments);
            mailSender.send(mimeMessage);
        }
        logger.debug("send email to " + sendTo + " success!");
    }

    public void sendMimeEmail(String sendTo, String sendFrom, String sendCc, String subject, String htmlMsg)
            throws MessagingException {
        logger.debug("send email to {}<{}>", sendTo, subject);
        if (LEVEL_UNIT_TEST.equals(level)) {
            logger.warn("接收到邮件发送动作：from {} to {}：{}-[{}]，鉴于单测环境，不发送邮件，请悉知！", sendFrom, sendTo, subject, htmlMsg);
        } else {
            MimeMessage mimeMessage = getMimeMessage(sendFrom, sendTo, sendCc, subject, htmlMsg, null);
            mailSender.send(mimeMessage);
        }
        logger.debug("send email to {} success!", sendTo);
    }

    public MimeMessage getMimeMessage(String sendFrom, String sendTo, String sendCc, String subject,
                                      String htmlMsg, Map<String, String> attachments) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        subject = getSubjectOfLevel(subject, sendTo, sendCc);
        if (LEVEL_ONLINE.equalsIgnoreCase(level)) {
            messageHelper.setTo(sendTo.split(","));
            if (StringUtils.hasText(sendCc)) {
                messageHelper.setCc(sendCc.split(","));
            }
        } else {
            String toEmails = LEVEL_DEV.equalsIgnoreCase(level) ? devToEmails : testToEmails;
            messageHelper.setTo(toEmails.split(","));
            if (StringUtils.hasText(sendCc)) {
                messageHelper.setCc(toEmails.split(","));
            }
        }
        if (attachments != null && !attachments.isEmpty()) {
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                File file = new File(entry.getValue());
                if (!file.exists()) {
                    logger.error("找不到文件:{},地址为:{}", entry.getKey(), entry.getValue());
                    continue;
                }
                FileSystemResource attachFile = new FileSystemResource(file);
                String fileName = null;
                try {
                    fileName = MimeUtility.encodeWord(entry.getKey());
                    messageHelper.addAttachment(fileName, attachFile);
                } catch (UnsupportedEncodingException e) {
                    logger.error("邮件附件发送，附件名转化出错：{}", entry.getKey(), e);
                }
            }
        }
        messageHelper.setFrom(sendFrom);
        messageHelper.setSubject(subject);
        messageHelper.setText(htmlMsg, true);
        return mimeMessage;
    }

    private String getSubjectOfLevel(String subject, String sendTo, String sendCc) {
        if (LEVEL_DEV.equalsIgnoreCase(level)) {
            String sendCcMsg = StringUtils.hasText(sendCc) ? "SENDCC<" + sendCc + ">-" : "";
            subject = systemName + "-DEV-SENDTO<" + sendTo + ">-" + sendCcMsg + subject;
        } else if (LEVEL_TEST.equalsIgnoreCase(level)) {
            String sendCcMsg = StringUtils.hasText(sendCc) ? "SENDCC<" + sendCc + ">-" : "";
            subject = systemName + "-TEST-SENDTO<" + sendTo + ">-" + sendCcMsg + subject;
        }
        return subject;
    }

    /**
     * 使用freemarker模板进行html邮件的发送
     *
     * @param sendTo
     * @param sendFrom
     * @param subject
     * @param data         数据
     * @param tamplatePath 模板路径
     *
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     */
    public void sendMimeEmailWithTemplate(String sendTo, String sendFrom, String subject,
                                          Map<String, Object> data, String tamplatePath)
            throws IOException, TemplateException, MessagingException {
        sendMimeEmailWithTemplate(sendTo, sendFrom, null, subject, data, tamplatePath);
    }

    public void sendMimeEmailWithTemplate(String sendTo, String sendFrom, String sendCc,
                                          String subject, Map<String, Object> data, String tamplatePath)
            throws IOException, TemplateException, MessagingException {
        String htmlText = getRenderTplHtml(tamplatePath, data);
        sendMimeEmail(sendTo, sendFrom, sendCc, subject, htmlText);
    }

    public String getRenderTplHtml(String tamplatePath, Map<String, Object> data)
            throws IOException, TemplateException {
        Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate(tamplatePath);
        return FreeMarkerTemplateUtils.processTemplateIntoString(tpl, data);
    }

    public void sendMimeEmailWithTemplate(String sendTo, String sendFrom,
                                          String sendCc, String subject, Map<String, Object> data,
                                          String tamplatePath, Map<String, String> attachments)
            throws IOException, TemplateException, MessagingException {
        String htmlText = getRenderTplHtml(tamplatePath, data);
        sendMimeEmail(sendTo, sendFrom, sendCc, subject, htmlText, attachments);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTestToEmails() {
        return testToEmails;
    }

    public void setTestToEmails(String testToEmails) {
        this.testToEmails = testToEmails;
    }

    public String getDevToEmails() {
        return devToEmails;
    }

    public void setDevToEmails(String devToEmails) {
        this.devToEmails = devToEmails;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public FreeMarkerConfigurer getFreeMarkerConfigurer() {
        return freeMarkerConfigurer;
    }

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }
}
