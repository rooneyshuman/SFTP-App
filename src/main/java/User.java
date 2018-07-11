import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

  String password;
  String userName;
  String hostName;
  Scanner scanner = new Scanner(System.in);
  String error = "Error";

  public User() {
    password = null;
    hostName = null;
    userName = null;
  }

  public boolean promptPassword() {
    System.out.println("Enter your password:");
    password = scanner.next();
    while (password == null || password.isEmpty()) {
      System.out.println("You did not enter a password. Enter your password:");
      password = scanner.next();
    }
    return true;
  }

  public String getPassword() {
    if (promptPassword()) {
      return password;
    } else {
      return error;
    }
  }

  public boolean promptUsername() {
    System.out.println("Enter your username:");
    userName = scanner.next();
    while (userName == null || userName.isEmpty() || !verifyUsername(userName)) {
      System.out.println("That was not a valid username.  Please enter 8-20 alpha numeric " +
          "characters.");
      userName = scanner.next();
    }
    return true;
  }

  public boolean verifyUsername(String toVerify) {
    String userNamePattern = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(toVerify);
    return matcher.matches();
  }

  public String getUsername() {
    if (promptUsername()) {
      return userName;
    } else {
      return error;
    }
  }

  public boolean promptHostname() {
    System.out.println("Enter your hostname:");
    hostName = scanner.next();
    if (hostName == null || hostName.isEmpty()) {
      return false;
    }
    return true;
  }

  public String getHostname() {
    if (promptHostname()) {
      return hostName;
    } else {
      return error;
    }
  }
}

