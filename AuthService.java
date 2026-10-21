
package service;

import model.User;
import repository.UserRepository;
import security.PasswordUtil;
import security.PasswordValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_DURATION_MS = 60_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository repository;

    private final Map<String, Integer> failedAttempts =
            new ConcurrentHashMap<>();

    private final Map<String, Long> lockUntil =
            new ConcurrentHashMap<>();

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public synchronized String register(
            String username, char[] password)
            throws IOException, GeneralSecurityException {

        if (password == null) {
            return "Password is required.";
        }

        if (username == null ||
                !username.matches("[A-Za-z0-9_]{3,20}")) {
            Arrays.fill(password, '\0');
            return "Username must be 3-20 letters, numbers, or underscores.";
        }

        String key = username.toLowerCase(Locale.ROOT);

        if (repository.findByUsername(key) != null) {
            Arrays.fill(password, '\0');
            return "Username already exists.";
        }

        String passwordText = new String(password);

        if (!PasswordValidator.isValid(passwordText)) {
            String message = PasswordValidator.getMessage(passwordText);
            Arrays.fill(password, '\0');
            return message;
        }

        byte[] salt = PasswordUtil.generateSalt();
        byte[] hash = PasswordUtil.hashPassword(password, salt);

        String userId = "U" +
                Long.toUnsignedString(RANDOM.nextLong(), 36)
                        .toUpperCase(Locale.ROOT);

        User user = new User(userId, key, salt, hash);
        repository.saveUser(user);

        return "Registration successful!";
    }

    public synchronized String login(
            String username, char[] password)
            throws GeneralSecurityException {

        if (password == null) {
            return "Invalid username or password.";
        }

        if (username == null) {
            Arrays.fill(password, '\0');
            return "Invalid username or password.";
        }

        String key = username.toLowerCase(Locale.ROOT);
        long now = System.currentTimeMillis();

        Long until = lockUntil.get(key);

        if (until != null && now < until) {
            Arrays.fill(password, '\0');
            return "Account temporarily locked. Try again later.";
        }

        User user = repository.findByUsername(key);

        if (user == null) {
            Arrays.fill(password, '\0');
            return "Invalid username or password.";
        }

        boolean valid = PasswordUtil.verifyPassword(
                password, user.getSalt(), user.getPasswordHash());

        if (valid) {
            failedAttempts.remove(key);
            lockUntil.remove(key);
            return "Login successful!";
        }

        int attempts = failedAttempts.merge(key, 1, Integer::sum);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockUntil.put(key, now + LOCK_DURATION_MS);
            failedAttempts.remove(key);
            return "Too many failed attempts. Locked for 60 seconds.";
        }

        return "Invalid username or password.";
    }

    public synchronized String changePassword(
            String username,
            char[] currentPassword,
            char[] newPassword)
            throws GeneralSecurityException, IOException {

        try {
            if (username == null ||
                    currentPassword == null ||
                    newPassword == null) {
                return "All fields are required.";
            }

            String key = username.toLowerCase(Locale.ROOT);
            User user = repository.findByUsername(key);

            if (user == null) {
                return "Invalid username or password.";
            }

            if (!PasswordUtil.verifyPassword(
                    currentPassword,
                    user.getSalt(),
                    user.getPasswordHash())) {
                return "Current password is incorrect.";
            }

            String newPasswordText = new String(newPassword);

            if (!PasswordValidator.isValid(newPasswordText)) {
                return PasswordValidator.getMessage(newPasswordText);
            }

            byte[] newSalt = PasswordUtil.generateSalt();
            byte[] newHash =
                    PasswordUtil.hashPassword(newPassword, newSalt);

            repository.updatePassword(key, newSalt, newHash);

            failedAttempts.remove(key);
            lockUntil.remove(key);

            return "Password changed successfully!";

        } finally {
            if (currentPassword != null) {
                Arrays.fill(currentPassword, '\0');
            }
            if (newPassword != null) {
                Arrays.fill(newPassword, '\0');
            }
        }
    }

    public synchronized String resetPassword(
            String username,
            String recoveryCode,
            char[] newPassword)
            throws GeneralSecurityException, IOException {

        try {
            return "Password reset is not enabled yet. " +
                    "A verified recovery method must be configured.";
        } finally {
            if (newPassword != null) {
                Arrays.fill(newPassword, '\0');
            }
        }
    }
}
