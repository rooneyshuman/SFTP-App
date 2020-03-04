package edu.pdx.cs.sftp;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.in;

/**
 * Gathers information about a connection attempt initiated by a user, verifies
 * user input against a set of input rules, and returns valid credentials.
 */
class User {
  String username;
  String password;
  String hostname;
  private Scanner scanner = new Scanner(in);

  /** Class constructor initializing all fields to null. */
  User() {
    username = null;
    password = null;
    hostname = null;
  }
  
  /**
   * Class constructor setting all fields to the current input.
   *
   * @param username sets username to current username.
   * @param password sets password to current password.
   * @param hostname sets hostname to current hostname.
   */
  User(String username, String password, String hostname) {
    this.username = username;
    this.password = password;
    this.hostname = hostname;
  }

  /**
   * Class constructor setting scanner up for testing.
   *
   * @param scannerArg String with arguments used for testing.
   */
  User(String scannerArg) {
    username = null;
    password = null;
    hostname = null;
    scanner = new Scanner(scannerArg);
  }

  /**
   * Prompt the user for a valid password.
   * Password must not be empty or include spaces.
   *
   * @return password input.
   */
  String getPassword() {
    out.println("Enter your password:");
    password = scanner.next();
    while (password == null || password.isEmpty() || !verifyPassword(password)) {
      err.println("You did not enter a password.\n"
                + "Please, enter your password:");
      password = scanner.next();
    }
    return password;
  }

  /**
   * Verify the password isn't empty and doesn't contain spaces.
   *
   * @param passwordToVerify is the string to be assessed.
   * @return <code>true</code> if the string is a valid password <code>false</code> otherwise.
   */
  private boolean verifyPassword(String passwordToVerify) {
    return !passwordToVerify.isEmpty() && !passwordToVerify.contains(" ");
  }

  /**
   * Prompt the user for a valid username.
   * Username must be alphanumeric of size 8-20.
   *
   * @return username input.
   */
  String getUsername() {
    out.println("Enter your username:");
    username = scanner.next();
    if (username == null || username.isEmpty() || !verifyUsername(username)) {
      err.println( "The username is invalid.\n"
                 + "Please enter 8-20 alphanumeric characters.");
      username = scanner.next();
    }
    return username;
  }

  /**
   * Verify the username is 8-20 alphanumeric characters.
   *
   * @param usernameToVerify is the string to be checked against a regular expression.
   * @return <code>true</code> if the string is a valid username <code>false</code> otherwise.
   */
  boolean verifyUsername(String usernameToVerify) {
    String userNamePattern = "^(?=.{2,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(usernameToVerify);
    return matcher.matches();
  }

  /**
   * Prompt the user for a valid host name, which follows the below parameters:
   * (1) Must be alphanumeric
   * (2) Can't be longer than 255 characters
   * (3) Host name segments cannot exceed 63 characters
   * (4) Must end and start with alphanumeric characters
   *
   * @return host name input.
   */
  String getHostname() {
    out.println("Enter your hostname:");
    hostname = scanner.next();
    while (hostname == null || hostname.isEmpty() || !verifyHostName(hostname)) {
      out.println("The host name is invalid.\n"
                + "Valid host names are no longer than 255 alphanumeric characters and dashes.\n"
                + "Each segment of the host name cannot be longer than 63 characters.");
      hostname = scanner.next();
    }
    return hostname;
  }

  /**
   * Verify the host name is alphanumeric, shorter than 255 characters, with name segments not
   * exceeding 63 characters, and starts and ends with alphanumeric characters.
   *
   * @param hostnameToVerify is a string to be checked against a regular expression.
   * @return <code>true</code> if the string is a valid host name <code>false</code> otherwise.
   */
  boolean verifyHostName(String hostnameToVerify) {
    if (hostnameToVerify.length() > 255) return false;
    String userNamePattern =
        "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\."
            + "([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(hostnameToVerify);
    return matcher.matches();
  }
}
