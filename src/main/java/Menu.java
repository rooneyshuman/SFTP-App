import java.util.Scanner;

public class Menu {
    Scanner sc = new Scanner(System.in);
    int option;

    public int mainMenu() {
        System.out.println("1. Establish Connection");
        System.out.println("2. Exit");
        System.out.println("Enter an above option number");
        //catch the option as string, verify string contains 1 or 2, convert to int and return
        option = sc.nextInt();
        return option;
    }

    /**
     * This is a working menu once a connection is established.  Method displays all options, prompts
     * for a choice and returns that choice as an integer.
     *
     * @return an int with a valid option number
     */
    public int workingMenu() {
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
        //catch the option as string, verify string contains 1 or 2, convert to int and return
        option = sc.nextInt();
        return option;
    }
}
