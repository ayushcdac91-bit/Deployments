package com.statewide.login.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginFeatureUtil {
    private final JavaMailSender mailSender;

    // Constructor injection ensures Spring provides the bean
    public LoginFeatureUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public static final String sms_username = "hpgovt-NHMHP";
    public static final String sms_password = "@smsgateway123";
    public static final String sms_senderId = "HPGOVT";
    public static final String secureKey = "6c3abab3-6e09-4216-8f73-d40613e6e01b";
    public static final String patient_login_otp_template_id = "1107170186110899974";
    final static String strFromEmail = "projectinchargehmis@gmail.com"; // email password "password@321#"
    final static String strPassword = "musuwhprdptxvsoj";

    public String sendMobileOtpForForgotPassword(String varUserName, String varMobileNumber) {
        String response = "";
        String res = "";
        try {

            // objActionSupport.clear();
            // objSession = objRequest.getSession();
            // final String mobilenumber = objRequest.getParameter("patMobileNo");
            System.out.println("varMobileNumber:" + varMobileNumber);
            String OTP = "";
            OTP = RandomStringUtils.random(6, "123456789");
            log.info("OTP -----" + OTP);
            // objSession.setAttribute("User_OTP", OTP);

            String msg = "Your OTP for login to HMIS Swasthya App is " + OTP + ". HPGOVT"; // GOOD
            // String msg = "Your OTP for Forgot Password is " + OTP + ". HPGOVT";
            String content = msg;
            System.out.println(content);
            response = SMSHttpPostClient.sendMobileOtpSMS(sms_username, sms_password, content, sms_senderId,
                    String.valueOf(varMobileNumber), secureKey, patient_login_otp_template_id);

            res = OTP;
            System.out.println("response is :" + response);
            // writeResponse1(objResponse, "OK");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

    public static void writeResponse1(HttpServletResponse resp, String output) {
        try {
            resp.reset();
            resp.flushBuffer();
            resp.setContentType("application/json");
            resp.setHeader("Cache-Control", "no-cache");
            resp.getWriter().write(output);
        } catch (Exception var3) {
            System.out.println(var3);
        }

    }

    public String sendEmailForgotPassword(String varUserName, String varEmailId) {
        String res = "";

        try {

            // Generate 6-digit OTP
            String OTP = RandomStringUtils.random(6, "123456789");
            System.out.println("Generated OTP: " + OTP);

            // Build email content
            String subject = "Your OTP for Password Reset";
            String messageBody = "Dear User,\n\n" +
                    "Your OTP for forgot your password on the HMIS HP is: " + OTP + "\n\n" +
                    "This OTP is valid for the next 5 minutes.\n\n" +
                    "Regards,\n" +
                    "HPGOVT";

            // Send email using JavaMail or your EmailService
            // If you have a service, replace this block accordingly

            // Properties props = new Properties();
            // props.put("mail.smtp.host", "smtp.gmail.com");
            // props.put("mail.smtp.port", "587");
            // props.put("mail.smtp.auth", "true");
            // props.put("mail.smtp.starttls.enable", "true");
            // props.put("mail.smtp.starttls.required", "true");
            // Use this only in test environments, since it bypasses certificate checks.

            // props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            // props.put("mail.debug", "true");

            // Authenticate and create session
            // Session session = Session.getInstance(props, new Authenticator() {
            // protected PasswordAuthentication getPasswordAuthentication() {
            // return new PasswordAuthentication(strFromEmail, strPassword);
            // }
            // });
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(varEmailId);
            message.setSubject(subject);
            message.setText(messageBody);

            mailSender.send(message);

            // Message message = new MimeMessage();
            // message.setFrom(new InternetAddress(strFromEmail));
            // message.setRecipients(Message.RecipientType.TO,
            // InternetAddress.parse(emailId));
            // message.setSubject(subject);
            // message.setText(messageBody);

            // Transport.send(message);
            System.out.println("OTP email sent successfully to " + varEmailId);

            // res = "OK";
            res = OTP;
            // writeResponse1(objResponse, "OK");

        } catch (Exception e) {
            e.printStackTrace();
            // writeResponse1(objResponse, "FAIL");
            res = "FAIL";
        }

        return res;
    }

}
