package edu.pdx.cs.sftp;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Gather information about a connection initiated by the user with the User class */
class User {

  /** Store the username in this variable */
  String username;
  /** Store the password in this variable */
  String password;
  /** Store the hostname in this variable */
  String hostname;
  /** Store IO scanner in this variable */
  private Scanner scanner = new Scanner(System.in);

  /** Initialize all fields to null with the default constructor */
  User() {
    username = null;
    password = null;
    hostname = null;
  }
  /**
   * Set the User fields to the current input with this constructor
   *
   * @param username sets username to current username
   * @param password sets password to current password
   * @param hostname sets hostname to current hostname
   */
  User(String username, String password, String hostname) {
    this.username = username;
    this.password = password;
    this.hostname = hostname;
  }

  /**
   * Set the scanner up for testing with this constructor
   *
   * @param scannerArg String with arguments used for testing
   */
  User(String scannerArg) {
    username = null;
    password = null;
    hostname = null;
    scanner = new Scanner(scannerArg);
  }

  /**
   * Prompt the user for a valid password
   *
   * @return The password input
   */
  String getPassword() {
    System.out.println("Enter your password:");
    password = scanner.next();
    while (password == null || password.isEmpty() || !verifyPassword(password)) {
      System.err.println("You did not enter a password. Enter your password:");
      password = scanner.next();
    }
    return password;
  }

  /**
   * Verify the string isn't empty and doesn't contain spaces
   *
   * @param password String to be assessed
   * @return true if the password isn't empty and does not include spaces Otherwise, return false
   */
  private boolean verifyPassword(String password) {
    return !password.isEmpty() && !password.contains(" ");
  }

  /**
   * Prompt the user for a valid username (Usernames must be alpha numeric of size 8-20).
   *
   * @return username input
   */
  String getUsername() {
    System.out.println("Enter your username:");
    username = scanner.next();
    // username = ""; //for testing
    if (username == null || username.isEmpty() || !verifyUsername(username)) {
      System.err.println(
          "That was not a valid username.  Please enter 8-20 alpha numeric " + "characters.");
      username = scanner.next(); // comment out for testing
    }
    return username;
  }

  /**
   * Check if the string is a valid username
   *
   * @param toVerify a string to be checked against a regular expression
   * @return <code>true</code> if the string is a valid username <code>false</code> otherwise
   */
  boolean verifyUsername(String toVerify) {
    String userNamePattern = "^(?=.{2,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(toVerify);
    return matcher.matches();
  }

  /**
   * Prompt the user for a valid host name which follows the following parameters: (1) Must be alpha
   * numeric + "." (2) Can't be longer than 255 characters (3) Host name segments cannot exceed 63
   * characters (4) Must end and start with alpha numeric characters
   *
   * @return The host name input
   */
  String getHostname() {
    System.out.println("Enter your hostname:");
    hostname = scanner.next();
    while (hostname == null || hostname.isEmpty() || !verifyHostName(hostname)) {
      System.out.println("That was not a valid Host Name.");
      System.out.println(
          "Valid host names are no longer than 255 alpha numeric characters and \n"
              + "dashes. Each segment of the host name cannot be longer than 63 characters.");
      hostname = scanner.next();
    }
    return hostname;
  }

  /**
   * Check if the string is a valid host name
   *
   * @param toVerify a string to be checked against a regular expression
   * @return <code>true</code> if the string is a valid host name <code>false</code> otherwise
   */
  boolean verifyHostName(String toVerify) {
    if (toVerify.length() > 255) return false;
    String userNamePattern =
        "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\."
            + "([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(toVerify);
    return matcher.matches();
  }
}
