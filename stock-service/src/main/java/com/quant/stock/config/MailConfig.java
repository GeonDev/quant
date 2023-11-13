package com.quant.stock.config;

import com.quant.core.exception.NoFoundException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${signkey.path}")
    String signKey;

    @Bean
    public JavaMailSender javaMailService() {

        try {
            JSONParser keyParser = new JSONParser();
            Reader reader = new FileReader(signKey);
            JSONObject jsonObject = (JSONObject) keyParser.parse(reader);


            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

            javaMailSender.setHost("smtp.gmail.com");
            javaMailSender.setUsername("geons.dev@gmail.com");
            javaMailSender.setPassword((String) jsonObject.get("mail-password"));

            javaMailSender.setPort(465);

            javaMailSender.setJavaMailProperties(getMailProperties());

            return javaMailSender;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust","smtp.gmail.com");
        properties.setProperty("mail.smtp.ssl.enable","true");
        return properties;
    }
}