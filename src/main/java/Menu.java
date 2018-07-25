import java.util.Scanner;

class Menu {

  private Scanner sc = new Scanner(System.in);
  private int option;

  /**
   * This is the main menu.  Method displays all options and prompts for a choice
   *
   * @return an int with a valid option number
   */
  int mainMenu() {
    option = 0;
    while (option != 1 && option != 2) {
      System.out.println("1. Establish Connection");
      System.out.println("2. Exit");
      System.out.println("Enter an above option number");
      if (!sc.hasNextInt()) {
        System.out.println("You did not enter a valid option");
        sc.next();
        continue;
      }
      option = sc.nextInt();
      System.out.println(option);
      if (option != 1 && option != 2) {
        System.out.println("A valid option was not entered.");
      }
    }
    return option;
  }

  /**
   * This is a working menu once a connection is established.  Method displays all options and
   * prompts for a choice
   *
   * @return an int with a valid option number
   */
  int workingMenu() {
    option = 0;
    while (option < 1 || option > 10) {
      System.out.println("1.  List Directories");
      System.out.println("2.  Change local directory");
      System.out.println("3.  Put File/Files");
      System.out.println("4.  Create Directory");
      System.out.println("5.  Delete File/Directory");
      System.out.println("6.  Change permissions");
      System.out.println("7.  Copy Directory");
      System.out.println("8.  Rename File/Directory");
      System.out.println("9.  View Log History");
      System.out.println("10. Close Connection");
      System.out.println("Enter an above option number");
      if (!sc.hasNextInt()) {
        System.out.println("You did not Enter a valid option");
        sc.next();
        continue;
      }
      option = sc.nextInt();
      if (option < 1 || option > 10) {
        System.out.println("A valid option was not entered.");
      }
    }
    return option;
  }

  /**
   * This is a sub-menu under working menu item "1. List Directories" to allow the user to
   * list directories and files on the remote server or the local machine.
   *
   * @return an int with a valid option number
   */
  int displayFilesMenu() {
    option = 0;
    while (option != 1 && option != 2) {
      System.out.println("1. List directories and files on remote server");
      System.out.println("2. List directories and files on local machine");
      System.out.println("Enter an above option number");
      if (!sc.hasNextInt()) {
        System.out.println("You did not enter a valid option.");
        sc.next();
        continue;
      }
      option = sc.nextInt();
      System.out.println(option);
      if (option != 1 && option != 2) {
        System.out.println("A valid option was not entered.");
      }
    }
    return option;
  }
}