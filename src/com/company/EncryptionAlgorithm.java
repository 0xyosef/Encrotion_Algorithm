package com.company;

public interface EncryptionAlgorithm {
    String encrypt(String plaintext,String key) throws Exception;
    String decrypt(String encrypted,String key) throws Exception;
    boolean requireKey();
    boolean isValidKey(String key);
    String generateKey();

    String name();
    String description();
}
