
package model;

public class User {
    private final String userId;
    private final String username;
    private final byte[] salt;
    private final byte[] passwordHash;
    private int failedAttempts;
    private boolean locked;

    public User(String userId, String username,
                byte[] salt, byte[] passwordHash) {
        this.userId = userId;
        this.username = username;
        this.salt = salt.clone();
        this.passwordHash = passwordHash.clone();
        this.failedAttempts = 0;
        this.locked = false;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public byte[] getPasswordHash() {
        return passwordHash.clone();
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int attempts) {
        this.failedAttempts = attempts;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
