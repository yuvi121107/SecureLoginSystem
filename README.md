# Secure Login System

## 1. Project Overview

The **Secure Login System** is a Java console-based application developed as part of the CodeNova Java Development Internship.

The application provides secure user registration, login authentication, password changes, and temporary account lockout after repeated failed login attempts.

It uses password hashing and a unique salt for each user to improve password security.

## 2. Features

- **User Registration:** Register a new user with a username and password.
- **Password Validation:** Requires at least 8 characters, one number, and one symbol.
- **Secure Password Storage:** Uses PBKDF2 with HMAC-SHA256 to hash passwords.
- **Unique Salt:** Generates a random salt for each user.
- **Secure Login:** Verifies passwords against stored password hashes.
- **Account Lockout:** Temporarily locks login attempts after three consecutive failed attempts.
- **Change Password:** Allows users to change their password after verifying their current password.
- **Protected Profile:** Displays user information after successful authentication.
- **File Persistence:** Saves user information in a binary file.
- **No Plaintext Password Storage:** The repository stores password hashes and salts instead of original passwords.

## 3. Technologies Used

- **Programming Language:** Java
- **JDK:** A compatible Java Development Kit
- **IDE:** Visual Studio Code
- **Security Algorithm:** PBKDF2 with HMAC-SHA256
- **Random Salt Generation:** SecureRandom
- **Password Verification:** MessageDigest.isEqual
- **Data Storage:** Binary file storage
- **User Interface:** Console menu with a password-entry popup

## 4. Project Structure

```text
SecureLoginSystem/
├── src/
│   ├── model/
│   │   └── User.java
│   ├── security/
│   │   ├── PasswordUtil.java
│   │   └── PasswordValidator.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   │   └── AuthService.java
│   └── ui/
│       ├── SecureLoginApp.java
│       └── ProfileView.java
├── data/
│   └── users.dat
├── out/
└── README.md
```

The `out/` folder contains compiled Java class files and may be generated when the project is compiled.

## 5. Main Components

### User.java
Stores user information, including the user ID, username, password salt, password hash, and account status information.

### PasswordUtil.java
Generates a random salt, derives password hashes using PBKDF2 with HMAC-SHA256, and verifies passwords.

### PasswordValidator.java
Checks whether a password satisfies the application's password requirements.

### UserRepository.java
Saves and loads user records from a binary file. It stores the username, salt, and password hash rather than the original password.

### AuthService.java
Implements user registration, login authentication, failed-login tracking, temporary lockout, and password changes.

### SecureLoginApp.java
Provides the main menu and accepts user input for registration, login, password changes, and exiting the application.

### ProfileView.java
Displays the protected profile after successful authentication without displaying password hashes or salts.

## 6. Password Security

The application uses PBKDF2 with HMAC-SHA256 to derive a password hash.

The current configuration uses:

- **Salt size:** 16 bytes
- **Derived hash size:** 256 bits
- **Iteration count:** 600,000
- **Random number generator:** SecureRandom

Each user receives a randomly generated salt. The salt and derived hash are saved in the user repository.

The original password is not intentionally written to the user-data file. Password verification compares derived hash values using `MessageDigest.isEqual`.

**Security note:** The current account lockout tracking is stored in memory, so failed-attempt counts and lockout state may reset when the application restarts. This is a learning project and has not been independently security-audited.

## 7. Installation and Setup

### Requirements

1. Install a compatible Java Development Kit.
2. Install Visual Studio Code.
3. Open the project folder in VS Code.

### Check Java Installation

Open the VS Code terminal and run:

```powershell
java -version
javac -version
```

Both commands should display the installed Java version.

### Compile the Project

Run the following commands from the project root folder:

```powershell
cd D:\SecureLoginSystem
javac -d out src\model\*.java src\security\*.java src\repository\*.java src\service\*.java src\ui\*.java
```

If compilation succeeds, the terminal should not display any errors.

### Run the Application

```powershell
java -cp out ui.SecureLoginApp
```

## 8. Application Usage

When the application starts, the main menu appears:

```text
===== SECURE LOGIN SYSTEM =====
1. Register
2. Login
3. Change Password
0. Exit
Choose an option:
```

### Register

1. Choose option `1`.
2. Enter a username.
3. Enter a password that satisfies the password rules.
4. Confirm that registration succeeds.

### Login

1. Choose option `2`.
2. Enter your registered username.
3. Enter your password.
4. After successful authentication, the protected profile is displayed.

### Change Password

1. Choose option `3`.
2. Enter your username.
3. Enter your current password.
4. Enter a new password that satisfies the validation rules.
5. Log in using the new password to verify the change.

### Account Lockout

Repeated incorrect login attempts trigger a temporary lockout after three failed attempts. The current implementation uses a 60-second lockout period.

### Exit

Choose option `0` to close the application.

## 9. Testing

The following test cases can be used to evaluate the application.

| Test Case | Expected Result |
|---|---|
| Register with a valid password | Registration succeeds |
| Register with a password missing a symbol | Registration is rejected |
| Log in with correct credentials | Login succeeds |
| Log in with an incorrect password | Access is denied |
| Enter an incorrect password three times | Temporary lockout is triggered |
| Change password with the correct current password | Password changes successfully |
| Log in with the new password | Login succeeds |
| Restart the application | Previously saved users remain available |
| View the protected profile | User details appear without password data |

## 10. Future Enhancements

- Persist failed-login counters and lockout state across restarts.
- Add stronger username and input validation.
- Implement secure session management and logout.
- Add automated unit tests.
- Add a graphical user interface.
- Improve audit logging without recording passwords or other sensitive credentials.

## 11. Learning Outcomes

This project provides practical experience with:

- Java classes, objects, and packages.
- Object-oriented programming.
- File handling and data persistence.
- Password hashing and salt generation.
- Authentication and password validation.
- Exception handling.
- Basic application security practices.

## 12. Conclusion

The Secure Login System demonstrates how a Java application can implement user registration and authentication using password hashing, unique salts, password validation, temporary account lockout, and file persistence.

The project was developed as a learning exercise for the CodeNova Java Development Internship.

## 13. Author

**Project:** Secure Login System  
**Internship:** CodeNova Java Development Internship  
**Domain:** Java Development
