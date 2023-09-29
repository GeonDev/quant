package com.quant.core.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.Reader;

@Configuration
public class JasyptConfig {

    @Value("${signkey.pass}")
    private String signkey;

    private static final String JASYPT_ALGORITHM = "PBEWithMD5AndDES";

    @Bean
    public PooledPBEStringEncryptor jasyptStringEncryptor() {

        try {
            JSONParser keyParser = new JSONParser();
            Reader reader = new FileReader(signkey);
            JSONObject jsonObject = (JSONObject) keyParser.parse(reader);

            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword((String) jsonObject.get("JASYPT_PASSWORD"));
            config.setAlgorithm(JASYPT_ALGORITHM);
            config.setKeyObtentionIterations(1000);
            config.setPoolSize(1);
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setStringOutputType("base64");
            encryptor.setConfig(config);
            return encryptor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
