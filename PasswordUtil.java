
package security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 600_000;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] hashPassword(char[] password, byte[] salt)
            throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, ITERATIONS, HASH_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
            Arrays.fill(password, '\0');
        }
    }

    public static boolean verifyPassword(
            char[] password, byte[] salt, byte[] expectedHash)
            throws GeneralSecurityException {

        byte[] actualHash = hashPassword(password, salt);

        try {
            return java.security.MessageDigest.isEqual(
                    actualHash, expectedHash);
        } finally {
            Arrays.fill(actualHash, (byte) 0);
        }
    }
}
