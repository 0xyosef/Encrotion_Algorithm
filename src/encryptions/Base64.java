package encryptions;

import com.company.EncryptionAlgorithm;

public class Base64 implements EncryptionAlgorithm {

    @Override
    public String encrypt(String plaintext, String key) {
        return java.util.Base64.getEncoder().encodeToString(plaintext.getBytes());
    }

    @Override
    public String decrypt(String encrypted, String key) {
        return new String(java.util.Base64.getDecoder().decode(encrypted.getBytes()));
    }

    @Override
    public boolean requireKey() {
        return false;
    }

    @Override
    public boolean isValidKey(String key) {
        return false;
    }

    @Override
    public String generateKey() {
        return null;
    }

    @Override
    public String name() {
        return "Base64";
    }

    @Override
    public String description() {
        return "Don't need a key";
    }
}
