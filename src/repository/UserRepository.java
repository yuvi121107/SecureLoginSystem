
package repository;

import model.User;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserRepository {

    private final Path filePath =
            Paths.get("data", "users.dat");

    private final Map<String, User> users =
            new LinkedHashMap<>();

    public UserRepository() {
        loadUsers();
    }

    public synchronized void saveUser(User user)
            throws IOException {

        String username = user.getUsername();

        if (users.containsKey(username)) {
            throw new IllegalArgumentException(
                    "Username already exists.");
        }

        users.put(username, user);

        try {
            saveAll();
        } catch (IOException e) {
            users.remove(username);
            throw e;
        }
    }

    public synchronized User findByUsername(String username) {
        return users.get(username);
    }

    public synchronized void updatePassword(
            String username,
            byte[] newSalt,
            byte[] newHash) throws IOException {

        User oldUser = users.get(username);

        if (oldUser == null) {
            throw new IllegalArgumentException(
                    "User not found.");
        }

        User updatedUser = new User(
                oldUser.getUserId(),
                oldUser.getUsername(),
                newSalt,
                newHash
        );

        users.put(username, updatedUser);

        try {
            saveAll();
        } catch (IOException e) {
            users.put(username, oldUser);
            throw e;
        }
    }

    private void saveAll() throws IOException {
        Path parent = filePath.getParent();
        Files.createDirectories(parent);

        Path temporaryFile =
                parent.resolve("users.tmp");

        try (DataOutputStream out =
                new DataOutputStream(
                    new BufferedOutputStream(
                        Files.newOutputStream(temporaryFile)))) {

            out.writeInt(users.size());

            for (User user : users.values()) {
                out.writeUTF(user.getUserId());
                out.writeUTF(user.getUsername());

                byte[] salt = user.getSalt();
                byte[] hash = user.getPasswordHash();

                out.writeInt(salt.length);
                out.write(salt);

                out.writeInt(hash.length);
                out.write(hash);
            }
        }

        try {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void loadUsers() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (DataInputStream in =
                new DataInputStream(
                    new BufferedInputStream(
                        Files.newInputStream(filePath)))) {

            int count = in.readInt();

            if (count < 0 || count > 100_000) {
                throw new IOException(
                        "Invalid user count.");
            }

            for (int i = 0; i < count; i++) {
                String id = in.readUTF();
                String username = in.readUTF();

                int saltLength = in.readInt();

                if (saltLength != 16) {
                    throw new IOException(
                            "Invalid salt length.");
                }

                byte[] salt = in.readNBytes(saltLength);

                int hashLength = in.readInt();

                if (hashLength != 32) {
                    throw new IOException(
                            "Invalid hash length.");
                }

                byte[] hash = in.readNBytes(hashLength);

                if (salt.length != saltLength ||
                        hash.length != hashLength ||
                        users.containsKey(username)) {
                    throw new IOException(
                            "Invalid user data.");
                }

                users.put(
                        username,
                        new User(id, username, salt, hash)
                );
            }

            if (in.read() != -1) {
                throw new IOException(
                        "Unexpected extra data.");
            }

        } catch (IOException e) {
            users.clear();

            throw new IllegalStateException(
                    "Could not safely load user data. " +
                    "Check data/users.dat.", e);
        }
    }
}
