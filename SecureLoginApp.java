
package ui;

import model.User;
import repository.UserRepository;
import service.AuthService;

import java.io.Console;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

public class SecureLoginApp {

    public static void main(String[] args) {
        UserRepository repository = new UserRepository();
        AuthService authService = new AuthService(repository);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== SECURE LOGIN SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Change Password");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        register(scanner, authService);
                        break;

                    case "2":
                        login(scanner, authService, repository);
                        break;

                    case "3":
                        changePassword(scanner, authService);
                        break;

                    case "0":
                        System.out.println(
                                "Thank you for using the system.");
                        return;

                    default:
                        System.out.println(
                                "Invalid option. Try again.");
                }
            } catch (IOException | GeneralSecurityException e) {
                System.out.println(
                        "Operation failed safely. Please try again.");
            }
        }
    }

    private static void register(
            Scanner scanner,
            AuthService authService)
            throws IOException, GeneralSecurityException {

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        char[] password = readPassword("Create password: ");

        try {
            System.out.println(
                    authService.register(username, password));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private static void login(
            Scanner scanner,
            AuthService authService,
            UserRepository repository)
            throws GeneralSecurityException {

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        char[] password = readPassword("Enter password: ");

        try {
            String result = authService.login(username, password);
            System.out.println(result);

            if ("Login successful!".equals(result)) {
                User user = repository.findByUsername(
                        username.toLowerCase(
                                java.util.Locale.ROOT));

                ProfileView.display(user);
            }
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private static void changePassword(
            Scanner scanner,
            AuthService authService)
            throws IOException, GeneralSecurityException {

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        char[] currentPassword =
                readPassword("Enter current password: ");

        char[] newPassword =
                readPassword("Enter new password: ");

        try {
            String result = authService.changePassword(
                    username, currentPassword, newPassword);

            System.out.println(result);
        } finally {
            Arrays.fill(currentPassword, '\0');
            Arrays.fill(newPassword, '\0');
        }
    }

   

private static char[] readPassword(String prompt) {
    JPasswordField passwordField = new JPasswordField(25);
    passwordField.setEchoChar('*');

    int result = JOptionPane.showConfirmDialog(
            null,
            passwordField,
            prompt + "\n(Type your password below)",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result == JOptionPane.OK_OPTION) {
        return passwordField.getPassword();
    }

    return new char[0];
}
}