package com.statewide.login.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordRedisOtp {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long OTP_TTL = 5; //

    public void storeOtp(String varMobileNumber, String otp) {

        redisTemplate.opsForValue().set(
                "OTP_MOBILE" + varMobileNumber,
                otp,
                OTP_TTL,
                TimeUnit.MINUTES);
    }

    public void storeOtpEmail(String varEmailId, String otp) {

        redisTemplate.opsForValue().set(
                "OTP_EMAIL" + varEmailId,
                otp,
                OTP_TTL,
                TimeUnit.MINUTES);
    }

}
