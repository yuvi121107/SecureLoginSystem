
package security;

public class PasswordValidator {

    public static boolean isValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasNumber = false;
        boolean hasSymbol = false;

        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(ch)) {
                hasSymbol = true;
            }
        }

        return hasNumber && hasSymbol;
    }

    public static String getMessage(String password) {
        if (password == null || password.length() < 8) {
            return "Password must contain at least 8 characters.";
        }

        if (!hasNumber(password)) {
            return "Password must contain at least one number.";
        }

        if (!hasSymbol(password)) {
            return "Password must contain at least one symbol.";
        }

        return "Password is valid.";
    }

    private static boolean hasNumber(String password) {
        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSymbol(String password) {
        for (char ch : password.toCharArray()) {
            if (!Character.isLetterOrDigit(ch)) {
                return true;
            }
        }
        return false;
    }
}
