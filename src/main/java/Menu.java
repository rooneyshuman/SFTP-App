import java.util.Scanner;

class Menu {

  private Scanner sc;

  Menu() {
    sc = new Scanner(System.in);
  }

  Menu(String scannerInput) {
    sc = new Scanner(scannerInput);
  }

  /**
   * Compartmentalizes the scanner for unit testing
   *
   * @return the number entered or zero if non numerical
   */
  private int intCheck() {
    int opt = 0;
    if (!sc.hasNextInt()) {
      sc.next();
      return opt;
    }
    opt = sc.nextInt();
    return opt;
  }

  /**
   * This is the main menu.  Method displays all options and prompts for a choice
   *
   * @return zero if non number entered, else the number entered
   */
  int mainMenu() {
    System.out.println("1. Establish Connection");
    System.out.println("2. Exit");
    System.out.println("Enter an above option number");

    return intCheck();
  }

  /**
   * This is a working menu once a connection is established.  Method displays all options and
   * prompts for a choice
   *
   * @return zero if non number entered, else the number entered
   */
  int workingMenu() {
    System.out.println("1.  List Directories");
    System.out.println("2.  Get File/Files");
    System.out.println("3.  Put File/Files");
    System.out.println("4.  Create Directory");
    System.out.println("5.  Delete File/Directory");
    System.out.println("6.  Change permissions");
    System.out.println("7.  Copy Directory");
    System.out.println("8.  Rename File/Directory");
    System.out.println("9.  View Log History");
    System.out.println("10. Close Connection");
    System.out.println("Enter an above option number");

    return intCheck();
  }

  /**
   * Menu used for menu options that may be local or remote.  Method displays all options and
   * prompts for a choice
   *
   * @return zero if non number entered, else the number entered
   */
  int localOrRemoteMenu(String operation) {
    System.out.println("1. View current local directory");
    System.out.println("2. View current remote directory");
    System.out.println("3. Change local directory");
    System.out.println("4. Change remote directory");
    System.out.println("5. " + operation + " local");
    System.out.println("6. " + operation + " remote");
    System.out.println("7. Back to main menu");
    System.out.println("Enter an above option number");

    return intCheck();
  }

}