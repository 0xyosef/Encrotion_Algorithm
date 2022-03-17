package encryptions;

import com.company.EncryptionAlgorithm;

import java.util.Random;

public class IDEA implements EncryptionAlgorithm {
    @Override
    public String encrypt(String plaintext, String key) throws Exception {
        final Idea idea = new Idea(key,true);
        byte[] data = plaintext.getBytes();
        idea.crypt(data);
        return new String(data);
    }

    @Override
    public String decrypt(String encrypted, String key) throws Exception {
        final Idea idea = new Idea(key,false);
        byte[] data = encrypted.getBytes();
        idea.crypt(data);
        return new String(data);
    }

    @Override
    public boolean requireKey() {
        return true;
    }

    @Override
    public boolean isValidKey(String key) {
        final int minChar = 0x21;
        final int maxChar = 0x7E;
        for (char c : key.toCharArray()) {
            if (c < minChar || c > maxChar)
                return false;
        }
        return true;
    }

    @Override
    public String generateKey() {
        final int minChar = 0x21;
        final int maxChar = 0x7E;
        int n = new Random().nextInt(20) + 10;
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            b.append((char) (new Random().nextInt(maxChar - minChar) + minChar));
        }
        return b.toString();
    }

    @Override
    public String name() {
        return "IDEA (International Data Encryption Algorithm)";
    }

    @Override
    public String description() {
        return "In cryptography, the International Data Encryption Algorithm, originally called Improved Proposed Encryption Standard, is a symmetric-key block cipher designed by James Massey of ETH Zurich and Xuejia Lai and was first described in 1991. The algorithm was intended as a replacement for the Data Encryption Standard.";
    }

    // Copyright 2015 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland
// www.source-code.biz, www.inventec.ch/chdh
//
// This module is multi-licensed and may be used under the terms of any of the following licenses:
//
//  LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//  EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//
// Please contact the author if you need another license.
// This module is provided "as is", without warranties of any kind.
//
// Home page: http://www.source-code.biz/idea/java
    private static class Idea {
        /**
         * IDEA encryption module (International Data Encryption Algorithm).
         *
         * <p>See <a href="https://en.wikipedia.org/wiki/International_Data_Encryption_Algorithm">Wikipedia</a>.
         */

        private static final int rounds = 8;         // number of rounds

        private final int[] subKey;             // internal encryption sub-keys

        /**
         * Creates an instance of the IDEA processor, initialized with a 16-byte binary key.
         *
         * @param key     A 16-byte binary key.
         * @param encrypt true to encrypt, false to decrypt.
         */
        public Idea(byte[] key, boolean encrypt) {
            int[] tempSubKey = expandUserKey(key);
            if (encrypt) {
                subKey = tempSubKey;
            } else {
                subKey = invertSubKey(tempSubKey);
            }
        }

        /**
         * Creates an instance of the IDEA processor, initialized with a character string key.
         *
         * @param charKey A string of ASCII characters within the range 0x21 .. 0x7E.
         * @param encrypt true to encrypt, false to decrypt.
         */
        public Idea(String charKey, boolean encrypt) {
            this(generateUserKeyFromCharKey(charKey), encrypt);
        }

        /**
         * Encrypts or decrypts a block of 8 data bytes.
         *
         * @param data Buffer containing the 8 data bytes to be encrypted/decrypted.
         */
        public void crypt(byte[] data) {
            crypt(data, 0);
        }

        /**
         * Encrypts or decrypts a block of 8 data bytes.
         *
         * @param data    Data buffer containing the bytes to be encrypted/decrypted.
         * @param dataPos Start position of the 8 bytes within the buffer.
         */
        public void crypt(byte[] data, int dataPos) {
            int x0 = ((data[dataPos + 0] & 0xFF) << 8) | (data[dataPos + 1] & 0xFF);
            int x1 = ((data[dataPos + 2] & 0xFF) << 8) | (data[dataPos + 3] & 0xFF);
            int x2 = ((data[dataPos + 4] & 0xFF) << 8) | (data[dataPos + 5] & 0xFF);
            int x3 = ((data[dataPos + 6] & 0xFF) << 8) | (data[dataPos + 7] & 0xFF);
            //
            int p = 0;
            for (int round = 0; round < rounds; round++) {
                int y0 = mul(x0, subKey[p++]);
                int y1 = add(x1, subKey[p++]);
                int y2 = add(x2, subKey[p++]);
                int y3 = mul(x3, subKey[p++]);
                //
                int t0 = mul(y0 ^ y2, subKey[p++]);
                int t1 = add(y1 ^ y3, t0);
                int t2 = mul(t1, subKey[p++]);
                int t3 = add(t0, t2);
                //
                x0 = y0 ^ t2;
                x1 = y2 ^ t2;
                x2 = y1 ^ t3;
                x3 = y3 ^ t3;
            }
            //
            int r0 = mul(x0, subKey[p++]);
            int r1 = add(x2, subKey[p++]);
            int r2 = add(x1, subKey[p++]);
            int r3 = mul(x3, subKey[p++]);
            //
            data[dataPos + 0] = (byte) (r0 >> 8);
            data[dataPos + 1] = (byte) r0;
            data[dataPos + 2] = (byte) (r1 >> 8);
            data[dataPos + 3] = (byte) r1;
            data[dataPos + 4] = (byte) (r2 >> 8);
            data[dataPos + 5] = (byte) r2;
            data[dataPos + 6] = (byte) (r3 >> 8);
            data[dataPos + 7] = (byte) r3;
        }

//--- Static methods -----------------------------------------------------------

        // Addition in the additive group.
// The arguments and the result are within the range 0 .. 0xFFFF.
        private static int add(int a, int b) {
            return (a + b) & 0xFFFF;
        }

        // Additive Inverse.
// The argument and the result are within the range 0 .. 0xFFFF.
        private static int addInv(int x) {
            return (0x10000 - x) & 0xFFFF;
        }

        // Multiplication in the multiplicative group.
// The arguments and the result are within the range 0 .. 0xFFFF.
        private static int mul(int a, int b) {
            long r = (long) a * b;
            if (r != 0) {
                return (int) (r % 0x10001) & 0xFFFF;
            } else {
                return (1 - a - b) & 0xFFFF;
            }
        }

        // Multiplicative inverse.
// The argument and the result are within the range 0 .. 0xFFFF.
// The following condition is met for all values of x: mul(x, mulInv(x)) == 1
        private static int mulInv(int x) {
            if (x <= 1) {
                return x;
            }
            int y = 0x10001;
            int t0 = 1;
            int t1 = 0;
            while (true) {
                t1 += y / x * t0;
                y %= x;
                if (y == 1) {
                    return 0x10001 - t1;
                }
                t0 += x / y * t1;
                x %= y;
                if (x == 1) {
                    return t0;
                }
            }
        }

        // Inverts decryption/encrytion sub-keys to encrytion/decryption sub-keys.
        private static int[] invertSubKey(int[] key) {
            int[] invKey = new int[key.length];
            int p = 0;
            int i = rounds * 6;
            invKey[i + 0] = mulInv(key[p++]);
            invKey[i + 1] = addInv(key[p++]);
            invKey[i + 2] = addInv(key[p++]);
            invKey[i + 3] = mulInv(key[p++]);
            for (int r = rounds - 1; r >= 0; r--) {
                i = r * 6;
                int m = r > 0 ? 2 : 1;
                int n = r > 0 ? 1 : 2;
                invKey[i + 4] = key[p++];
                invKey[i + 5] = key[p++];
                invKey[i + 0] = mulInv(key[p++]);
                invKey[i + m] = addInv(key[p++]);
                invKey[i + n] = addInv(key[p++]);
                invKey[i + 3] = mulInv(key[p++]);
            }
            return invKey;
        }

        // Expands a 16-byte user key to the internal encryption sub-keys.
        private static int[] expandUserKey(byte[] userKey) {
            if (userKey.length != 16) {
                throw new IllegalArgumentException();
            }
            int[] key = new int[rounds * 6 + 4];
            for (int i = 0; i < userKey.length / 2; i++) {
                key[i] = ((userKey[2 * i] & 0xFF) << 8) | (userKey[2 * i + 1] & 0xFF);
            }
            for (int i = userKey.length / 2; i < key.length; i++) {
                key[i] = ((key[(i + 1) % 8 != 0 ? i - 7 : i - 15] << 9) | (key[(i + 2) % 8 < 2 ? i - 14 : i - 6] >> 7)) & 0xFFFF;
            }
            return key;
        }

        // Generates a 16-byte binary user key from a character string key.
// The characters within the string must be within the range 0x21 .. 0x7E.
        private static byte[] generateUserKeyFromCharKey(String charKey) {
            final int minChar = 0x21;
            final int maxChar = 0x7E;
            final int nofChar = maxChar - minChar + 1;    // Number of different valid characters
            int[] a = new int[8];
            for (int p = 0; p < charKey.length(); p++) {
                int c = charKey.charAt(p);
                if (c < minChar || c > maxChar) {
                    throw new IllegalArgumentException("Wrong character in key string.");
                }
                int val = c - minChar;
                for (int i = a.length - 1; i >= 0; i--) {
                    val += a[i] * nofChar;
                    a[i] = val & 0xFFFF;
                    val >>= 16;
                }
            }
            byte[] key = new byte[16];
            for (int i = 0; i < 8; i++) {
                key[i * 2] = (byte) (a[i] >> 8);
                key[i * 2 + 1] = (byte) a[i];
            }
            return key;
        }

    }

}
