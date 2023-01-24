package com.stock.quant.service;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class JasyptTest {

    @Autowired
    PooledPBEStringEncryptor jasyptStringEncryptor;

    @Test
    public void jasyptEncryptTest() {
        System.out.println("***** enc: " + jasyptStringEncryptor.encrypt("root"));
    }

    @Test
    public void jasyptDecryptTest() {
        System.out.println("***** dec:" + jasyptStringEncryptor.decrypt("ZE6WCSBDTfP7fjlqtF/kXg=="));
    }
}
