
package ui;

import model.User;

public class ProfileView {

    public static void display(User user) {
        if (user == null) {
            System.out.println("Access denied. Please log in first.");
            return;
        }

        System.out.println("\n===== PROTECTED USER PROFILE =====");
        System.out.println("User ID  : " + user.getUserId());
        System.out.println("Username : " + user.getUsername());
        System.out.println("Status   : Authenticated");
        System.out.println("==================================");
    }
}
