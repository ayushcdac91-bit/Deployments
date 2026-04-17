package com.statewide.login.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Component;

@Component
public class SMSHttpPostClient {

    public static String sendMobileOtpSMS(String username, String password, String message,
            String senderId, String mobileNumber,
            String secureKey, String templateId) {

        String url = "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT";
        String responseString = "";

        try {
            // Trust all certificates (for self-signed / testing)
            TrustStrategy trustAllStrategy = (chain, authType) -> true;

            SSLContext sslContext = SSLContextBuilder.create()
                    .setProtocol("TLSv1.2")
                    .loadTrustMaterial(null, trustAllStrategy)
                    .build();

            try (CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build()) {

                HttpPost post = new HttpPost(url);

                // Encrypt password
                String encryptedPassword = MD5(password);

                // Generate hash key
                String hashKey = hashGenerator(username, senderId, message, secureKey);

                // Set POST parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("mobileno", mobileNumber));
                params.add(new BasicNameValuePair("senderid", senderId));
                params.add(new BasicNameValuePair("content", message));
                params.add(new BasicNameValuePair("smsservicetype", "singlemsg"));
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", encryptedPassword));
                params.add(new BasicNameValuePair("key", hashKey));
                params.add(new BasicNameValuePair("templateid", templateId));

                post.setEntity(new UrlEncodedFormEntity(params));

                try (CloseableHttpResponse response = client.execute(post)) {
                    responseString = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                            .lines()
                            .collect(Collectors.joining());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseString;
    }

    private static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] md5 = new byte[64];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5 = md.digest();
        return convertedToHex(md5);
    }

    private static String convertedToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            int halfOfByte = (data[i] >>> 4) & 0x0F;
            int twoHalfBytes = 0;

            do {
                if ((0 <= halfOfByte) && (halfOfByte <= 9)) {
                    buf.append((char) ('0' + halfOfByte));
                }

                else {
                    buf.append((char) ('a' + (halfOfByte - 10)));
                }

                halfOfByte = data[i] & 0x0F;

            } while (twoHalfBytes++ < 1);
        }
        return buf.toString();
    }

    protected static String hashGenerator(String userName, String senderId, String content, String secureKey) {

        StringBuffer finalString = new StringBuffer();
        finalString.append(userName.trim()).append(senderId.trim()).append(content.trim()).append(secureKey.trim());
        // logger.info("Parameters for SHA-512 : "+finalString);
        String hashGen = finalString.toString();
        StringBuffer sb = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(hashGen.getBytes());
            byte byteData[] = md.digest();
            // convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return sb.toString();
    }

}
