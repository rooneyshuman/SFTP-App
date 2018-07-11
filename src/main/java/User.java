import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

  private String password;
  private String username;
  private String hostname;
  private Scanner scanner = new Scanner(System.in);

  /**
   * Default constructor initializes all fields to null
   */
  public User() {
    password = null;
    hostname = null;
    username = null;
  }

  /**
   * P
   * @return
   */
  public String getPassword() {
    System.out.println("Enter your password:");
    password = scanner.next();
    while (password == null || password.isEmpty()) {
      System.out.println("You did not enter a password. Enter your password:");
      password = scanner.next();
    }
    return password;
  }

  public String getUsername() {
    System.out.println("Enter your username:");
    username = scanner.next();
    while (username == null || username.isEmpty() || !verifyUsername(username)) {
      System.out.println("That was not a valid username.  Please enter 8-20 alpha numeric " +
          "characters.");
      username = scanner.next();
    }
    return username;
  }

  public boolean verifyUsername(String toVerify) {
    String userNamePattern = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(toVerify);
    return matcher.matches();
  }

  public String getHostname() {
    System.out.println("Enter your hostname:");
    hostname = scanner.next();
    while (hostname == null || hostname.isEmpty() || !verifyHostName(hostname)) {
      System.out.println("That was not a valid Host Name.");
      System.out.println("Valid host names are no longer than 255 alpha numeric characters and \n" +
          "dashes. Each segment of the host name cannot be longer than 63 characters.");
      username = scanner.next();
    }
    return hostname;
  }

  public boolean verifyHostName(String toVerify) {
    if (toVerify.length() > 255)
      return false;
    String userNamePattern = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\." +
        "([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
    Pattern pattern = Pattern.compile(userNamePattern);
    Matcher matcher = pattern.matcher(toVerify);
    return matcher.matches();
  }

}